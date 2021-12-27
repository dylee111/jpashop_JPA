package jpabook.jpashop.service;

import jpabook.jpashop.Repository.MemberRepository;
import jpabook.jpashop.domain.Member;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.fail;


@RunWith(SpringRunner.class) //Junit을 실행할 때 스프링이랑 엮어서 실행하기 위함
@SpringBootTest // 스프링 부트를 띄운 상태에서 테스트 진행하기 위해서 (없을 경우 @AutoWired 등 사용 불가)
@Transactional  // 트랜젝션을 걸고 테스트를 하고 끝나고 자동으로 rollback (Service나 Repository에서는 자동 rollback x)
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;

    @Test
//    @Rollback(value = false) // 트랜젝션 자동 rollback x -> insert 쿼리 등 확인이 필요한 경우
    public void 회원가입() throws Exception{
        //GIVEN
        Member member = new Member();
        member.setName("LEE");

        //WHEN
        Long savaId = memberService.join(member);

        //THEN
//        em.flush();
        Assert.assertEquals(member, memberRepository.findOne(savaId));
    }

    @Test(expected = IllegalStateException.class)
    public void 회원_중복_예외() throws Exception {
        //GIVEN
        Member member1 = new Member();
        member1.setName("LEE");

        Member member2 = new Member();
        member2.setName("LEE");

        //WHEN
        memberService.join(member1);
        memberService.join(member2);

        //THEN
        fail("예외가 발생해야 한다");
    }
}