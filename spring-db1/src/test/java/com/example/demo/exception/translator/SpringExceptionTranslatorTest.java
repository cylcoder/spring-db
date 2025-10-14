package com.example.demo.exception.translator;

import static com.example.demo.connection.ConnectionConst.PASSWORD;
import static com.example.demo.connection.ConnectionConst.URL;
import static com.example.demo.connection.ConnectionConst.USERNAME;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

@Slf4j
class SpringExceptionTranslatorTest {

  DataSource dataSource;

  @BeforeEach
  void setUp() {
    dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
  }

  @Test
  void sqlExceptionErrorCode() {
    try {
      String sql = "SELECT bad grammar";
      Connection con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement(sql);
      pstmt.executeQuery();
    } catch (SQLException e) {
      assertThat(e.getErrorCode()).isEqualTo(42122);
      int errorCode = e.getErrorCode();
      log.info("errorCode={}", errorCode);
      log.info("error", e);
    }
  }

  @Test
  void exceptionTranslator() {
    String sql = "SELECT bad grammar";
    try {
      Connection con = dataSource.getConnection();
      PreparedStatement pstmt = con.prepareStatement(sql);
      pstmt.executeQuery();
    } catch (SQLException e) {
      assertThat(e.getErrorCode()).isEqualTo(42122);
      SQLErrorCodeSQLExceptionTranslator exTranslator =
          new SQLErrorCodeSQLExceptionTranslator(dataSource);
      DataAccessException resultEx = exTranslator.translate("select", sql, e);
      log.info("resultEx", resultEx);
      assertThat(resultEx).isNotNull();
      assertThat(resultEx.getClass()).isEqualTo(BadSqlGrammarException.class);
    }
  }

}
