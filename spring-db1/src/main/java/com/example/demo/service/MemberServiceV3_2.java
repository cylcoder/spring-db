package com.example.demo.service;

import com.example.demo.domain.Member;
import com.example.demo.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/*
* 트랜잭션 - 트랜잭션 템플릿
* 트랜잭션 템플릿으로 트랜잭션 시작, 커밋, 롤백의 코드를 제거
* */
@Slf4j
public class MemberServiceV3_2 {

  private final TransactionTemplate txTemplate;
  private final MemberRepositoryV3 memberRepository;

  public MemberServiceV3_2(PlatformTransactionManager transactionManager, MemberRepositoryV3 memberRepository) {
    this.txTemplate = new TransactionTemplate(transactionManager);
    this.memberRepository = memberRepository;
  }

  public void accountTransfer(String fromId, String toId, int money) {
    txTemplate.executeWithoutResult(status -> {
      try {
        bizLogic(fromId, toId, money);
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }
    });
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
