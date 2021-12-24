package jpabook.jpashop;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @Transactional // save, find 등은 트랜젝션 내에서 작동하기 때문에 필수로 있어야 한다. (Test에서 있으면 DB를 rollback해버림)
    @Rollback(false) // 트랙젝션 rollback 없이 작동
    public void testMember() throws Exception {
        //Given
        Member member = new Member();
        member.setUsername("MemberA");

        //When
        Long savedId = memberRepository.save(member);
        Member findMember = memberRepository.find(savedId);

        //Then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member); // TRUE -> 같은 영속성 컨텍스트에 있기 때문에
        System.out.println("findMember == member" + (findMember == member));
    }
}