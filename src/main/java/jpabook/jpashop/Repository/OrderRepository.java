package jpabook.jpashop.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

import static jpabook.jpashop.domain.QMember.member;
import static jpabook.jpashop.domain.QOrder.order;

@Repository
@Slf4j
//@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public OrderRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long orderId) {
        return em.find(Order.class, orderId);
    }

    public List<Order> findByQuerydsl(OrderSearch orderSearch) {

        if ((orderSearch.getMemberName() == null && orderSearch.getOrderStatus() == null)
                || orderStatusEq(orderSearch.getOrderStatus()) == null) {
            return queryFactory
                    .selectFrom(order)
                    .leftJoin(order.member, member).fetchJoin()
                    .fetch();
        } else {

            return queryFactory
                    .selectFrom(order)
                    .leftJoin(order.member, member).fetchJoin()
                    .where(
                            usernameEq(orderSearch.getMemberName()),
                            orderStatusEq(orderSearch.getOrderStatus())
                    )
                    .fetch();
        }
    }

    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //????????? ??????
        List<Predicate> criteria = new ArrayList<>();
        //?????? ?????? ??????
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.getOrderStatus());
            criteria.add(status);
        }
        //?????? ?????? ??????
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" +
                            orderSearch.getMemberName() + "%");
            criteria.add(name);
        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //?????? 1000???
        return query.getResultList();
    }

    public List<Order> findAllOrder() {
        return em.createQuery("SELECT o FROM Order o ", Order.class).getResultList();
    }

    // ??????????????? ??????????????? ?????? -> ?????? ???????????? ????????? ??? ?????? ???????????? ?????? ??????
    // ???????????? ???????????? ?????? ????????? ????????? ????????? ??????

    public List<Order> findAllWithMemberAndDelivery() {
        return em.createQuery("SELECT o FROM Order o " +
                " JOIN FETCH o.member m " +
                " JOIN FETCH o.delivery d ", Order.class)
                .getResultList();
    }
    // OverLoad
    public List<Order> findAllWithMemberAndDelivery(int offset, int limit) {
        // ?????? ?????? ????????? JOIN FETCH ??????
        return em.createQuery("SELECT o FROM Order o " +
                        " JOIN FETCH o.member m " +
                        " JOIN FETCH o.delivery d ", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
    public List<Order> findAllWithItem() {
        return em.createQuery("SELECT DISTINCT o FROM Order o " +
                " JOIN FETCH o.member m " +
                " JOIN FETCH o.delivery d " +
                " JOIN FETCH o.orderItems oi " +
                " JOIN FETCH oi.item i ", Order.class)
                .getResultList();
    }

    private BooleanExpression orderStatusEq(OrderStatus orderStatus) {
        return StringUtils.hasText(orderStatus.toString()) ? order.status.eq(orderStatus) : null;
    }

    private BooleanExpression usernameEq(String name) {
        return StringUtils.hasText(name) ? member.name.eq(name) : null;
    }
}
