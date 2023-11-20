package br.com.orchestrated.pattern.ordervalidationservice.service;

import br.com.orchestrated.pattern.ordervalidationservice.exception.ValidationException;
import br.com.orchestrated.pattern.ordervalidationservice.model.Customer;
import br.com.orchestrated.pattern.ordervalidationservice.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer findCustomerByDocument(String document) {

        return this.customerRepository
                .findByDocument(document)
                .orElseThrow(() -> new ValidationException("Customer not found by document informed."));
    }

    public boolean customerIsAvailableValue(Customer customer, double totalOrder) {
        return customer.getAvailableValue() >= totalOrder;
    }

    public Customer save(Customer customer) {
        return this.customerRepository.save(customer);
    }
}
