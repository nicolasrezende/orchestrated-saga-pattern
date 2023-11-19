package br.com.orchestrated.pattern.orderservice.controller;

import br.com.orchestrated.pattern.orderservice.dto.OrderDto;
import br.com.orchestrated.pattern.orderservice.model.Order;
import br.com.orchestrated.pattern.orderservice.service.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> create(@Valid @RequestBody OrderDto orderDto) {
        var order = this.orderService.createOrder(orderDto);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }
}
