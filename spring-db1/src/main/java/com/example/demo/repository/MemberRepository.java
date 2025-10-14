package com.example.demo.repository;

import com.example.demo.domain.Member;

public interface MemberRepository {

  Member save(Member member);
  Member findById(String memberId);
  void update(String memberId, int money);
  void deleteById(String memberId);

}
