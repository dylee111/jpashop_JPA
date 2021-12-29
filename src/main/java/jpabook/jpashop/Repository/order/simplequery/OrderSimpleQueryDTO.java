package jpabook.jpashop.Repository.order.simplequery;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderSimpleQueryDTO {
    private Long orderId;
    private String name;
    private OrderStatus orderStatus;
    private LocalDateTime orderDate;
    private Address address;

    public OrderSimpleQueryDTO(Long orderId, String name, OrderStatus orderStatus, LocalDateTime orderDate, Address address) {
        this.orderId = orderId;
        this.name = name;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.address = address;
    }
}
