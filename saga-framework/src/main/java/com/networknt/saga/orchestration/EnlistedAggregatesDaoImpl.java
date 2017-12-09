package com.networknt.saga.orchestration;


import com.networknt.saga.orchestration.EnlistedAggregate;
import com.networknt.saga.orchestration.EnlistedAggregatesDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class EnlistedAggregatesDaoImpl implements EnlistedAggregatesDao {

    private Logger logger = LoggerFactory.getLogger(getClass());
    private DataSource dataSource;


    public EnlistedAggregatesDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setDataSource(DataSource dataSource) {this.dataSource = dataSource;}


  @Override
  public void save(String sagaId, Set<EnlistedAggregate> enlistedAggregates) {

      String psInsert = "INSERT INTO saga_enlisted_aggregates(saga_id, aggregate_type, aggregate_id) values(?,?,?)";

      for (EnlistedAggregate ela : enlistedAggregates) {

          try (final Connection connection = dataSource.getConnection()) {

              PreparedStatement stmt = connection.prepareStatement(psInsert);
              stmt.setString(1, sagaId);
              stmt.setString(2, ela.getAggregateClass().getName());
              stmt.setString(3, ela.getAggregateId().toString());
              stmt.executeUpdate();
          } catch (SQLException e) {
              logger.error("SqlException:", e);
          }
    }
  }

    @Override
    public Set<EnlistedAggregate> findEnlistedAggregates(String sagaId) {
        return null;
 /*   return new HashSet<>(jdbcTemplate.query("Select aggregate_type, aggregate_id from saga_enlisted_aggregates where saga_id = ?",
            (rs, rowNum) -> {
              try {
                return new EnlistedAggregate((Class) ClassUtils.forName(rs.getString("aggregate_type"), getClass().getClassLoader()), rs.getString("aggregate_id"));
              } catch (ClassNotFoundException e) {
                throw new RuntimeException();
              }
            },
            sagaId));*/
  }

    @Override
    public Set<String> findSagas(Class aggregateType, String aggregateId) {

        String psSelect = "Select saga_id from saga_enlisted_aggregates where aggregate_type = ? AND  aggregate_id = ?";

        Set<String> sagas = new HashSet<>();
        try (final Connection connection = dataSource.getConnection()) {

            PreparedStatement ps = connection.prepareStatement(psSelect);
            ps.setString(1, aggregateType.getName());
            ps.setString(1, aggregateId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                sagas.add(rs.getString("saga_id"));
            }
        } catch (SQLException e) {
            logger.error("SqlException:", e);
        }
        return sagas;

    /*
    return new HashSet<>(jdbcTemplate.query("Select saga_id from saga_enlisted_aggregates where aggregate_type = ? AND  aggregate_id = ?",
            (rs, rowNum) -> {
              return rs.getString("aggregate_type");
            },
            aggregateType, aggregateId));*/
  }
}
