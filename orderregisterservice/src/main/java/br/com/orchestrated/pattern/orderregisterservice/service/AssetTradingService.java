package br.com.orchestrated.pattern.orderregisterservice.service;

import br.com.orchestrated.pattern.orderregisterservice.dto.EmailDto;
import br.com.orchestrated.pattern.orderregisterservice.dto.EventDto;
import br.com.orchestrated.pattern.orderregisterservice.dto.HistoryDto;
import br.com.orchestrated.pattern.orderregisterservice.dto.OrderDto;
import br.com.orchestrated.pattern.orderregisterservice.exeception.ValidationException;
import br.com.orchestrated.pattern.orderregisterservice.model.AssetTrading;
import br.com.orchestrated.pattern.orderregisterservice.producer.KafkaProducer;
import br.com.orchestrated.pattern.orderregisterservice.repository.AssetTradingRepository;
import br.com.orchestrated.pattern.orderregisterservice.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static br.com.orchestrated.pattern.orderregisterservice.enums.ESagaStatus.*;
import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
@AllArgsConstructor
public class AssetTradingService {

    private static final String CURRENT_SOURCE = "ORDER_REGISTER_SERVICE";

    private final AssetTradingRepository assetTradingRepository;
    private final EmailService emailService;
    private final KafkaProducer kafkaProducer;
    private final JsonUtil jsonUtil;

    public void registerTrading(EventDto eventDto) {
        try {
            checkTransaction(eventDto);
            registerAssetTrading(eventDto, true);
            sendEmailCustomer(eventDto);
            handleSuccess(eventDto);
        } catch (Exception ex) {
            log.error("Error trying to register order: ", ex);
            handleFailCurrentNotExecuted(eventDto, ex.getMessage());
        }

        kafkaProducer.sendEvent(jsonUtil.toJson(eventDto));
    }

    private void validateTransactionIdAndOrderId(EventDto eventDto) {

        if (isEmpty(eventDto.getTransactionId()) || isEmpty(eventDto.getOrderId())) {
            throw new ValidationException("OrderID and TransactionID must be informed!");
        }
    }

    private void validateOrderDetailAndCustomer(OrderDto orderDto) {

        if (isEmpty(orderDto.getOrderDetail()) || isEmpty(orderDto.getCostumer())) {
            throw new ValidationException("Order Detail and Customer must be informed!");
        }
    }

    private void checkTransaction(EventDto eventDto) {
        validateTransactionIdAndOrderId(eventDto);
        validateOrderDetailAndCustomer(eventDto.getOrder());

        if (assetTradingRepository.existsByOrderIdAndTransactionId(eventDto.getOrderId(), eventDto.getTransactionId())) {
            throw new ValidationException("There's another transactionId for this validation.");
        }
    }

    private void registerAssetTrading(EventDto eventDto, boolean executed) {
        var assetTrading = AssetTrading
                .builder()
                .orderId(eventDto.getOrderId())
                .transactionId(eventDto.getTransactionId())
                .participantDocument(eventDto.getOrder().getCostumer().getDocument())
                .participantEmail(eventDto.getOrder().getCostumer().getEmail())
                .tickerSymbol(eventDto.getOrder().getOrderDetail().getTickerSymbol())
                .tradeQuantity(eventDto.getOrder().getOrderDetail().getTradeQuantity())
                .price(eventDto.getOrder().getOrderDetail().getPrice())
                .total(eventDto.getOrder().getOrderDetail().getTotal())
                .operation(eventDto.getOrder().getOrderDetail().getOperation())
                .executed(executed)
                .build();

        this.assetTradingRepository.save(assetTrading);
    }

    private void sendEmailCustomer(EventDto eventDto) {
        var emailDto = EmailDto
                .builder()
                .to(eventDto.getOrder().getCostumer().getEmail())
                .subject("Order Executed")
                .text(String.format("Order %s executed successfully!", eventDto.getOrderId()))
                .build();

        this.emailService.sendEmail(emailDto);
        eventDto.setStatus(SUCCESS);
        eventDto.setSource(CURRENT_SOURCE);
        addHistory(eventDto, "Email successfully sent!");
    }

    private void addHistory(EventDto event, String message) {
        var history = HistoryDto
                .builder()
                .source(event.getSource())
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        event.addToHistory(history);
    }

    private void handleSuccess(EventDto event) {
        event.setStatus(SUCCESS);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Order register successfully!");
    }

    private void handleFailCurrentNotExecuted(EventDto event, String message) {
        event.setStatus(ROLLBACK_PENDING);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Fail to register order: ".concat(message));
    }

    public void realizeRollback(EventDto eventDto) {
        eventDto.setStatus(FAIL);
        eventDto.setSource(CURRENT_SOURCE);
        try {
            rollbackOrderRegister(eventDto);
            addHistory(eventDto, "Rollback executed for order register!");
        } catch (Exception ex) {
            addHistory(eventDto, "Rollback not executed for register: ".concat(ex.getMessage()));
        }

        kafkaProducer.sendEvent(jsonUtil.toJson(eventDto));
    }

    private void rollbackOrderRegister(EventDto eventDto) {
        var assetTrading = this.findByOrderIdAndTransaction(eventDto.getOrderId(), eventDto.getTransactionId());
        assetTrading.setExecuted(false);
        this.assetTradingRepository.save(assetTrading);
    }

    private AssetTrading findByOrderIdAndTransaction(String orderId, String transactionId) {
        return this.assetTradingRepository
                .findByOrderIdAndTransactionId(orderId, transactionId)
                .orElseThrow(() -> new ValidationException("Order register not found for orderId and transactionId"));
    }
}
