package br.com.orchestrated.pattern.ordervalidationservice.dto.stock;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockDto {

    private List<StockDetailDto> stock;
}
