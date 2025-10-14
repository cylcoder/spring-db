package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.domain.Member;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.MemberRepositoryV3;
import com.example.demo.repository.MemberRepositoryV4_1;
import com.example.demo.repository.MemberRepositoryV4_2;
import com.example.demo.repository.MemberRepositoryV5;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/*
* 예외 누수 문제 해결
* SQLException 제거
* MemberRepository 인터페이스 의존
* */
@SpringBootTest
@Slf4j
class MemberServiceV4Test {

  public static final String MEMBER_A = "memberA";
  public static final String MEMBER_B = "memberB";
  public static final String MEMBER_EX = "ex";

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private MemberServiceV4 memberService;

  @TestConfiguration
  static class TestConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    MemberRepository memberRepository() {
//      return new MemberRepositoryV4_1(dataSource);
//      return new MemberRepositoryV4_2(dataSource);
      return new MemberRepositoryV5(dataSource);
    }

    @Bean
    MemberServiceV4 memberService() {
      return new MemberServiceV4(memberRepository());
    }

  }

  @AfterEach
  void tearDown() {
    memberRepository.deleteById(MEMBER_A);
    memberRepository.deleteById(MEMBER_B);
    memberRepository.deleteById(MEMBER_EX);
  }

  @Test
  void AopCheck() {
    log.info("memberService.getClass()={}", memberService.getClass());
    log.info("memberRepository.getClass()={}", memberRepository.getClass());
    assertThat(AopUtils.isAopProxy(memberService)).isTrue();
    assertThat(AopUtils.isAopProxy(memberRepository)).isFalse();
  }

  @Test
  @DisplayName("정상 이체")
  void accountTransfer() {
    // given
    setUpMember();

    // when
    memberService.accountTransfer(MEMBER_A, MEMBER_B, 2000);

    // then
    Member memberA = memberRepository.findById(MEMBER_A);
    Member memberB = memberRepository.findById(MEMBER_B);
    assertThat(memberA.getMoney()).isEqualTo(8000);
    assertThat(memberB.getMoney()).isEqualTo(12000);
  }

  @Test
  @DisplayName("이체중 예외 발생")
  void accountTransferEx() {
    // given
    setUpMember();

    // when
    assertThatThrownBy(() -> memberService.accountTransfer(MEMBER_A, MEMBER_EX, 2000))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("이체중 예외 발생");

    // then
    Member memberA = memberRepository.findById(MEMBER_A);
    Member memberB = memberRepository.findById(MEMBER_EX);
    assertThat(memberA.getMoney()).isEqualTo(10000);
    assertThat(memberB.getMoney()).isEqualTo(10000);
  }

  private void setUpMember() {
    Member memberA = new Member(MEMBER_A, 10000);
    Member memberB = new Member(MEMBER_B, 10000);
    Member memberEx = new Member(MEMBER_EX, 10000);
    memberRepository.save(memberA);
    memberRepository.save(memberB);
    memberRepository.save(memberEx);
  }

}