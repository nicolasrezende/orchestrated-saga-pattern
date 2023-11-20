package br.com.orchestrated.pattern.ordervalidationservice.service;

import br.com.orchestrated.pattern.ordervalidationservice.datasource.StockDataSource;
import br.com.orchestrated.pattern.ordervalidationservice.dto.EventDto;
import br.com.orchestrated.pattern.ordervalidationservice.dto.HistoryDto;
import br.com.orchestrated.pattern.ordervalidationservice.dto.OrderDetailDto;
import br.com.orchestrated.pattern.ordervalidationservice.dto.OrderDto;
import br.com.orchestrated.pattern.ordervalidationservice.dto.stock.StockDetailDto;
import br.com.orchestrated.pattern.ordervalidationservice.enums.ETypeOperation;
import br.com.orchestrated.pattern.ordervalidationservice.exception.ValidationException;
import br.com.orchestrated.pattern.ordervalidationservice.model.Customer;
import br.com.orchestrated.pattern.ordervalidationservice.model.Validation;
import br.com.orchestrated.pattern.ordervalidationservice.producer.KafkaProducer;
import br.com.orchestrated.pattern.ordervalidationservice.repository.ValidationRepository;
import br.com.orchestrated.pattern.ordervalidationservice.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static br.com.orchestrated.pattern.ordervalidationservice.enums.ESagaStatus.*;
import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Service
@AllArgsConstructor
public class OrderValidationService {

    private static final String CURRENT_SOURCE = "ORDER_VALIDATION_SERVICE";

    private final StockDataSource stockDataSource;
    private final ValidationRepository validationRepository;
    private final CustomerService customerService;
    private final CustomerPositionService customerPositionService;
    private final KafkaProducer kafkaProducer;
    private final JsonUtil jsonUtil;

    public void validateOrder(EventDto eventDto) {
        try {
            checkTransaction(eventDto);

            var stockDetailDto = getStockAndValidate(eventDto.getOrder().getOrderDetail().getTickerSymbol());
            sumTotalOrder(stockDetailDto, eventDto.getOrder().getOrderDetail());

            var customer = this.customerService
                    .findCustomerByDocument(eventDto.getOrder().getCostumer().getDocument());
            eventDto.getOrder().setCostumer(customer.toCustomerDto());

            validateTypeOperation(eventDto, customer);
            createValidationAndUpdatePosition(eventDto, customer);
            handleSuccess(eventDto);

        } catch(Exception ex) {
            log.error("Error trying to validate product: ", ex);
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

        if (validationRepository.existsByOrderIdAndTransactionId(eventDto.getOrderId(), eventDto.getTransactionId())) {
            throw new ValidationException("There's another transactionId for this validation.");
        }
    }

    private StockDetailDto getStockAndValidate(String tickerSymbol) {

        var stockDetail = this.stockDataSource
                .getStockByTickerSymbol(tickerSymbol)
                .orElseThrow(() -> new ValidationException("Stock not found by ticker symbol informed."));

        return stockDetail;
    }

    private void sumTotalOrder(StockDetailDto stock, OrderDetailDto orderDetailDto) {

        var total = stock.getPrice() * orderDetailDto.getTradeQuantity();
        orderDetailDto.setTotal(total);
    }

    private void validateTypeOperation(EventDto eventDto, Customer customer) {

        var orderDto = eventDto.getOrder();
        var operation = orderDto.getOrderDetail().getOperation();

        if (operation == ETypeOperation.BUY) {
            var availableValue =
                    this.customerService.customerIsAvailableValue(customer,
                            orderDto.getOrderDetail().getTotal());

            if (!availableValue) {
                throw new ValidationException("Customer does not have enough balance.");
            }

            customer.setAvailableValue(customer.getAvailableValue() - orderDto.getOrderDetail().getTotal());
        } else if (operation == ETypeOperation.SELL) {

            var customerPosition = this.customerPositionService
                    .findByTickerSymbolAndCustomerId(orderDto.getOrderDetail().getTickerSymbol(),
                            customer.getId());

            if (isEmpty(customerPosition)) {
                throw new ValidationException("Position not found by ticker symbol.");
            }

            if (!this.customerPositionService.
                    checkIfCustomerHasQuantityRequired(customerPosition,
                            orderDto.getOrderDetail().getTradeQuantity())) {
                throw new ValidationException("Insufficient quantity to sell.");
            }

            customer.setAvailableValue(customer.getAvailableValue() + orderDto.getOrderDetail().getTotal());
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private void createValidationAndUpdatePosition(EventDto eventDto, Customer customer) {

        var validation = Validation
                .builder()
                .orderId(eventDto.getOrderId())
                .transactionId(eventDto.getTransactionId())
                .success(true)
                .build();

        this.customerPositionService.updateCustomerPosition(eventDto, customer);
        this.customerService.save(customer);
        this.validationRepository.save(validation);
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
        addHistory(event, "Order are validated successfully!");
    }

    private void handleFailCurrentNotExecuted(EventDto event, String message) {
        event.setStatus(ROLLBACK_PENDING);
        event.setSource(CURRENT_SOURCE);
        addHistory(event, "Fail to validate order: ".concat(message));
    }

    public void realizeRollback(EventDto eventDto) {
        eventDto.setStatus(FAIL);
        eventDto.setSource(CURRENT_SOURCE);
        try {
            rollbackTransaction(eventDto);
            addHistory(eventDto, "Rollback executed successfully!");
        } catch(Exception ex) {
            addHistory(eventDto, "Rollback not executed for validation: ".concat(ex.getMessage()));
        }

        kafkaProducer.sendEvent(jsonUtil.toJson(eventDto));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private void rollbackTransaction(EventDto eventDto) {
        var validation = this.findByOrderIdAndTransactionId(eventDto.getOrderId(), eventDto.getTransactionId());
        validation.setSuccess(false);

        var customer = this.customerService.findCustomerByDocument(eventDto.getOrder().getCostumer().getDocument());
        var customerPosition = this.customerPositionService.rollbackCustomerPosition(eventDto, customer);

        this.customerPositionService.save(customerPosition);
        this.customerService.save(customer);
        this.validationRepository.save(validation);
    }

    private Validation findByOrderIdAndTransactionId(String orderId, String transactionId) {
        return this.validationRepository
                .findByOrderIdAndTransactionId(orderId, transactionId)
                .orElseThrow(() -> new ValidationException("Validation not found by orderID and transactionID"));
    }
}
