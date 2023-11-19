package br.com.orchestrated.pattern.orderservice.dto;

import br.com.orchestrated.pattern.orderservice.enums.ETypeOperation;
import br.com.orchestrated.pattern.orderservice.model.Customer;
import br.com.orchestrated.pattern.orderservice.model.Order;
import br.com.orchestrated.pattern.orderservice.model.OrderDetail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    @NotBlank(message = "The costumer document is required.")
    @Size(min = 11, max = 11, message = "the costumer document must have a maximum of 11 characters.")
    @CPF(message = "The costumer document it is not a valid CPF.")
    private String customerDocument;

    @NotBlank(message = "The ticker symbol is required.")
    @Size(min = 3, max = 6, message = "The ticker symbol must be between 3 and 6 characters.")
    private String tickerSymbol;

    @NotNull(message = "The trade quantity is required.")
    private Integer tradeQuantity;

    @NotNull(message = "The operation is required.")
    private ETypeOperation operation;

    public Order toOrder() {
        var costumer = Customer
                .builder()
                .document(customerDocument)
                .build();

        var orderDetail = OrderDetail
                .builder()
                .tickerSymbol(tickerSymbol)
                .tradeQuantity(tradeQuantity)
                .operation(operation)
                .build();

        var order = Order
                .builder()
                .costumer(costumer)
                .orderDetail(orderDetail)
                .createdAt(LocalDateTime.now())
                .build();

        return order;
    }
}
