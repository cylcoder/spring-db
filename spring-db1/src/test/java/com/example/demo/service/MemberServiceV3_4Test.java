package com.example.demo.service;

import static com.example.demo.connection.ConnectionConst.PASSWORD;
import static com.example.demo.connection.ConnectionConst.URL;
import static com.example.demo.connection.ConnectionConst.USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.domain.Member;
import com.example.demo.repository.MemberRepositoryV3;
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
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

/*
* 트랜잭션 - DataSource, transactionManager 자동 등록
* */
@SpringBootTest
@Slf4j
class MemberServiceV3_4Test {

  public static final String MEMBER_A = "memberA";
  public static final String MEMBER_B = "memberB";
  public static final String MEMBER_EX = "ex";

  @Autowired
  private MemberRepositoryV3 memberRepository;

  @Autowired
  private MemberServiceV3_3 memberService;

  @TestConfiguration
  static class TestConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    MemberRepositoryV3 memberRepository() {
      return new MemberRepositoryV3(dataSource);
    }

    @Bean
    MemberServiceV3_3 memberService() {
      return new MemberServiceV3_3(memberRepository());
    }

  }

  @AfterEach
  void tearDown() {
    memberRepository.deleteAll();
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
  void accountTransfer() throws Exception {
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
  void accountTransferEx() throws Exception {
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

  private void setUpMember() throws SQLException {
    Member memberA = new Member(MEMBER_A, 10000);
    Member memberB = new Member(MEMBER_B, 10000);
    Member memberEx = new Member(MEMBER_EX, 10000);
    memberRepository.save(memberA);
    memberRepository.save(memberB);
    memberRepository.save(memberEx);
  }

}