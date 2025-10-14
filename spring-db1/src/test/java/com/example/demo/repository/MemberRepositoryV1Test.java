package com.example.demo.repository;

import static com.example.demo.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.demo.domain.Member;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
class MemberRepositoryV1Test {

  MemberRepositoryV1 repository;

  @BeforeEach
  void beforeEach() {
    // 기본 Drivermanager는 항상 새로운 커넥션을 획득
    // DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

    HikariDataSource dataSource = new HikariDataSource();
    dataSource.setJdbcUrl(URL);
    dataSource.setUsername(USERNAME);
    dataSource.setPassword(PASSWORD);

    repository = new MemberRepositoryV1(dataSource);
  }

  @Test
  void crud() throws Exception {
    log.info("start");

    // save
    Member member = new Member("memberV0", 10000);
    repository.save(member);

    // findById
    Member memberById = repository.findById(member.getMemberId());
    assertThat(memberById).isEqualTo(member);

    // update
  }

}