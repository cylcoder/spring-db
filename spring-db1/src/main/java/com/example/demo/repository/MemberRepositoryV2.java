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
import org.springframework.jdbc.support.JdbcUtils;

/*
* JDBC - ConnectionParam
* 파라미터를 통해 커넥션을 공유하여 트랜잭션 처리함
* 서비스 레이어에서 커넥션을 들고 있다가 인자로 전달해서 동일한 커넥션을 사용
* */
@Slf4j
@RequiredArgsConstructor
public class MemberRepositoryV2 {

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

  public Member findById(String memberId) throws Exception {
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

  public Member findById(Connection con, String memberId) throws SQLException {
    String sql = "SELECT * FROM member where member_id = ?";
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
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
      // connection은 여기서 닫지 않는다.
      JdbcUtils.closeResultSet(rs);
      JdbcUtils.closeStatement(pstmt);
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

  public void update(Connection con, String memberId, int money) {
    String sql = "UPDATE member SET money = ? WHERE member_id = ?";
    PreparedStatement pstmt = null;

    try {
      pstmt = con.prepareStatement(sql);
      pstmt.setInt(1, money);
      pstmt.setString(2, memberId);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      log.error("ERROR", e);
    } finally {
      // connection은 여기서 닫지 않는다.
      JdbcUtils.closeStatement(pstmt);
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

  private void close(Connection con, Statement stmt, ResultSet rs) {
    JdbcUtils.closeResultSet(rs);
    JdbcUtils.closeStatement(stmt);
    JdbcUtils.closeConnection(con);
  }

  private Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

}
