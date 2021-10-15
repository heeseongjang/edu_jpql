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
            Team teamA = new Team();
            teamA.setName("팀A");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("팀B");
            em.persist(teamB);

            Member member = new Member();
//            member.setAge(1);
//            member.setUsername("member1");
//            member.setUsername(null);
            member.setUsername("회원1");
            member.setTeam(teamA);
//            member.setType(MemberType.ADMIN);

            Member member2 = new Member();
//            member2.setAge(2);
            member2.setUsername("회원2");
//            member2.setType(MemberType.ADMIN);
            member2.setTeam(teamA);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.setTeam(teamB);
            em.persist(member);
            em.persist(member2);
            em.persist(member3);
//            }
//            jpqlInit(em);

//            em.flush();
//            em.clear();

//            projection(em);
//            paging(em);
//            join(em);
//            subQuery(em);
//            jpqlType(em);
//            caseEx(em);
//            function(em);
//            pathExpression(em);
//            fetchJoin1(em);
//            fetchJoin2(em);
//            entity(em, teamA);
//            namedQuery(em, member);

            int resultCount = em.createQuery("update Member m set m.age=20")
                    .executeUpdate();
            System.out.println(resultCount);

            em.clear();
            Member findMember = em.find(Member.class, member.getId());
            System.out.println("findMember = " + findMember.getAge());
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.clear();
        }
    }

    private static void namedQuery(EntityManager em, Member member) {
        List<Member> resultList = em.createNamedQuery("Member.findByUsername", Member.class)
                .setParameter("username", "회원2")
                .getResultList();
        for (Member m : resultList){
            System.out.println("member : " + member);
        }
    }

    private static void entity(EntityManager em, Team teamA) {
        //            String query = "select m from Member m where m = :member";
//            String query = "select m from Member m where m.id = :member";
        String query = "select m from Member m where m.team = :team";

        Member findMember = em.createQuery(query, Member.class)
//                   .setParameter("member", member)
//                   .setParameter("memberId", member.getId())
               .setParameter("team", teamA)
                .getSingleResult();

        System.out.println("findMember : "  + findMember);
    }

    private static void fetchJoin2(EntityManager em) {
        //            String query = "select distinct t From Team t join fetch t.members as m";//별칭 되도록 금지!
//            String query = "select t From Team t join fetch t.members m";
        String query = "select t From Team t ";
        List<Team> resultList = em.createQuery(query, Team.class)
                .setFirstResult(0)
//                    .setMaxResults(1)
                .setMaxResults(2)
                .getResultList();

        for (Team s : resultList) {
            System.out.println("team = " + s.getName() + "|Members = " + s.getMembers().size());
        }
    }

    private static void fetchJoin1(EntityManager em) {
        //            String query = "select m From Member m";
//            String query = "select m From Member m join fetch m.team"; //조회할때 팀까지 조회해오자
//            List<Member> resultList = em.createQuery(query, Member.class)
//                    .getResultList();
        //회원1, 팀A(SQL)
        //회원2, 팀A(1차캐시)
        //회원3, 팀B(SQL)
        //회원 100명이면? 이래서 페치 조인

        String query = "select distinct t From Team t join fetch t.members";
        List<Team> resultList = em.createQuery(query, Team.class)
                .getResultList();

        for (Team s : resultList) {
            System.out.println("team = " + s.getName() + "|Members = " + s.getMembers().size());
        }
    }

    private static void pathExpression(EntityManager em) {
        //            String query = "select  m.username from Member m"; //username에서 .으로 뭘 더 검색할 수 없음 상태 필드
//            String query = "select  m.team from Member m"; //묵시적인 내부조인 & 단일값 연 경로
        String query = "select  m.team from Team t join t.members m"; //컬렉션 값 연관 경로
        List<String> resultList = em.createQuery(query, String.class)
                .getResultList();

        for (String s : resultList) {
            System.out.println("s = " + s);
        }
    }

    private static void function(EntityManager em) {
        //            String query = "select concat('a','b') from Member m";
//            String query = "select substring(m.username,2,3) from Member m";
//            String query = "select locate('de','abcdefg') from Member m";
//            String query = "select size(t.members) from Team t";
//            @OrderColumn 안쓰는 걸 추천
//            String query = "select size(t.members) from Team t";
        String query = "select function('group_concat', m.username) from Member m";
        List<String> resultList = em.createQuery(query, String.class)
                .getResultList();

        for (String s : resultList) {
            System.out.println("s = " + s);
        }
    }

    private static void caseEx(EntityManager em) {
        //            String query = "" +
//                    "select " +
//                    " case when m.age <= 10 then '학생요금'" +
//                    "      when m.age >= 60 then '경로요금'" +
//                    "      else '일반요금'" +
//                    "end " +
//                    " from Member m";
//            String query = "select coalesce(m.username, '이름 없는 회원') from Member m";
        String query = "select NULLIF(m.username, '관리자') from Member m";
        List<String> resultList = em.createQuery(query, String.class).getResultList();

        for (String s : resultList) {
            System.out.println("s = " + s);
        }
    }

    private static void jpqlType(EntityManager em) {
        String query = "select m.username, 'HELLO', TRUE from Member m" +
                " where m.type = jpql.MemberType.ADMIN" +
                " and m.age between 0 and 10";
        List<Object[]> result = em.createQuery(query)
                .getResultList();
    }

    private static void subQuery(EntityManager em) {
        //from절의 subQuery는 불가
        String query = "select (select avg(m1.age) from Member m1) from Member m join Team t on m.username = t.name";
        List<Member> result = em.createQuery(query, Member.class)
                .getResultList();
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
