package jpabook.jpashop.Repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderSimpleQueryService {

    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    public List<OrderSimpleQueryDTO> findAllOrderDTO() {
        return orderSimpleQueryRepository.findOrderDTO();

    }
}
