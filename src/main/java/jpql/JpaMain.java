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
//            for (int i = 0; i < 100; i++) {
            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setAge(1);
            member.setUsername("member1");

            member.setTeam(team);
            em.persist(member);
//            }
//            jpqlInit(em);

            em.flush();
            em.clear();
//            projection(em);
//            paging(em);

            join(em);
            
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.clear();
        }
    }

    private static void join(EntityManager em) {
        //            String query = "select m from Member m inner join m.team t where  t.name =:teamName";
//            String query = "select m from Member m left join m.team t";
//            String query = "select m from Member m, Team t where m.username = t.name";
//            String query = "select m from Member m left join m.team t on t.name = 'teamA'";
        //연관관계 없는 외부 조인
        String query = "select m from Member m left join Team t on m.username = t.name";
        List<Member> result = em.createQuery(query, Member.class)
//                    .setParameter("teamName", "teamA")
                .getResultList();
    }

    private static void paging(EntityManager em) {
        Member member = new Member();
        member.setAge(10);
        member.setUsername("member1");
        em.persist(member);
    }

    private static void projection(EntityManager em) {
        //엔티티 프로젝션은 영속성 컨텍스트에 관리 대상이다!
        List<Member> results = em.createQuery("select m from Member m ", Member.class).getResultList();
        Member findMember = results.get(0);
        findMember.setAge(20);


        List<MemberDTO> results2 = em.createQuery("select new jpql.MemberDTO(m.username, m.age) from Member m ", MemberDTO.class).getResultList();

        System.out.println(results.get(0).getUsername());
    }

    private static void jpqlInit(EntityManager em) {
        //반환 값이 명확할 때
        TypedQuery<Member> query = em.createQuery("select m from Member m ", Member.class);
        List<Member> resultList = query.getResultList();

        //무조건 결과가 1개여야한다. 그 이상도 그이하도 안된다(에러)
        TypedQuery<Member> query3 = em.createQuery("select m from Member m where m.username =: username", Member.class);
        //이름 기준으로 파라미터 바인딩
        query3.setParameter("username", "member1");
        Member singleResult = query3.getSingleResult();

        //반환 값이 명확하지 않을 떄
        Query query2 = em.createQuery("select m.username, m.age from Member m");

    }
}
