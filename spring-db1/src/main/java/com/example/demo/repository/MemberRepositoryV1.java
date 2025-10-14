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
* JDBC - DataSource 사용, JdbcUtils 사용
* 구현체인 DriverManager로 커넥션을 얻던 방식을 추상화된 인터페이스인 DataSource에 의존하도록 수정
* 실제 구현체는 Configuration 클래스에서 설정 -> Hikari CP로 주입
* JdbcUtils를 이용해서 커넥션 닫음
* 트랜잭션 처리를 위한 커넥션 공유가 불가능함
* */
@Slf4j
@RequiredArgsConstructor
public class MemberRepositoryV1 {

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

  private Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

  private void close(Connection con, Statement stmt, ResultSet rs) {
    JdbcUtils.closeResultSet(rs);
    JdbcUtils.closeStatement(stmt);
    JdbcUtils.closeConnection(con);
  }

}
