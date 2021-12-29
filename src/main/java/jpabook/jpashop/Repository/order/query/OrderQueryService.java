package jpabook.jpashop.Repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderQueryService {

    private final OrderQueryRepository orderQueryRepository;

    public List<OrderQueryDTO> findOrders() {
        return orderQueryRepository.findOrderQueryDTO();
    }

    public List<OrderQueryDTO> findAllByDtoOptimization() {
        return orderQueryRepository.findAllByDtoOptimization();
    }

    public List<OrderFlatDTO> findAllByDtoFlat() {
        return orderQueryRepository.findAllByDtoFlat();
    }
}
