package com.example.demo.service;

import com.example.demo.domain.Member;
import com.example.demo.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/*
* 트랜잭션 - 트랜잭션 매니저
* 트랜잭션 매니저를 통해 더이상 파라미터가 없이도 동일한 커넥션을 사용
* */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV3_1 {

  private final PlatformTransactionManager transactionManager;
  private final MemberRepositoryV3 memberRepository;

  public void accountTransfer(String fromId, String toId, int money) throws Exception {
    // 트랜잭션 시작
    TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

    try {
      bizLogic(fromId, toId, money);
      transactionManager.commit(status);
    } catch (Exception e) {
      transactionManager.rollback(status);
      throw new IllegalStateException(e);
    }
  }

  private void bizLogic(String fromId, String toId, int money) throws Exception {
    Member fromMember = memberRepository.findById(fromId);
    Member toMember = memberRepository.findById(toId);

    memberRepository.update(fromId, fromMember.getMoney() - money);
    validate(toMember);
    memberRepository.update(toId, toMember.getMoney() + money);
  }

  private void validate(Member member) {
    if (member.getMemberId().equals("ex")) {
      throw new IllegalStateException("이체중 예외 발생");
    }
  }

}
