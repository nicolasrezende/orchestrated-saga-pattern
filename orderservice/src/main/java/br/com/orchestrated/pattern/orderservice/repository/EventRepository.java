package br.com.orchestrated.pattern.orderservice.repository;

import br.com.orchestrated.pattern.orderservice.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface EventRepository extends MongoRepository<Event, String> {

    Optional<Event> findTop1ByOrderIdOrderByCreatedAtDesc(String orderId);
    Optional<Event> findTop1ByTransactionIdOrderByCreatedAtDesc(String transactionId);
}
