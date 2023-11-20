package br.com.orchestrated.pattern.orchestratorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private String id;
    private CustomerDto costumer;
    private OrderDetailDto orderDetail;
    private LocalDateTime createdAt;
}
