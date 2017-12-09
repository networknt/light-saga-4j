package com.networknt.saga.orchestration;



import java.util.Set;

public interface EnlistedAggregatesDao {


   void save(String sagaId, Set<EnlistedAggregate> enlistedAggregates) ;


   Set<EnlistedAggregate> findEnlistedAggregates(String sagaId);

   Set<String> findSagas(Class aggregateType, String aggregateId);

}
