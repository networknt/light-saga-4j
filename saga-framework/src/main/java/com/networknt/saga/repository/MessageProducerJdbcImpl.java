package com.networknt.saga.repository;


import com.networknt.eventuate.common.impl.JSonMapper;
import com.networknt.eventuate.jdbc.IdGenerator;
import com.networknt.eventuate.jdbc.IdGeneratorImpl;
import com.networknt.saga.core.message.common.Message;
import com.networknt.saga.core.message.producer.MessageProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class MessageProducerJdbcImpl implements MessageProducer {

  protected Logger logger = LoggerFactory.getLogger(getClass());

  private DataSource dataSource;
  private IdGenerator idGenerator = new IdGeneratorImpl();

  public MessageProducerJdbcImpl(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public void setDataSource(DataSource dataSource) {this.dataSource = dataSource;}


  @Override
  public void send(String destination, Message message) {

    Objects.requireNonNull(destination);
    Objects.requireNonNull(message);

    String psInsert = "insert into message(id, destination, headers, payload) values(?, ?, ?, ?)";
    int count = 0;
    String id = idGenerator.genId().asString();
    message.getHeaders().put(Message.ID, id);

    try (final Connection connection = dataSource.getConnection()) {

      PreparedStatement stmt = connection.prepareStatement(psInsert);
      stmt.setString(1, id);
      stmt.setString(2, destination);
      stmt.setString(3, JSonMapper.toJson(message.getHeaders()));
      stmt.setString(4, message.getPayload());

      count = stmt.executeUpdate();

      if (count != 1) {
        logger.error("Failed to insert Message: {}", message.getPayload());
      }
    } catch (SQLException e) {
      logger.error("SqlException:", e);
    }

  }

}
