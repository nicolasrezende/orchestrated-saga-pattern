package br.com.orchestrated.pattern.orchestratorservice.saga;

import br.com.orchestrated.pattern.orchestratorservice.dto.EventDto;
import br.com.orchestrated.pattern.orchestratorservice.enums.ETopics;
import br.com.orchestrated.pattern.orchestratorservice.exception.ValidationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static br.com.orchestrated.pattern.orchestratorservice.saga.SagaHandler.*;
import static java.lang.String.format;
import static org.springframework.util.ObjectUtils.isEmpty;

@Slf4j
@Component
@AllArgsConstructor
public class SagaExecutionController {

    private static final String SAGA_LOG_ID = "ORDER  ID: %s | TRANSACTION ID %s | EVENT ID %s";

    public ETopics getNextTopic(EventDto event) {
        if (isEmpty(event.getSource()) || isEmpty(event.getStatus())) {
            throw new ValidationException("Source and status must be informed.");
        }

        var topic = findTopicBySourceAndStatus(event);
        logCurrentSaga(event, topic);
        return topic;
    }

    private ETopics findTopicBySourceAndStatus(EventDto event) {
        return (ETopics) Arrays.stream(SAGA_HANDLER)
                .filter(row -> isEventSourceAndStatusValid(event, row))
                .map(i -> i[TOPIC_INDEX])
                .findFirst()
                .orElseThrow(() -> new ValidationException("Topic not found!"));
    }

    private boolean isEventSourceAndStatusValid(EventDto event, Object[] row) {
        var source = row[EVENT_SOURCE_INDEX];
        var status = row[SAGA_STATUS_INDEX];

        return source.equals(event.getSource()) && status.equals(event.getStatus());
    }

    private void logCurrentSaga(EventDto event, ETopics topic) {
        var sagaId = createSagaId(event);
        var source = event.getSource();
        switch (event.getStatus()) {
            case SUCCESS -> log.info("### CURRENT SAGA: {} | SUCCESS | NEXT TOPIC {} | {}",
                    source, topic, sagaId);
            case ROLLBACK_PENDING -> log.info("### CURRENT SAGA: {} | ROLLBACK_PENDING | NEXT TOPIC {} | {}",
                    source, topic, sagaId);
            case FAIL -> log.info("### CURRENT SAGA: {} | FAIL | NEXT TOPIC {} | {}",
                    source, topic, sagaId);
        }
    }

    private String createSagaId(EventDto event) {
        return format(SAGA_LOG_ID, event.getOrder().getId(), event.getTransactionId(), event.getId());
    }
}
