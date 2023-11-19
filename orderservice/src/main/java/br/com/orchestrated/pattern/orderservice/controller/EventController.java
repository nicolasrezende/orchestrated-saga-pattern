package br.com.orchestrated.pattern.orderservice.controller;

import br.com.orchestrated.pattern.orderservice.dto.EventFiltersDto;
import br.com.orchestrated.pattern.orderservice.model.Event;
import br.com.orchestrated.pattern.orderservice.service.EventService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/event")
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<?> findByFilters(EventFiltersDto filtersDto) {
        Event event = this.eventService.findByFilters(filtersDto);

        if (event == null) {
            return new ResponseEntity<>("Event not found.", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(event, HttpStatus.OK);
    }
}
