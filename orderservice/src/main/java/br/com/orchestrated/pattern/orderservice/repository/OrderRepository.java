package br.com.orchestrated.pattern.orderservice.repository;

import br.com.orchestrated.pattern.orderservice.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String> {
}
