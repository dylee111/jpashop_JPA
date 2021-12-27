package jpabook.jpashop.domain.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class MemberForm {

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    private String city;
    private String street;
    private String zipcode;
}
