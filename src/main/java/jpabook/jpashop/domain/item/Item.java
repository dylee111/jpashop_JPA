package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
public abstract class Item {
    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> items = new ArrayList<>();

    //--비즈니스 로직--//
    // 데이터를 가지고 있는 엔티티에서 핵심 비즈니스 로직을 가지고 있는 것이 좋다.
    // 엔티티 데이터를 변경할 일이 발생하면 Setter를 통해서 변경하는 것이 아닌 비즈니스 로직을 설정해서 변경하는 것이 바람직직

    /*
    * Stock 증가
    * */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    /*
     * Stock 감소
     * */
    public void removeStock(int quantity) {
        int restStock = stockQuantity - quantity;
        if (restStock <= 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }

}
