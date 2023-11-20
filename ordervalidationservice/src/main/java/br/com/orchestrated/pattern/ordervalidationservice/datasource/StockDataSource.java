package br.com.orchestrated.pattern.ordervalidationservice.datasource;

import br.com.orchestrated.pattern.ordervalidationservice.dto.stock.FinanceDataDto;
import br.com.orchestrated.pattern.ordervalidationservice.dto.stock.StockDetailDto;
import br.com.orchestrated.pattern.ordervalidationservice.dto.stock.StockDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Component
public class StockDataSource {

    @Value("${api.finance.uri}")
    private String baseUri;

    @Value("${api.finance.key}")
    private String apiKey;

    public Optional<StockDetailDto> getStockByTickerSymbol(String tickerSymbol) {

        String url = String.format("%s/search?query=%s&language=en", baseUri, tickerSymbol);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-RapidAPI-Key", apiKey);

        HttpEntity<String> entity = new HttpEntity<>("", headers);

        ResponseEntity<FinanceDataDto> response
                = restTemplate.exchange(url, HttpMethod.GET, entity, FinanceDataDto.class);

        FinanceDataDto financeDataDto = response.getBody();

        Optional<StockDetailDto> stockDtoOptional;
        if (financeDataDto.getData().getStock().isEmpty()) {
            log.info("Stock not found for ticker symbol {}", tickerSymbol);
            stockDtoOptional = Optional.ofNullable(null);
            return stockDtoOptional;
        }

        stockDtoOptional = Optional.ofNullable(financeDataDto.getData().getStock().stream().findFirst().get());
        return stockDtoOptional;
    }
}
