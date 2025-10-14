package com.example.demo.repository;

import com.example.demo.domain.Member;
import com.example.demo.repository.ex.MyDbException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;

/*[Service Layer]
    ↑
    │  (기술 독립 예외: MyDbException)
    │
[Repository Layer]
    ├── JDBC (SQLException)
    ├── JPA (PersistenceException)
    └── MyBatis (MyBatisException)*/

/*
* 예외 누수 문제 해결
* 체크 예외를 런타임 예외로 변경
* MemberRepository 인터페이스 사용
* throws SQLException로 인해 서비스가 JDBC 종속적인 SQLException을 의존하는 문제를
* 런타임 예외인 MyDbException로 변환하여 특정 기술에 종속적이지 않도록 개선
* */
@Slf4j
@RequiredArgsConstructor
public class MemberRepositoryV4_1 implements MemberRepository {

  private final DataSource dataSource;

  @Override
  public Member save(Member member) {
    String sql = "INSERT INTO member (member_id, money) values (?, ?)";
    Connection con = null;
    PreparedStatement pstmt = null;

    try {
      con = getConnection();
      pstmt = con.prepareStatement(sql);
      pstmt.setString(1, member.getMemberId());
      pstmt.setInt(2, member.getMoney());
      pstmt.executeUpdate();
      return member;
    } catch (SQLException e) {
      throw new MyDbException(e);
    } finally {
      close(con, pstmt, null);
    }
  }

  @Override
  public Member findById(String memberId) {
    String sql = "SELECT * FROM member WHERE member_id = ?";
    Connection con = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
      con = getConnection();
      pstmt = con.prepareStatement(sql);
      pstmt.setString(1, memberId);

      rs = pstmt.executeQuery();
      if (rs.next()) {
        Member member = new Member();
        member.setMemberId(rs.getString("member_id"));
        member.setMoney(rs.getInt("money"));
        return member;
      } else {
        throw new NoSuchElementException("member not found (memberId = %s)".formatted(memberId));
      }
    } catch (SQLException e) {
      throw new MyDbException(e);
    } finally {
      close(con, pstmt, rs);
    }
  }

  @Override
  public void update(String memberId, int money) {
    String sql = "UPDATE member SET money = ? WHERE member_id = ?";
    Connection con = null;
    PreparedStatement pstmt = null;

    try {
      con = getConnection();
      pstmt = con.prepareStatement(sql);
      pstmt.setInt(1, money);
      pstmt.setString(2, memberId);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      throw new MyDbException(e);
    } finally {
      close(con, pstmt, null);
    }
  }

  @Override
  public void deleteById(String memberId) {
    String sql = "DELETE FROM member WHERE member_id = ?";
    Connection con = null;
    PreparedStatement pstmt = null;

    try {
      con = getConnection();
      pstmt = con.prepareStatement(sql);
      pstmt.setString(1, memberId);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      throw new MyDbException(e);
    } finally {
      close(con, pstmt, null);
    }
  }

  public void deleteAll() {
    String sql = "DELETE FROM member";
    Connection con = null;
    PreparedStatement pstmt = null;

    try {
      con = getConnection();
      pstmt = con.prepareStatement(sql);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      log.error("ERROR", e);
    } finally {
      close(con, pstmt, null);
    }
  }

  private Connection getConnection() {
    // 주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
    // ConnectionHolder conHolder = (ConnectionHolder) TransactionSynchronizationManager.getResource(dataSource);
    // getConnection() 내부를 보면 위와 같이 트랜잭션 동기화 매니저로부터 커넥션을 얻는다.
    return DataSourceUtils.getConnection(dataSource);
  }

  private void close(Connection con, Statement stmt, ResultSet rs) {
    JdbcUtils.closeResultSet(rs);
    JdbcUtils.closeStatement(stmt);
    // 주의! 트랜잭션 동기화를 사용하려면 DataSourceUtils를 사용해야 한다.
    DataSourceUtils.releaseConnection(con, dataSource);
  }

}
