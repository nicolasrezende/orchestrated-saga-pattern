package br.com.orchestrated.pattern.orchestratorservice.saga;

import static br.com.orchestrated.pattern.orchestratorservice.enums.EEventSource.*;
import static br.com.orchestrated.pattern.orchestratorservice.enums.ESagaStatus.*;
import static br.com.orchestrated.pattern.orchestratorservice.enums.ETopics.*;

public final class SagaHandler {

    private SagaHandler() {}

    public static final Object[][] SAGA_HANDLER = {
        { ORCHESTRATOR, SUCCESS, ORDER_VALIDATION_SUCCESS },
        { ORCHESTRATOR, FAIL, FINISH_FAIL },

        { ORDER_VALIDATION_SERVICE, ROLLBACK_PENDING, ORDER_VALIDATION_FAIL },
        { ORDER_VALIDATION_SERVICE, FAIL, FINISH_FAIL },
        { ORDER_VALIDATION_SERVICE, SUCCESS, ORDER_REGISTER_SUCCESS  },

        { ORDER_REGISTER_SERVICE, ROLLBACK_PENDING, ORDER_REGISTER_FAIL },
        { ORDER_REGISTER_SERVICE, FAIL, ORDER_VALIDATION_FAIL },
        { ORDER_REGISTER_SERVICE, SUCCESS, FINISH_SUCCESS }
    };

    public static final int EVENT_SOURCE_INDEX = 0;
    public static final int SAGA_STATUS_INDEX = 1;
    public static final int TOPIC_INDEX = 2;
}
