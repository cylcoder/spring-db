package com.example.demo.service;

import com.example.demo.domain.Member;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/*
* 예외 누수 문제 해결
* SQLException 제거
* MemberRepository 인터페이스 의존
* */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV4 {

  private final MemberRepository memberRepository;

  @Transactional
  public void accountTransfer(String fromId, String toId, int money) {
    bizLogic(fromId, toId, money);
  }

  private void bizLogic(String fromId, String toId, int money) {
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
