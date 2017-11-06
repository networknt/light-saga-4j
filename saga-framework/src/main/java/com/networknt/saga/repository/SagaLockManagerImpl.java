package com.networknt.saga.repository;

import com.networknt.eventuate.common.impl.JSonMapper;
import com.networknt.eventuate.jdbc.IdGenerator;
import com.networknt.eventuate.jdbc.IdGeneratorImpl;
import com.networknt.saga.core.message.common.Message;
import com.networknt.saga.core.message.producer.MessageBuilder;
import com.networknt.saga.participant.SagaLockManager;

import com.networknt.saga.participant.StashedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SagaLockManagerImpl implements SagaLockManager {

  private Logger logger = LoggerFactory.getLogger(getClass());
  private DataSource dataSource;


  public SagaLockManagerImpl(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public void setDataSource(DataSource dataSource) {this.dataSource = dataSource;}

  @Override
  public boolean claimLock(String sagaType, String sagaId, String target) {
    String psInsert = "INSERT INTO saga_lock_table(target, saga_type, saga_id) VALUES(?, ?,?)";
    while (true) {
      try (final Connection connection = dataSource.getConnection()) {
        PreparedStatement stmt = connection.prepareStatement(psInsert);
        stmt.setString(1, target);
        stmt.setString(2, sagaType);
        stmt.setString(3, sagaId);

        try {
          stmt.executeUpdate();
          return true;
        } catch (SQLException e) {
          Optional<String> owningSagaId = selectForUpdate(target);
          if (owningSagaId.isPresent()) {
            if (owningSagaId.get().equals(sagaId))
              return true;
            else {
              logger.debug("Saga {} {} is blocked by {} which has locked {}", sagaType, sagaId, owningSagaId, target);
              return false;
            }
          }
          logger.debug("{}  is repeating attempt to lock {}", sagaId, target);
        }

      } catch (SQLException e) {
        logger.error("SqlException:", e);
      }
    }
  }

  private Optional<String> selectForUpdate(String target) {
    String psSelect = "select saga_id from saga_lock_table WHERE target = ? FOR UPDATE";

    String saga_id = null;
    try (final Connection connection = dataSource.getConnection()) {

      PreparedStatement ps = connection.prepareStatement(psSelect);
      ps.setString(1, target);

      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        saga_id = rs.getString("saga_id");
      }

    } catch (SQLException e) {
      logger.error("SqlException:", e);
    }
    return Optional.ofNullable(saga_id);
  }

  @Override
  public void stashMessage(String sagaType, String sagaId, String target, Message message) {

    logger.debug("Stashing message from {} for {} : {}", sagaId, target, message);
    String psInsert = "INSERT INTO saga_stash_table(message_id, target, saga_type, saga_id, message_headers, message_payload) VALUES(?, ?,?, ?, ?, ?)";
    int count = 0;

    try (final Connection connection = dataSource.getConnection()) {

      PreparedStatement stmt = connection.prepareStatement(psInsert);
      stmt.setString(1, message.getRequiredHeader(Message.ID));
      stmt.setString(2, target);
      stmt.setString(3, sagaType);
      stmt.setString(4, sagaType);
      stmt.setString(5, JSonMapper.toJson(message.getHeaders()));
      stmt.setString(6, message.getPayload());

      count = stmt.executeUpdate();

      if (count != 1) {
        logger.error("Failed to insert stashMessage: {}", message.getPayload());
      }
    } catch (SQLException e) {
      logger.error("SqlException:", e);
    }
  }

  @Override
  public Optional<Message> unlock(String sagaId, String target) {
    Optional<String> owningSagaId = selectForUpdate(target);
    if (!owningSagaId.isPresent()) {
      throw new IllegalArgumentException("no saga is for unlock");
    }
    if (!owningSagaId.get().equals(sagaId)) {
      throw new IllegalArgumentException(String.format("Expected owner to be %s but is %s", sagaId, owningSagaId.get()));
    }

    logger.debug("Saga {} has unlocked {}", sagaId, target);

    List<StashedMessage> stashedMessages = getStashedMessages(target);

    if (stashedMessages.isEmpty()) {
      assertEqualToOne(lockAndstashDelete("delete from saga_lock_table where target = ?", target));
      return Optional.empty();
    }

    StashedMessage stashedMessage = stashedMessages.get(0);

    logger.debug("unstashed from {}  for {} : {}", sagaId, target, stashedMessage.getMessage());

    assertEqualToOne(stashTableUpdate(stashedMessage.getSagaType(),stashedMessage.getSagaId(), target));

    assertEqualToOne(lockAndstashDelete("delete from saga_stash_table where message_id = ?", stashedMessage.getMessage().getId()));

    return Optional.of(stashedMessage.getMessage());
  }

  protected List<StashedMessage> getStashedMessages(String target) {
    String psSelect = "select message_id, target, saga_type, saga_id, message_headers, message_payload from saga_stash_table WHERE target = ? ORDER BY message_id LIMIT 1";

    List<StashedMessage> stashedMessages = new ArrayList<>();
    try (final Connection connection = dataSource.getConnection()) {

      PreparedStatement ps = connection.prepareStatement(psSelect);
      ps.setString(1, target);

      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        StashedMessage message = new StashedMessage (rs.getString("saga_type"), rs.getString("saga_id"),
                MessageBuilder.withPayload(rs.getString("message_payload")).withExtraHeaders("",
                JSonMapper.fromJson(rs.getString("message_headers"), Map.class)).build());
        stashedMessages.add(message);
      }

    } catch (SQLException e) {
      logger.error("SqlException:", e);
    }
    return stashedMessages;
  }

  protected int lockAndstashDelete(String queryStr, String input) {
    logger.debug("delet call  {} for {} ", queryStr, input);
    int count = 0;

    try (final Connection connection = dataSource.getConnection()) {

      PreparedStatement stmt = connection.prepareStatement(queryStr);
      stmt.setString(1, input);
      count = stmt.executeUpdate();
      if (count != 1) {
        logger.error("Failed to update {} for {}", queryStr, input);
      }
    } catch (SQLException e) {
      logger.error("SqlException:", e);
    }
    return count;
  }

  protected int stashTableUpdate(String sagaType, String sagaId, String target) {
    int count = 0;
    String psUpdate = "update saga_lock_table set saga_type = ?, saga_id = ? where target = ?";

    try (final Connection connection = dataSource.getConnection()) {

      PreparedStatement stmt = connection.prepareStatement(psUpdate);
      stmt.setString(1, sagaType);
      stmt.setString(2, sagaId);
      stmt.setString(3, target);

      count = stmt.executeUpdate();
      if (count != 1) {
        logger.error("Failed to update saga_lock_table {} ,{}, {}", sagaType, sagaId, target);
      }
    } catch (SQLException e) {
      logger.error("SqlException:", e);
    }
    return count;
  }

  private void assertEqualToOne(int n) {
    if (n != 1)
      throw new RuntimeException("Expected to update one row but updated: " + n);
  }
}
