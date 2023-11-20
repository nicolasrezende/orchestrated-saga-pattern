package br.com.orchestrated.pattern.orderregisterservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    @Id
    private String id;
    private CustomerDto costumer;
    private OrderDetailDto orderDetail;
    private LocalDateTime createdAt;
}
