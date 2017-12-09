package com.networknt.saga.orchestration;


import java.util.List;

public interface AggregateInstanceSubscriptionsDAO {


  public void update(String sagaType, String sagaId, List<EventClassAndAggregateId> eventHandlers);

  public List<SagaTypeAndId> findSagas(String aggregateType, String aggregateId, String eventType);
}
