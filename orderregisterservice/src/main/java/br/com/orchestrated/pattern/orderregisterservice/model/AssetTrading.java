package br.com.orchestrated.pattern.orderregisterservice.model;

import br.com.orchestrated.pattern.orderregisterservice.enums.ETypeOperation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "asset_tradings")
public class AssetTrading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private String tickerSymbol;

    @Column(nullable = false)
    private String participantDocument;

    @Column(nullable = false)
    private String participantEmail;

    @Column(nullable = false)
    private Integer tradeQuantity;

    @Column(nullable = false)
    private double price;

    @Column(nullable = false)
    private double total;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ETypeOperation operation;

    @Column(nullable = false)
    private boolean executed;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        var now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
