package br.com.orchestrated.pattern.orderregisterservice.dto;

import br.com.orchestrated.pattern.orderregisterservice.enums.ESagaStatus;
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

    private String source;
    private ESagaStatus status;
    private String message;
    private LocalDateTime createdAt;
}
