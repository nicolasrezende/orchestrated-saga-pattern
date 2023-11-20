package br.com.orchestrated.pattern.orderregisterservice.utils;

import br.com.orchestrated.pattern.orderregisterservice.dto.EventDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JsonUtil {

    private final ObjectMapper objectMapper;

    public String toJson(Object object) {
        try {
            return this.objectMapper.writeValueAsString(object);
        } catch (Exception ex) {
            return "";
        }
    }

    public EventDto toEvent(String json) {
        try {
            return this.objectMapper.readValue(json, EventDto.class);
        } catch (Exception ex) {
            return null;
        }
    }
}
