package br.com.orchestrated.pattern.orderservice.service;

import br.com.orchestrated.pattern.orderservice.dto.EventFiltersDto;
import br.com.orchestrated.pattern.orderservice.exceptions.ValidationException;
import br.com.orchestrated.pattern.orderservice.model.Event;
import br.com.orchestrated.pattern.orderservice.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.xml.stream.EventFilter;

import java.time.LocalDateTime;

import static org.springframework.util.ObjectUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public void notifyEnding(Event event) {
        event.setOrderId(event.getOrderId());
        event.setCreatedAt(LocalDateTime.now());
        save(event);
        log.info("Order {} with saga notified! Transaction: {}", event.getOrderId(), event.getTransactionId());
    }

    public Event save(Event event) {
        return this.eventRepository.save(event);
    }

    public Event findByFilters(EventFiltersDto filtersDto) {
        validateFilters(filtersDto);

        if (!isEmpty(filtersDto.getOrderId())) {
            return findEventByOrderId(filtersDto.getOrderId());
        }

        return findEventByTransactionId(filtersDto.getTransactionId());
    }

    private void validateFilters(EventFiltersDto filtersDto) {
        if (isEmpty(filtersDto.getOrderId()) && isEmpty(filtersDto.getTransactionId())) {
            throw new ValidationException("OrderID or TransactionID must be informed.");
        }
    }

    private Event findEventByOrderId(String orderId) {
        return this.eventRepository
                .findTop1ByOrderIdOrderByCreatedAtDesc(orderId)
                .orElse(null);
    }

    private Event findEventByTransactionId(String transactionId) {
         return this.eventRepository
                .findTop1ByTransactionIdOrderByCreatedAtDesc(transactionId)
                .orElse(null);
    }
}
