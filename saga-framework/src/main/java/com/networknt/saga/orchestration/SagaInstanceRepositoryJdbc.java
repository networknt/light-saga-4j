package com.networknt.saga.orchestration;


import com.networknt.eventuate.jdbc.IdGenerator;
import com.networknt.eventuate.jdbc.IdGeneratorImpl;
import com.networknt.service.SingletonServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SagaInstanceRepositoryJdbc implements SagaInstanceRepository {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private DataSource dataSource = SingletonServiceFactory.getBean(DataSource.class);
  private IdGenerator idGenerator = SingletonServiceFactory.getBean(IdGenerator.class);

  public SagaInstanceRepositoryJdbc() {
  }

  @Override
  public void save(SagaInstance sagaInstance) {

    Objects.requireNonNull(sagaInstance);

    String psInsert = "INSERT INTO saga_instance(saga_type, saga_id, state_name, last_request_id, saga_data_type, saga_data_json) VALUES(?, ?, ?,?,?,?)";
    int count = 0;
    sagaInstance.setId(idGenerator.genId().asString());
    logger.info("Saving {} {}", sagaInstance.getSagaType(), sagaInstance.getId());

    try (final Connection connection = dataSource.getConnection()) {
      connection.setAutoCommit(false);
      PreparedStatement stmt = connection.prepareStatement(psInsert);
      stmt.setString(1, sagaInstance.getSagaType());
      stmt.setString(2, sagaInstance.getId());
      stmt.setString(3, sagaInstance.getStateName());
      stmt.setString(4, sagaInstance.getLastRequestId());
      stmt.setString(5, sagaInstance.getSerializedSagaData().getSagaDataType());
      stmt.setString(6, sagaInstance.getSerializedSagaData().getSagaDataJSON());

      count = stmt.executeUpdate();

      if (count != 1) {
        logger.error("Failed to insert Saga instance: {}", sagaInstance.getSerializedSagaData().getSagaDataJSON());
      }
      saveDestinationsAndResources(connection, sagaInstance);
      connection.commit();
    } catch (SQLException e) {
      logger.error("SqlException:", e);
    }

  }

  private void saveDestinationsAndResources(Connection connection, SagaInstance sagaInstance) throws SQLException{
    String psInsert= "INSERT INTO saga_instance_participants(saga_type, saga_id, destination, resource) values(?,?,?,?)";
    try (PreparedStatement ps = connection.prepareStatement(psInsert)) {

      for (DestinationAndResource dr : sagaInstance.getDestinationsAndResources()) {
        if (!duplicatedDestinationsAndResources(connection, dr, sagaInstance.getSagaType(), sagaInstance.getId())) {
          ps.setString(1, sagaInstance.getSagaType());
          ps.setString(2, sagaInstance.getId());
          ps.setString(3, dr.getDestination());
          ps.setString(4, dr.getResource());
          ps.addBatch();
        }

      }
      ps.executeBatch();
    }
  }

  private boolean duplicatedDestinationsAndResources(Connection connection, DestinationAndResource dr, String sagaType, String sagaId) throws SQLException{
    boolean isDuplicated = false;
    String psSelect = "SELECT count(*) as num FROM saga_instance_participants WHERE saga_type = ? AND saga_id = ? and destination =? and resource = ?";
    PreparedStatement ps = connection.prepareStatement(psSelect);
    ps.setString(1, sagaType);
    ps.setString(2, sagaId);
    ps.setString(3, dr.getDestination());
    ps.setString(4, dr.getResource());
    ResultSet rs = ps.executeQuery();
    while (rs.next()) {
      if (rs.getInt("num")>0)  isDuplicated = true;
    }
    return isDuplicated;
  }


  @Override
  public SagaInstance find(String sagaType, String sagaId) {
    Objects.requireNonNull(sagaType);
    Objects.requireNonNull(sagaId);
    logger.info("finding {} {}", sagaType, sagaId);
    SagaInstance sagaInstance = null;
    String psSelect = "SELECT saga_type,saga_id,state_name,last_request_id, saga_data_type, saga_data_json   FROM saga_instance WHERE saga_type = ? AND saga_id = ?";
    String psSelect_dest = "SELECT destination, resource FROM saga_instance_participants WHERE saga_type = ? AND saga_id = ?";

    try (final Connection connection = dataSource.getConnection()) {
      PreparedStatement stmt = connection.prepareStatement(psSelect_dest);
      stmt.setString(1, sagaType);
      stmt.setString(2, sagaId);
      ResultSet rs2 = stmt.executeQuery();
      Set<DestinationAndResource> destinationsAndResources = new HashSet<>();
      while (rs2.next()) {
        DestinationAndResource destinationsAndResource = new DestinationAndResource(rs2.getString("destination"), rs2.getString("resource"));
        destinationsAndResources.add(destinationsAndResource);
      }
      stmt = connection.prepareStatement(psSelect);
      stmt.setString(1, sagaType);
      stmt.setString(2, sagaId);
      ResultSet rs = stmt.executeQuery();
      if (rs == null || rs.getFetchSize() > 1) {
        logger.error("incorrect fetch result {}, {}", sagaType, sagaId);
      } else {
        while (rs.next()) {
          sagaInstance = new SagaInstance(sagaType, sagaId, rs.getString("state_name"), rs.getString("last_request_id"),
                  new SerializedSagaData(rs.getString("saga_data_type"), rs.getString("saga_data_json")), destinationsAndResources);
        }
      }
    } catch (SQLException e) {
      logger.error("SqlException:", e);
    }

    return sagaInstance;
    // TODO insert - sagaInstance.getDestinationsAndResources();
  }

  @Override
  public void update(SagaInstance sagaInstance) {

    String psUpdate = "UPDATE saga_instance SET state_name = ?, last_request_id = ?, saga_data_type = ?, saga_data_json = ? where saga_type = ? AND saga_id = ?";
    int count = 0;
    logger.info("Update {} {}", sagaInstance.getSagaType(), sagaInstance.getId());

    try (final Connection connection = dataSource.getConnection()) {
      connection.setAutoCommit(false);
      PreparedStatement stmt = connection.prepareStatement(psUpdate);
      stmt.setString(1, sagaInstance.getStateName());
      stmt.setString(2, sagaInstance.getLastRequestId());
      stmt.setString(3, sagaInstance.getSerializedSagaData().getSagaDataType());
      stmt.setString(4, sagaInstance.getSerializedSagaData().getSagaDataJSON());
      stmt.setString(5, sagaInstance.getSagaType());
      stmt.setString(6, sagaInstance.getId());

      count = stmt.executeUpdate();

      if (count != 1) {
        logger.error("Failed to update Saga instance: {}", sagaInstance.getSerializedSagaData().getSagaDataJSON());
      }
      saveDestinationsAndResources(connection, sagaInstance);
      connection.commit();
    } catch (SQLException e) {
      logger.error("SqlException:", e);
    }


  }

  @Override
  public <Data> SagaInstanceData<Data> findWithData(String sagaType, String sagaId) {
    SagaInstance sagaInstance = find(sagaType, sagaId);
    Data sagaData = SagaDataSerde.deserializeSagaData(sagaInstance.getSerializedSagaData());
    return new SagaInstanceData<>(sagaInstance, sagaData);
  }

}
