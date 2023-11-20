package br.com.orchestrated.pattern.orchestratorservice.dto;

import br.com.orchestrated.pattern.orchestratorservice.enums.EEventSource;
import br.com.orchestrated.pattern.orchestratorservice.enums.ESagaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryDto {

    private EEventSource source;
    private ESagaStatus status;
    private String message;
    private LocalDateTime createdAt;
}
