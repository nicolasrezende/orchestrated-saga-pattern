package br.com.orchestrated.pattern.orchestratorservice.dto;

import br.com.orchestrated.pattern.orchestratorservice.enums.EEventSource;
import br.com.orchestrated.pattern.orchestratorservice.enums.ESagaStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.ObjectUtils.isEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {

    private String id;
    private String transactionId;
    private String orderId;
    private OrderDto order;
    private EEventSource source;
    private ESagaStatus status;
    private List<HistoryDto> histories;
    private LocalDateTime createdAt;

    public void addToHistory(HistoryDto history) {
        if (isEmpty(histories)) {
            histories = new ArrayList<>();
        }

        histories.add(history);
    }
}