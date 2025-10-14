package com.example.demo.repository;

import static com.example.demo.connection.ConnectionConst.PASSWORD;
import static com.example.demo.connection.ConnectionConst.URL;
import static com.example.demo.connection.ConnectionConst.USERNAME;

import com.example.demo.domain.Member;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;

/*
* JDBC - DriverManager 사용
* 매 요청마다 새로운 커넥션을 직접 생성하고 닫음
* 트랜잭션 처리를 위한 커넥션 공유가 불가능함
* */
@Slf4j
public class MemberRepositoryV0 {

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

  private Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL, USERNAME, PASSWORD);
  }

  private void close(Connection con, Statement stmt, ResultSet rs) {
    if (rs != null) {
      try {
        rs.close();
      } catch (SQLException e) {
        log.info("ERROR", e);
      }
    }

    if (stmt != null) {
      try {
        stmt.close();
      } catch (SQLException e) {
        log.info("ERROR", e);
      }
    }

    if (con != null) {
      try {
        con.close();
      } catch(SQLException e) {
        log.info("ERROR", e);
      }
    }
  }

}
