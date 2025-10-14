package com.example.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.domain.Member;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class MemberRepositoryV0Test {

  @Test
  void saveMember() throws SQLException {
    MemberRepositoryV0 memberRepository = new MemberRepositoryV0();
    Member member = new Member("1", 10);
    memberRepository.save(member);
  }

  @Test
  void findMember() throws Exception {
    MemberRepositoryV0 memberRepository = new MemberRepositoryV0();
    Member member = new Member("1", 10);
    memberRepository.save(member);
    Member findMember = memberRepository.findById("1");
    log.info("member={}", member);
    assertThat(member).isEqualTo(findMember);
  }

  @Test
  void updateMember() throws Exception {
    MemberRepositoryV0 memberRepository = new MemberRepositoryV0();
    Member member = new Member("1", 10);
    memberRepository.save(member);

    memberRepository.update(member.getMemberId(), 20);
    Member findMember = memberRepository.findById(member.getMemberId());
    assertThat(findMember.getMoney()).isEqualTo(20);
  }

  @Test
  void deleteMember() throws SQLException {
    MemberRepositoryV0 memberRepository = new MemberRepositoryV0();
    Member member = new Member("1", 10);
    memberRepository.save(member);

    memberRepository.deleteById(member.getMemberId());
    assertThatThrownBy(() -> memberRepository.findById(member.getMemberId()))
        .isInstanceOf(NoSuchElementException.class);
  }

}