package br.com.orchestrated.pattern.ordervalidationservice.model;

import br.com.orchestrated.pattern.ordervalidationservice.dto.CustomerDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String document;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private double availableValue;

    public CustomerDto toCustomerDto() {
        return CustomerDto
                .builder()
                .name(name)
                .document(document)
                .email(email)
                .build();
    }
}
