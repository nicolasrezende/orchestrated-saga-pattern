package br.com.orchestrated.pattern.ordervalidationservice.consumer;

import br.com.orchestrated.pattern.ordervalidationservice.service.OrderValidationService;
import br.com.orchestrated.pattern.ordervalidationservice.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class OrderValidationConsumer {

    private final OrderValidationService orderValidationService;
    private final JsonUtil jsonUtil;

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.order-validation-success}"
    )
    public void consumeOrderValidationSuccess(String payload) {
        log.info("Receiving event {} from order-validation-success topic", payload);
        var event = jsonUtil.toEvent(payload);
        this.orderValidationService.validateOrder(event);
    }

    @KafkaListener(
            groupId = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.topic.order-validation-fail}"
    )
    public void consumeOrderValidationFail(String payload) {
        log.info("Receiving event {} from order-validation-fail topic", payload);
        var event = jsonUtil.toEvent(payload);
        this.orderValidationService.realizeRollback(event);
    }
}
