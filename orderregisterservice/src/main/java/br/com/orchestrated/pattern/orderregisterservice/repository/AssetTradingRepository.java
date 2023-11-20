package br.com.orchestrated.pattern.orderregisterservice.repository;

import br.com.orchestrated.pattern.orderregisterservice.model.AssetTrading;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetTradingRepository extends JpaRepository<AssetTrading, Integer> {

    boolean existsByOrderIdAndTransactionId(String orderId, String transactionId);
    Optional<AssetTrading> findByOrderIdAndTransactionId(String orderId, String transactionId);
}
