package br.com.orchestrated.pattern.ordervalidationservice.dto.stock;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDetailDto {

    private String symbol;
    private String name;
    private String type;
    private double price;
    private String currency;

    @JsonProperty("country_code")
    private String countryCode;
}
