package com.example.demo.service;

import com.example.demo.domain.Member;
import com.example.demo.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;

/*
* Happy Path만 고려한 코드
* 트랜잭션 처리가 안되어있음
* */
@RequiredArgsConstructor
public class MemberServiceV1 {

  private final MemberRepositoryV1 memberRepository;

  public void accountTransfer(String fromId, String toId, int money) throws Exception {
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
