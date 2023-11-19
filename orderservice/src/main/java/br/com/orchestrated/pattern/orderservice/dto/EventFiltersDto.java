package br.com.orchestrated.pattern.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventFiltersDto {

    private String orderId;
    private String transactionId;
}
