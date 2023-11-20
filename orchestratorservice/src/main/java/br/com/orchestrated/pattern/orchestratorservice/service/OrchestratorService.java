package br.com.orchestrated.pattern.orchestratorservice.service;

import br.com.orchestrated.pattern.orchestratorservice.dto.EventDto;
import br.com.orchestrated.pattern.orchestratorservice.dto.HistoryDto;
import br.com.orchestrated.pattern.orchestratorservice.enums.ETopics;
import br.com.orchestrated.pattern.orchestratorservice.producer.SagaOrchestratorProducer;
import br.com.orchestrated.pattern.orchestratorservice.saga.SagaExecutionController;
import br.com.orchestrated.pattern.orchestratorservice.utils.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static br.com.orchestrated.pattern.orchestratorservice.enums.EEventSource.ORCHESTRATOR;
import static br.com.orchestrated.pattern.orchestratorservice.enums.ESagaStatus.FAIL;
import static br.com.orchestrated.pattern.orchestratorservice.enums.ESagaStatus.SUCCESS;

@Slf4j
@Service
@AllArgsConstructor
public class OrchestratorService {

    private final SagaOrchestratorProducer producer;
    private final SagaExecutionController sagaExecutionController;
    private final JsonUtil jsonUtil;

    public void startSaga(EventDto event) {
        event.setSource(ORCHESTRATOR);
        event.setStatus(SUCCESS);
        var topic = getTopic(event);
        log.info("SAGA STARTED");
        addHistory(event, "Saga started!");
        sendToProducerWithTopic(event, topic);
    }

    public void finishSagaSuccess(EventDto event) {
        event.setSource(ORCHESTRATOR);
        event.setStatus(SUCCESS);
        log.info("SAGA FINISHED SUCCESSFULLY FOR EVENT {}!", event.getId());
        addHistory(event, "Saga finished successfully!");
        notifyFinishedSaga(event);
    }

    public void finishSagaFail(EventDto event) {
        event.setSource(ORCHESTRATOR);
        event.setStatus(FAIL);
        log.info("SAGA FINISHED SUCCESSFULLY FOR EVENT {}!", event.getId());
        addHistory(event, "Saga finished successfully!");
        notifyFinishedSaga(event);
    }

    public void continueSaga(EventDto event) {
        var topic = getTopic(event);
        log.info("SAGA CONTINUING FOR EVENT {}", event.getId());
        sendToProducerWithTopic(event, topic);
    }

    private ETopics getTopic(EventDto event) {
        return sagaExecutionController.getNextTopic(event);
    }

    private void addHistory(EventDto event, String message) {
        var history = HistoryDto
                .builder()
                .source(event.getSource())
                .status(event.getStatus())
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();
        event.addToHistory(history);
    }

    private void sendToProducerWithTopic(EventDto event, ETopics topic) {
        producer.sendEvent(jsonUtil.toJson(event), topic.getTopic());
    }

    private void notifyFinishedSaga(EventDto event) {
        producer.sendEvent(jsonUtil.toJson(event), ETopics.NOTIFY_ENDING.getTopic());
    }
}
