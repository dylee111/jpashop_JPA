package jpabook.jpashop.Repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

/*
 * API 스펙에 맞춘 쿼리를 작성할 레포지토리
 * */
@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    public List<OrderSimpleQueryDTO> findOrderDTO() {
        // new 생성자를 통해서 DTO 클래스에 접근 -> 원하는 필드값만 매핑
        // join fetch X
        return em.createQuery("SELECT new jpabook.jpashop.Repository.order.simplequery.OrderSimpleQueryDTO(o.id, m.name, o.status, o.orderDate, d.address) " +
                " FROM Order o " +
                " JOIN o.member m " +
                " JOIN o.delivery d ", OrderSimpleQueryDTO.class)
                .getResultList();
    }
}
