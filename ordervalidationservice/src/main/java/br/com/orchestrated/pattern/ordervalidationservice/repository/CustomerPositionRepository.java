package br.com.orchestrated.pattern.ordervalidationservice.repository;

import br.com.orchestrated.pattern.ordervalidationservice.model.CustomerPosition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerPositionRepository extends JpaRepository<CustomerPosition, Integer> {

    Optional<CustomerPosition> findByTickerSymbolAndCustomerId(String tickerSymbol, Integer customerId);
}
