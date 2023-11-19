package br.com.orchestrated.pattern.orderservice.model;

import br.com.orchestrated.pattern.orderservice.enums.ETypeOperation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {

    private String tickerSymbol;
    private Integer tradeQuantity;
    private ETypeOperation operation;
    private double total;
}
