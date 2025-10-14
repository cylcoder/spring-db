package com.example.demo.repository;

import com.example.demo.domain.Member;
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

/*
* 트랜잭션 - 트랜잭션 매니저
* DataSourceUtils.getConnection()
* DataSourceUtils.releaseConnection()
* DataSourceUtils를 통한 동일한 커넥션 획득 -> 더이상 파라미터로 전달받지 않아도 됨
* */
@Slf4j
@RequiredArgsConstructor
public class MemberRepositoryV3 {

  private final DataSource dataSource;

  public Member save(Member member) throws SQLException {
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
      log.error("ERROR", e);
      throw e;
    } finally {
      close(con, pstmt, null);
    }
  }

  public Member findById(String memberId) throws SQLException {
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
      log.error("ERROR", e);
      throw e;
    } finally {
      close(con, pstmt, rs);
    }
  }

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
      log.error("ERROR", e);
    } finally {
      close(con, pstmt, null);
    }
  }

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
      log.error("ERROR", e);
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
