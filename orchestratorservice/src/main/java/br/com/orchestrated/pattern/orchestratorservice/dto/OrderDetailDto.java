package br.com.orchestrated.pattern.orchestratorservice.dto;

import br.com.orchestrated.pattern.orchestratorservice.enums.ETypeOperation;
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
    private double price;
    private double total;
}

