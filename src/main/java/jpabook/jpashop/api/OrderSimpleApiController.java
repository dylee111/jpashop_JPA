package jpabook.jpashop.api;

import jpabook.jpashop.Repository.order.simplequery.OrderSimpleQueryDTO;
import jpabook.jpashop.Repository.order.simplequery.OrderSimpleQueryService;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.service.OrderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/*
* xToOne (ManyToOne / OneToOne)
* Order
* Order -> Member
* Order -> Delivery
* */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderService orderService;
    private final OrderSimpleQueryService orderSimpleQueryService;

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDTO> orderV2() {
        // Order 2건
        // N + 1 문제 발생 -> 1(findAllOrder()) + N(회원 조회) + N(배송 조회)
        List<Order> orders = orderService.findAllOrder();
        // 엔티티를 DTO로 변환
        List<SimpleOrderDTO> result = orders.stream()
                .map(order -> new SimpleOrderDTO(order)).collect(Collectors.toList());
        return result;
    }

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDTO> orderV3() {

        //join fetch 사용
        List<Order> orders = orderService.findAllWithMemberAndDelivery();
        List<SimpleOrderDTO> result = orders.stream()
                .map(order -> new SimpleOrderDTO(order))
                .collect(Collectors.toList());

        return result;
    }

    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDTO> orderV4() {
        return orderSimpleQueryService.findAllOrderDTO();
    }

        @Data
        static class SimpleOrderDTO {
            private Long orderId;
            private String name;
            private OrderStatus orderStatus;
            private LocalDateTime orderDate;
            private Address address;

            public SimpleOrderDTO(Order order) {
                orderId = order.getId();
                name = order.getMember().getName(); // Member LAZY 초기화
                orderStatus = order.getStatus();
                orderDate = order.getOrderDate();
                address = order.getDelivery().getAddress(); // Delivery LAZY 초기화
            }
        } // SimpleOrderDTO
    }
