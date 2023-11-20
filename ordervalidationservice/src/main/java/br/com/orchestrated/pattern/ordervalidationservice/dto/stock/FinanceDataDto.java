package br.com.orchestrated.pattern.ordervalidationservice.dto.stock;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinanceDataDto {

    private String status;

    @JsonProperty("request_id")
    private String requestId;

    private StockDto data;
}
