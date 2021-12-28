package jpabook.jpashop.service;

import jpabook.jpashop.Repository.OrderRepository;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        //given
        Member member = createMember();
        Item item = createItem();

        int orderCount = 2;
        
        //when
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount); // 주문 생성
        
        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
        assertEquals("주문한 상품 수는 1", 1, getOrder.getOrderItems().size());
        assertEquals("주문 가격은 가격 * 수량", 10000*orderCount, getOrder.totalPrice());
        assertEquals("주문 수량만큼 재고는 감소", 8, item.getStockQuantity());
    }


    @Test(expected = NotEnoughStockException.class) // 예상되는 예외 설정
    public void 상품주문_재고수량초과() throws Exception {
        //given
        Member member = createMember();
        Item item = createItem();
        //when
        int orderCount = 11;

        orderService.order(member.getId(), item.getId(), orderCount); // 여기서 예외가 터져야 함
        //then
        fail("재고 수량 부족이 발생해야 합니다.");
    }

    @Test
    public void 주문취소() throws Exception {
        //given
        Member member = createMember();
        Item item = createItem();

        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);
        //when
        orderService.cancelOrder(orderId);

        //then
        Order order = orderRepository.findOne(orderId);

        assertEquals("주문 상태는 CANCEL로 변경", OrderStatus.CANCEL, order.getStatus());
        assertEquals("재고 수량은 다시 원상복구되어야 한다.", 10, item.getStockQuantity());
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("부산", "해운대","123-123"));
        em.persist(member);
        return member;
    }

    private Item createItem() {
        Item item = new Book("JPA 교과서", 10000, 10, "KIM", "12334");
        em.persist(item);
        return item;
    }
}
