package br.com.orchestrated.pattern.orderservice.service;

import br.com.orchestrated.pattern.orderservice.dto.OrderDto;
import br.com.orchestrated.pattern.orderservice.model.Costumer;
import br.com.orchestrated.pattern.orderservice.model.Event;
import br.com.orchestrated.pattern.orderservice.model.Order;
import br.com.orchestrated.pattern.orderservice.model.OrderDetail;
import br.com.orchestrated.pattern.orderservice.producer.EventProducer;
import br.com.orchestrated.pattern.orderservice.repository.OrderRepository;
import br.com.orchestrated.pattern.orderservice.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final String TRANSACTION_ID_PATTERN = "%s_%s";

    private final OrderRepository orderRepository;
    private final EventService eventService;
    private final EventProducer producer;
    private final JsonUtil jsonUtil;

    public Order createOrder(OrderDto orderDto) {
        var order = orderDto.toOrder();

        this.orderRepository.save(order);
        var event = this.createEvent(order);

        this.producer.sendEvent(jsonUtil.toJson(event));
        return order;
    }

    private Event createEvent(Order order) {
        var event = Event
                .builder()
                .orderId(order.getId())
                .order(order)
                .transactionId(
                        String.format(TRANSACTION_ID_PATTERN, Instant.now().toEpochMilli(), UUID.randomUUID())
                )
                .createdAt(LocalDateTime.now())
                .build();

        this.eventService.save(event);
        return event;
    }
}
