package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class BookForm {

    private Long id;
    @NotBlank(message = "책 제목을 입력해주세요.")
    private String name;
    @NotNull(message = "제품의 가격을 입력해주세요.")
    private int price;
    @NotNull(message = "재고 수량을 입력해주세요.")
    private int stockQuantity;

    private String author;
    private String isbn;

}
