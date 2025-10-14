package hello.itemservice.repository.jdbctemplate;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.StringUtils;

@Slf4j
public class JdbcTemplateItemRepositoryV1 implements ItemRepository {

  /*
   * jdbcTemplate.update() -> CUD(INSERT, UPDATE, DELETE)
   * jdbcTemplate.queryForObject() -> 단일 행 SELECT
   * jdbcTemplate.query() -> 여러 행 SELECT
   * */

  private final JdbcTemplate jdbcTemplate;

  public JdbcTemplateItemRepositoryV1(DataSource dataSource) {
    jdbcTemplate = new JdbcTemplate(dataSource);
  }

  @Override
  public Item save(Item item) {
    String sql = "INSERT INTO item(item_name, price, quantity) VALUES (?, ?, ?)";
    // DB에서 생성된 키를 받아올 때 사용하는 객체
    // INSERT 실행 후 DB가 생성한 ID를 keyholder가 내부적으로 보관
    KeyHolder keyHolder = new GeneratedKeyHolder();
    // SQL 실행 후 DB가 생성한 키를 keyholder에 저장
    jdbcTemplate.update(connection -> {
      // 자동 증가 키
      // DB가 생성한 키를 JDBC가 린터받을 수 있도록 지정
      PreparedStatement pstmt = connection.prepareStatement(sql, new String[]{"id"});
      pstmt.setString(1, item.getItemName());
      pstmt.setInt(2, item.getPrice());
      pstmt.setInt(3, item.getQuantity());
      return pstmt;
    }, keyHolder);

    long key = keyHolder.getKey().longValue();
    item.setId(key);
    return item;
  }

  @Override
  public void update(Long itemId, ItemUpdateDto updateParam) {
    String sql = "UPDATE item SET item_name = ?, price = ?, quantity = ? WHERE id = ?";
    jdbcTemplate.update(
        sql,
        updateParam.getItemName(),
        updateParam.getPrice(),
        updateParam.getQuantity(),
        itemId
    );
  }

  @Override
  public Optional<Item> findById(Long id) {
    String sql = "SELECT id, item_name, price, quantity FROM item WHERE id = ?";
    try {
      Item item = jdbcTemplate.queryForObject(sql, itemRowMapper(), id);
      return Optional.of(item);
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @Override
  public List<Item> findAll(ItemSearchCond cond) {
    String itemName = cond.getItemName();
    Integer maxPrice = cond.getMaxPrice();
    String sql = "SELECT id, item_name, price, quantity FROM item";

    //동적 쿼리
    if (StringUtils.hasText(itemName) || maxPrice != null) {
      sql += " where";
    }
    boolean andFlag = false;
    List<Object> param = new ArrayList<>();
    if (StringUtils.hasText(itemName)) {
      sql += " item_name like concat('%',?,'%')";
      param.add(itemName);
      andFlag = true;
    }
    if (maxPrice != null) {
      if (andFlag) {
        sql += " and";
      }
      sql += " price <= ?";
      param.add(maxPrice);
    }
    log.info("sql={}", sql);

    return jdbcTemplate.query(sql, itemRowMapper(), param.toArray());
  }

  private RowMapper<Item> itemRowMapper() {
    return ((rs, rowNum) -> {
      Item item = new Item();
      item.setId(rs.getLong("id"));
      item.setItemName(rs.getString("item_name"));
      item.setPrice(rs.getInt("price"));
      item.setQuantity(rs.getInt("quantity"));
      return item;
    });
  }

}
