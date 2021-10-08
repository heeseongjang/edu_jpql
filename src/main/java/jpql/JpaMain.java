package jpql;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Member member = new Member();
            member.setAge(10);
            member.setUsername("member1");
            em.persist(member);

            //반환 값이 명확할 때
            TypedQuery<Member> query = em.createQuery("select m from Member m ", Member.class);
            List<Member> resultList = query.getResultList();

            //무조건 결과가 1개여야한다. 그 이상도 그이하도 안된다(에러)
            TypedQuery<Member> query3 = em.createQuery("select m from Member m where m.username =: username", Member.class);
            //이름 기준으로 파라미터 바인딩
            query3.setParameter("username", "member1");
            Member singleResult = query3.getSingleResult();

            //반환 값이 명확하지 않을 떄
            Query query2 = em.createQuery("select m.username, m.age from Member m"");
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.clear();
        }
    }
}
