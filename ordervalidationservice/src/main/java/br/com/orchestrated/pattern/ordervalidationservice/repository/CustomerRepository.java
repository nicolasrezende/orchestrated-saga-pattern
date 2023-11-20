package br.com.orchestrated.pattern.ordervalidationservice.repository;

import br.com.orchestrated.pattern.ordervalidationservice.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    Optional<Customer> findByDocument(String document);
}
