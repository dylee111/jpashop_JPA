package jpabook.jpashop.Repository.order.query;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderItemQueryDTO {

    private Long orderId;
    private String itemName;
    private int orderPrice;
    private int count;

    public OrderItemQueryDTO(Long orderId, String itemName, int orderPrice, int count) {
        this.orderId = orderId;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }
}
