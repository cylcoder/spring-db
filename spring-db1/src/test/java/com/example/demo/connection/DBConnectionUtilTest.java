package com.example.demo.connection;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class DBConnectionUtilTest {

  @Test
  void connection() throws SQLException {
    Connection connection = DBConnectionUtil.getConnection();
    assertThat(connection).isNotNull();
  }

}