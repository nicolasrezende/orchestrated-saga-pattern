package br.com.orchestrated.pattern.ordervalidationservice.dto;

import br.com.orchestrated.pattern.ordervalidationservice.enums.ETypeOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDto {

    private String tickerSymbol;
    private Integer tradeQuantity;
    private ETypeOperation operation;
    private double total;
}

