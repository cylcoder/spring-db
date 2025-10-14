package com.example.demo.service;

import static com.example.demo.connection.ConnectionConst.PASSWORD;
import static com.example.demo.connection.ConnectionConst.URL;
import static com.example.demo.connection.ConnectionConst.USERNAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.domain.Member;
import com.example.demo.repository.MemberRepositoryV2;
import com.example.demo.repository.MemberRepositoryV3;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

class MemberServiceV3_1Test {

  public static final String MEMBER_A = "memberA";
  public static final String MEMBER_B = "memberB";
  public static final String MEMBER_EX = "ex";

  private MemberRepositoryV3 memberRepository;
  private MemberServiceV3_1 memberService;

  @BeforeEach
  void setUp() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
    memberRepository = new MemberRepositoryV3(dataSource);
    PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
    memberService = new MemberServiceV3_1(transactionManager, memberRepository);
  }

  @AfterEach
  void tearDown() {
    memberRepository.deleteAll();
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