package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RestController // @Controller + @ResponseBody
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    /*
    * 회원 등록 API V1
    * */
    // API를 만들 때는 엔티티를 파라미터로 받는 것 금지, 외부로 노출도 금지
    // V1은 사용하지 말 것!
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    /*
    * 회원 등록 API V2
    * */
    // 장점 1 : 누군가 엔티티 필드를 변경하면 컴파일 에러가 발생하기 때문에 에러를 찾기 용이하다. -> 엔티티를 변경해도 API 스펙이 바뀌지 않는다.
    // 장점 2 : API 스펙에서 어떤 데이터를 받는지 별도의 DTO 클래스를 통해서 확인이 가능 -> 어떤 데이터를 어떻게 입력해야 하는지 확인이 쉽다.
    // -> API는 요청을 주고 받는 것은 절대 엔티티를 받지 않는다! 별도의 DTO 객체를 만들어서 사용할 것!
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long memberId = memberService.join(member);

        return new CreateMemberResponse(memberId);
    }

    /*
    * 회원 수정 API
    * */
    @PutMapping("/api/v2/members/{memberId}")
    public UpdateMemberResponse updateMember(@PathVariable("memberId") Long memberId,
                                             @RequestBody @Valid UpdateMemberRequest request) {

        memberService.updateMember(memberId, request.getName(), request.getCity(), request.getStreet(), request.getZipcode());
        Member member = memberService.findOne(memberId);
        // DTO를 외부에서 생성하면 조금 더 단순화할 수 있다.
        return new UpdateMemberResponse(member.getId(), member.getName(),
                member.getAddress().getCity(),
                member.getAddress().getStreet(),
                member.getAddress().getZipcode());
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class CreateMemberRequest {
        // 엔티티에서 설정하면 해당 엔티티를 사용하는 모든 로직에 적용이 됨. 하지만 로직마다 null이 필요한 로직도 있기 때문에 DTO에서 설정하면 유동적으로 적용가능
        @NotEmpty
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
        private String city;
        private String street;
        private String zipcode;
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
        private String city;
        private String street;
        private String zipcode;
    }
}
