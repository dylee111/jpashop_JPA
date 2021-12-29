package jpabook.jpashop.Repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    /*
     * 컬렉션은 한번에 조회
     * Query : 루트 쿼리 1번 , 컬렉션 N번
     * 단건 조회에서 많이 사용하는 방식
     * */
    public List<OrderQueryDTO> findOrderQueryDTO() {
        // 루트 조회 (ToOne 코드를 한번에 조회)
        List<OrderQueryDTO> result = findOrders();

        // 루프를 돌면서 컬렉션 추가 (추카 쿼리 실행 - N번)
        result.forEach(orderQueryDTO -> {
            List<OrderItemQueryDTO> orderItems = findOrderItems(orderQueryDTO.getOrderId());
            orderQueryDTO.setOrderItems(orderItems);
        });

        return result;
    }

    /*
    * 최적화 - Query : 루트 (1번) / 컬렉션 (1번)
    * 데이터를 한번에 처리할 때 많이 사용하는 방식
    * */
    public List<OrderQueryDTO> findAllByDtoOptimization() {
        List<OrderQueryDTO> result = findOrders();

        // Order 엔티티 식별자를 List로 반환 -> orderItems에서 IN절에 사용할 식별자
        List<Long> orderIds = toOrderIds(result);

        // OrderItem 조회 쿼리
        Map<Long, List<OrderItemQueryDTO>> orderItemMap = findOrderItemMap(orderIds);

        // 루프를 통해서 메모리에 Map 데이터를 올려둠
        result.forEach(orderQueryDTO -> orderQueryDTO.setOrderItems(orderItemMap.get(orderQueryDTO.getOrderId())));

        return result;
    }

    // OrderItem 조회 쿼리
    private Map<Long, List<OrderItemQueryDTO>> findOrderItemMap(List<Long> orderIds) {
        List<OrderItemQueryDTO> orderItems = em.createQuery("SELECT new jpabook.jpashop.Repository.order.query.OrderItemQueryDTO(oi.order.id, i.name, oi.orderPrice, oi.count) " +
                        " FROM OrderItem oi " +
                        " JOIN oi.item i " +
                        " WHERE oi.order.id " +
                        " IN :orderIds ", OrderItemQueryDTO.class)
                .setParameter("orderIds", orderIds)
                .getResultList();

        // Key : orderItemQueryDTO.getOrderId() / value : orderItemQueryDTO
        Map<Long, List<OrderItemQueryDTO>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(orderItemQueryDTO -> orderItemQueryDTO.getOrderId()));
        return orderItemMap;
    }

    // Order 엔티티 식별자를 List로 반환 -> orderItems에서 IN절에 사용할 식별자
    private List<Long> toOrderIds(List<OrderQueryDTO> result) {
        List<Long> orderIds = result.stream()
                .map(orderQueryDTO -> orderQueryDTO.getOrderId()).collect(Collectors.toList());
        return orderIds;
    }

    /*
     * 1:N 관계(컬렉션)을 제외한 나머지를 한번에 조회
     * */
    public List<OrderQueryDTO> findOrders() {
        return em.createQuery("SELECT new jpabook.jpashop.Repository.order.query.OrderQueryDTO(o.id, m.name, o.orderDate, o.status, d.address) " +
                " FROM Order o " +
                " JOIN o.member m " +
                " JOIN o.delivery d ", OrderQueryDTO.class).getResultList();
    }

    /*
     * 1:N 관계인 OrderItem 조회
     * */
    public List<OrderItemQueryDTO> findOrderItems(Long orderId) {
        return em.createQuery("SELECT new jpabook.jpashop.Repository.order.query.OrderItemQueryDTO(oi.order.id, i.name, oi.orderPrice, oi.count) " +
                        " FROM OrderItem oi " +
                        " JOIN oi.item i " +
                        " WHERE oi.order.id=:orderId", OrderItemQueryDTO.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    /*
    * Flat 최적화 메서드
    * */
    public List<OrderFlatDTO> findAllByDtoFlat() {
        return em.createQuery("SELECT new jpabook.jpashop.Repository.order.query.OrderFlatDTO(o.id, m.name, o.orderDate, " +
                        " o.status, d.address, i.name, oi.orderPrice, oi.count) " +
                        " FROM Order o " +
                        " JOIN o.member m " +
                        " JOIN o.delivery d " +
                        " JOIN o.orderItems oi " +
                        " JOIN oi.item i ", OrderFlatDTO.class)
                .getResultList();
    }
}
