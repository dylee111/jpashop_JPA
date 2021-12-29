package jpabook.jpashop.api;

import jpabook.jpashop.Repository.order.query.OrderFlatDTO;
import jpabook.jpashop.Repository.order.query.OrderItemQueryDTO;
import jpabook.jpashop.Repository.order.query.OrderQueryDTO;
import jpabook.jpashop.Repository.order.query.OrderQueryService;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.service.OrderService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderService orderService;
    private final OrderQueryService orderQueryService;

    /*
     * V1 - 엔티티를 직접 반환
     * */
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> allOrder = orderService.findAllOrder();
        for (Order order : allOrder) {
            order.getMember().getName(); // 프록시 강제 초기화
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                orderItem.getItem().getName();
            }
        }
        return allOrder;
    }

    /*
     * V2 - 엔티티를 DTO로 변환
     * */
    @GetMapping("/api/v2/orders")
    public FindOrderResult ordersV2() {
        List<Order> orders = orderService.findAllOrder();
        List<OrderDTO> result = orders.stream()
                .map(order -> new OrderDTO(order))
                .collect(toList());

        return new FindOrderResult(result.size(), result);
    }

    /*
     * V3 - 엔티티를 DTO로 변환 + 페치 조인 사용
     * V2와 거의 동일한 코드이지만 성능 차이 큼
     * */
    @GetMapping("/api/v3/orders")
    public FindOrderResult ordersV3() {
        List<Order> orders = orderService.findAllWithItem();
        List<OrderDTO> result = orders.stream()
                .map(order -> new OrderDTO(order))
                .collect(toList());

        return new FindOrderResult(result);
    }
    /*
     * V3 - 엔티티를 DTO로 변환 + 페치 조인 사용 + 페이징
     * Lazy Loading은 전부 페치 조인 설정
     * */
    @GetMapping("/api/v3.1/orders")
    public FindOrderResult ordersV3_paging(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                           @RequestParam(value = "limit", defaultValue = "100") int limit) {

        List<Order> orders = orderService.findAllWithMemberAndDelivery(offset, limit);
        List<OrderDTO> result = orders.stream()
                .map(order -> new OrderDTO(order))
                .collect(toList());

        return new FindOrderResult(result);
    }

    /*
     * V4 - JPA에서 DTO 직접 조회
     * */
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDTO> ordersV4() {
        return orderQueryService.findOrders();
    }

    /*
     * V5 - JPA에서 DTO 직접 조회 (성능 최적화)
     * */
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDTO> ordersV5() {
        return orderQueryService.findAllByDtoOptimization();
    }

    /*
     * V6 - JPA에서 DTO 직접 조회 (플랫 데이터 최적화)
     * */
    @GetMapping("/api/v6/orders")
    public List<OrderQueryDTO> ordersV6() {
        List<OrderFlatDTO> flats = orderQueryService.findAllByDtoFlat();

        return flats.stream()
                .collect(groupingBy(orderFlatDTO -> new OrderQueryDTO(orderFlatDTO.getOrderId(),
                                orderFlatDTO.getName(), orderFlatDTO.getOrderDate(), orderFlatDTO.getOrderStatus(), orderFlatDTO.getAddress()),
                        mapping(orderFlatDTO -> new OrderItemQueryDTO(orderFlatDTO.getOrderId(),
                                orderFlatDTO.getItemName(), orderFlatDTO.getOrderPrice(), orderFlatDTO.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDTO(e.getKey().getOrderId(),
                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),
                        e.getKey().getAddress(), e.getValue()))
                .collect(toList());
    }

    @Getter
    static class OrderDTO {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDTO> orderItemDTOS; // DTO로 감싼다는 의미는 완전히 엔티티에 대한 의존을 끊어야 한다는 의미

        public OrderDTO(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItemDTOS = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDTO(orderItem))
                    .collect(toList());
        }
    } // OrderDTO

    @Getter
    static class OrderItemDTO {
        private String itemName; // 제품명
        private int orderPrice;  // 주문 가격
        private int count;       // 주문 수량

        public OrderItemDTO(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getOrderPrice();
        }
    } // OrderItemDTO

    @Getter
    static class FindOrderResult<T> {
        private int count;
        private T data;

        public FindOrderResult(int count, T data) {
            this.count = count;
            this.data = data;
        }

        public FindOrderResult(T data) {
            this.data = data;
        }
    }

}
