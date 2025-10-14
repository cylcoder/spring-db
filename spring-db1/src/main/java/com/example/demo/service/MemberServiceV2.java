package com.example.demo.service;

import com.example.demo.domain.Member;
import com.example.demo.repository.MemberRepositoryV2;
import java.sql.Connection;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
* 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
* 트랜잭션 처리를 하려면 커넥션을 공유해야 함
* 커넥션을 파라미터로 전달받아 동일한 커넥션을 통해 트랜잭션 처리
* */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

  private final DataSource dataSource;
  private final MemberRepositoryV2 memberRepository;

  public void accountTransfer(String fromId, String toId, int money) throws Exception {
    Connection con = dataSource.getConnection();

    try {
      con.setAutoCommit(false); // 트랜잭션 시작
      bizLogic(con, fromId, toId, money); // 비즈니스 로직
      con.commit();
    } catch (Exception e) {
      con.rollback();
      throw new IllegalStateException(e);
    } finally {
      release(con);
    }
  }

  private void validate(Member member) {
    if (member.getMemberId().equals("ex")) {
      throw new IllegalStateException("이체중 예외 발생");
    }
  }

  private void bizLogic(Connection con, String fromId, String toId, int money) throws Exception {
    Member fromMember = memberRepository.findById(fromId);
    Member toMember = memberRepository.findById(toId);

    memberRepository.update(con, fromId, fromMember.getMoney() - money);
    validate(toMember);
    memberRepository.update(con, toId, toMember.getMoney() + money);
  }

  private void release(Connection con) {
    if (con != null) {
      try {
        con.setAutoCommit(true);
        con.close();
      } catch (Exception e) {
        log.info("error", e);
      }
    }
  }

}
