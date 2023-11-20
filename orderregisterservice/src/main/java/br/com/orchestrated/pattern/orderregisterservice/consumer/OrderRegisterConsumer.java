package br.com.orchestrated.pattern.orderregisterservice.consumer;
import br.com.orchestrated.pattern.orderregisterservice.service.AssetTradingService;
import br.com.orchestrated.pattern.orderregisterservice.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class OrderRegisterConsumer {

    private final AssetTradingService assetTradingService;
    private final JsonUtil jsonUtil;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.order-register-success}"
    )
    public void consumeOrderValidationSuccess(String payload) {
        log.info("Receiving event {} from order-validation-success topic", payload);
        var event = jsonUtil.toEvent(payload);
        this.assetTradingService.registerTrading(event);
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.order-register-fail}"
    )
    public void consumeOrderValidationFail(String payload) {
        log.info("Receiving event {} from order-validation-fail topic", payload);
        var event = jsonUtil.toEvent(payload);
        this.assetTradingService.realizeRollback(event);
    }
}
