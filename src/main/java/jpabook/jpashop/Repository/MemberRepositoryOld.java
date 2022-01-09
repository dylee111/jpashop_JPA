package jpabook.jpashop.Repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository // Component에 스프링 빈으로 자동 등록
@RequiredArgsConstructor
public class MemberRepositoryOld {

    private final EntityManager entityManager;

    // entiryMamagetFactory 주입 시.
//    @PersistenceUnit
//    private EntityManagerFactory entityManagerFactory;

    // 회원 저장
    public void save(Member member) {
        entityManager.persist(member);
    }

    // 단일 회원 정보 조회
    public Member findOne(Long id) {
        return entityManager.find(Member.class, id);
    }

    // 회원 리스트 조회
    public List<Member> findAll() {
        return entityManager.createQuery("SELECT m FROM Member m ", Member.class)
                .getResultList();
    }

    // 이름으로 회원 리스트 조회
    public List<Member> findByName(String name) {
        return entityManager.createQuery("SELECT m FROM Member m WHERE m.name=:name ", Member.class)
                .setParameter("name", name) // 파라미터 바인딩
                .getResultList();
    }
}
