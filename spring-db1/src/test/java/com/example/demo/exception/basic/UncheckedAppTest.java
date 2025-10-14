package com.example.demo.exception.basic;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class UncheckedAppTest {

  @Test
  void unchecked() {
    Controller controller = new Controller();
    assertThatThrownBy(controller::request).isInstanceOf(Exception.class);
  }

  @Test
  void printEx() {
    Controller controller = new Controller();
    try {
      controller.request();
    } catch (Exception e) {
      log.info("ex", e);
    }
  }

  static class Controller {
    Service service = new Service();

    public void request() {
      service.logic();
    }
  }

  static class Service {
    Repository repository = new Repository();
    NetworkClient networkClient = new NetworkClient();

    public void logic() {
      repository.call();
      networkClient.call();
    }
  }

  static class NetworkClient {
    public void call() {
      throw new RuntimeConnectException("연결 실패");
    }
  }

  static class Repository  {
    public void call() {
      try {
        runSQL();
      } catch (SQLException e) {
//        throw new RuntimeSQLException(e);
        throw new RuntimeSQLException();
      }
    }

    public void runSQL() throws SQLException {
      throw new SQLException();
    }
  }

  static class RuntimeConnectException extends RuntimeException {
    public RuntimeConnectException(String message) {
      super(message);
    }
  }

  static class RuntimeSQLException extends RuntimeException {
    public RuntimeSQLException() {

    }

    public RuntimeSQLException(Throwable cause) {
      super(cause);
    }
  }

}
