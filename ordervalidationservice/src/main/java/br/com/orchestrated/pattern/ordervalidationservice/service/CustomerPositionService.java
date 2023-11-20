package br.com.orchestrated.pattern.ordervalidationservice.service;

import br.com.orchestrated.pattern.ordervalidationservice.dto.EventDto;
import br.com.orchestrated.pattern.ordervalidationservice.enums.ETypeOperation;
import br.com.orchestrated.pattern.ordervalidationservice.model.Customer;
import br.com.orchestrated.pattern.ordervalidationservice.model.CustomerPosition;
import br.com.orchestrated.pattern.ordervalidationservice.repository.CustomerPositionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import static org.springframework.util.ObjectUtils.isEmpty;

@Service
@AllArgsConstructor
public class CustomerPositionService {

    private final CustomerPositionRepository customerPositionRepository;

    public CustomerPosition findByTickerSymbolAndCustomerId(String tickerSymbol, Integer customerId) {
        return this.customerPositionRepository
                .findByTickerSymbolAndCustomerId(tickerSymbol, customerId)
                .orElse(null);
    }

    public boolean checkIfCustomerHasQuantityRequired(CustomerPosition customerPosition, Integer quantity) {
        return customerPosition.getQuantity() >= quantity;
    }

    public CustomerPosition updateCustomerPosition(EventDto eventDto, Customer customer) {
        var customerPosition = this
                .findByTickerSymbolAndCustomerId(eventDto.getOrder().getOrderDetail().getTickerSymbol(),
                        customer.getId());

        var orderDetail = eventDto.getOrder().getOrderDetail();

        if (isEmpty(customerPosition)) {
            customerPosition = CustomerPosition
                    .builder()
                    .customer(customer)
                    .tickerSymbol(orderDetail.getTickerSymbol())
                    .quantity(orderDetail.getTradeQuantity())
                    .total(orderDetail.getTotal())
                    .build();

            return this.save(customerPosition);
        }

        var operation = eventDto.getOrder().getOrderDetail().getOperation();
        if (operation == ETypeOperation.BUY) {
            customerPosition.setQuantity(customerPosition.getQuantity() + orderDetail.getTradeQuantity());
            customerPosition.setTotal(customerPosition.getTotal() + orderDetail.getTotal());
        }

        if (operation == ETypeOperation.SELL) {
            customerPosition.setQuantity(customerPosition.getQuantity() - orderDetail.getTradeQuantity());
            customerPosition.setTotal(customerPosition.getTotal() - orderDetail.getTotal());
        }

        return this.save(customerPosition);
    }

    public CustomerPosition rollbackCustomerPosition(EventDto eventDto, Customer customer) {
        var orderDetail = eventDto.getOrder().getOrderDetail();
        var operation = orderDetail.getOperation();
        var totalOrder = orderDetail.getTotal();

        var customerPosition = this.findByTickerSymbolAndCustomerId(
                orderDetail.getTickerSymbol(),
                customer.getId()
        );

        if (operation == ETypeOperation.BUY) {
            customer.setAvailableValue(customer.getAvailableValue() + totalOrder);
            customerPosition.setQuantity(customerPosition.getQuantity() - orderDetail.getTradeQuantity());
            customerPosition.setTotal(customerPosition.getTotal() - orderDetail.getTotal());
        } else if (operation == ETypeOperation.SELL) {
            customer.setAvailableValue(customer.getAvailableValue() - totalOrder);
            customerPosition.setQuantity(customerPosition.getQuantity() + orderDetail.getTradeQuantity());
            customerPosition.setTotal(customerPosition.getTotal() + orderDetail.getTotal());
        }

        return customerPosition;
    }

    public CustomerPosition save(CustomerPosition customerPosition) {
        return this.customerPositionRepository.save(customerPosition);
    }
}
