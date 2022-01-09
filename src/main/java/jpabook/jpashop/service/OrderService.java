package jpabook.jpashop.service;

import jpabook.jpashop.Repository.ItemRepository;
import jpabook.jpashop.Repository.MemberRepositoryOld;
import jpabook.jpashop.Repository.OrderRepository;
import jpabook.jpashop.Repository.OrderSearch;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepositoryOld memberRepository;
    private final ItemRepository itemRepository;

    /*
     *주문
     **/
    @Transactional
    public Long order(Long memberId, Long itemID, int count) {

        //엔티티 조회
        Member member = memberRepository.findOne(memberId); // 영속 상태 (외부에서 Member가 넘어오면 영속 상태가 아닌 상태로 넘어온다)
        Item item = itemRepository.findOne(itemID);

        //배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setStatus(DeliveryStatus.READY);
        delivery.setAddress(member.getAddress());

        //주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        //주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        orderRepository.save(order);

        return order.getId();
    }


    /*
    * 주문 취소
    * */
    @Transactional // 트랙젝션 내에서 메서드가 동작해야지 커밋이 되기 때문에 필수!!!!!!!
    public void cancelOrder(Long orderId) {
        //주문 조회
        Order order = orderRepository.findOne(orderId);
        log.info("SERVICE ORDERID >>> " + orderId);
        log.info("AFTER >>>" + order.getStatus());

        //주문 취소
        order.cancel(); // 더티 체킹에서 의해서 쿼리 작성 없이 변경 감지가 되서 자동으로 Delete, Update 쿼리 발생
        log.info("BEFORE >>>" + order.getStatus());
    }

    // Order 전체 조회 (join fetch 미사용)
    public List<Order> findAllOrder() {
        return orderRepository.findAllOrder();
    }

    // Order 전체 조회 (join fetch 사용)
    public List<Order> findAllWithMemberAndDelivery() {
        return orderRepository.findAllWithMemberAndDelivery();
    }
    // OverLoad
    public List<Order> findAllWithMemberAndDelivery(int offset, int limit) {
        return orderRepository.findAllWithMemberAndDelivery(offset, limit);
    }

    //검색
    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAllByCriteria(orderSearch);
    }

    public List<Order> findAllWithItem() {
        return orderRepository.findAllWithItem();
    }

    public List<Order> findByQuerydsl(OrderSearch orderSearch) {
        return orderRepository.findByQuerydsl(orderSearch);
    }
}
