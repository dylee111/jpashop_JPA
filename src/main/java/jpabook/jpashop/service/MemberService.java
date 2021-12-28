package jpabook.jpashop.service;

import jpabook.jpashop.Repository.MemberRepository;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//JPA 데이터는 트랙젝션 내에서 작동
@Service
@Transactional(readOnly = true) // JPA에서 읽기 전용으로 성능 최적화를 함 (데이터 변경 X)
@RequiredArgsConstructor // final이 있는 필드만 생성자를 만들어 줌
public class MemberService {

    // @RequiredArgsConstructor에 의해서 자동으로 AutoWired됨
    private final MemberRepository memberRepository;

//    @Autowired // 생성자 인젝션 -> 생성 시점에 어떤 Repository를 사용하고 있는지 명확하게 알 수 있는 장점
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

//    @Autowired // Setter 인젝션 -> 테스트 코드를 작성할 때 유용 (필요한 Repository를 불러서 사용할 수 있기 때문에)
//    public void setMemberRepository(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    /*
     * 회원 가입
     * */
    @Transactional // 클래스 레벨에서 @Transactional을 무시하고 메서드 레벨의 어노테이션을 우선함
    public Long join(Member member) {
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    /*
     * 중복회원 검증
     * */
    private void validateDuplicateMember(Member member) {
        //EXCEPTION
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    public List<Member> findAllMember() {
        return memberRepository.findAll();
    }

    // 단일 회원 조회
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    public void updateMember(Long memberId, String name, String city, String street, String zipcode) {
        Member member = memberRepository.findOne(memberId);
        member.setName(name);
        member.setAddress(new Address(city, street, zipcode));
    }
}
