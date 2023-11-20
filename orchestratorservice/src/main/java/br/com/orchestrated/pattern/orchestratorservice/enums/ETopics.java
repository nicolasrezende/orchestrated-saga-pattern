package br.com.orchestrated.pattern.orchestratorservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ETopics {

    START_SAGA("start-saga"),
    BASE_ORCHESTRATOR("orchestrator"),
    FINISH_SUCCESS("finish-success"),
    FINISH_FAIL("finish-fail"),
    ORDER_VALIDATION_SUCCESS("order-validation-success"),
    ORDER_VALIDATION_FAIL("order-validation-fail"),
    ORDER_REGISTER_SUCCESS("order-register-success"),
    ORDER_REGISTER_FAIL("order-register-fail"),
    NOTIFY_ENDING("notify-ending");

    private final String topic;
}
