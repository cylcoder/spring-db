package com.example.demo.connection;

import static com.example.demo.connection.ConnectionConst.PASSWORD;
import static com.example.demo.connection.ConnectionConst.URL;
import static com.example.demo.connection.ConnectionConst.USERNAME;

import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Slf4j
class ConnectionTest {

  @Test
  void driverManager() throws SQLException {
    Connection con1 = DriverManager.getConnection(ConnectionConst.URL, USERNAME, PASSWORD);
    Connection con2 = DriverManager.getConnection(ConnectionConst.URL, USERNAME, PASSWORD);
    log.info("con1={}", con1);
    log.info("con2={}", con2);
  }

  @Test
  void dataSourceDriverManager() throws SQLException {
    DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
    useDataSource(dataSource);
  }

  @Test
  void dataSourceConnectionPool() throws InterruptedException, SQLException {
    HikariDataSource dataSource = new HikariDataSource();
    dataSource.setJdbcUrl(URL);
    dataSource.setUsername(USERNAME);
    dataSource.setPassword(PASSWORD);
    dataSource.setMaximumPoolSize(10);
    dataSource.setPoolName("MyPool");
    useDataSource(dataSource);
    Thread.sleep(1000);
  }

  private void useDataSource(DataSource dataSource) throws SQLException {
    Connection con1 = dataSource.getConnection();
    Connection con2 = dataSource.getConnection();
    log.info("con1={}", con1);
    log.info("con2={}", con2);
  }

}
