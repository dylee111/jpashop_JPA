package jpabook.jpashop.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {
    private String city;
    private String street;
    private String zipcode;

    // 값 타입은 불변(immutable) 형태로 설계하는 것이 일반적
    // 생성자를 통해서 최초 생성시에만 초기화하도록 설계하는 것이 좋다.
    // JPA 구현 라이브러리가 객체를 생성할 때 리플렉션 또는 프록시를 지원하기 위해 기본 생성자 필수
    // JPA 스펙상 protected로 생성할 것 -> 안전성을 위해
    protected Address() {}

    public Address(String city, String street, String zipcode) {
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
    }
}
