package com.example.demo.connection;

import static com.example.demo.connection.ConnectionConst.PASSWORD;
import static com.example.demo.connection.ConnectionConst.URL;
import static com.example.demo.connection.ConnectionConst.USERNAME;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DBConnectionUtil {

  public static Connection getConnection() throws SQLException {
    Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    log.info("connection={}", connection);
    log.info("connection.getClass()={}", connection.getClass());
    return connection;
  }

}
