package com.networknt.saga.repository;


import com.networknt.saga.orchestration.EventClassAndAggregateId;
import com.networknt.saga.orchestration.SagaTypeAndId;

import java.util.List;

public interface AggregateInstanceSubscriptionsDAO {


  public void update(String sagaType, String sagaId, List<EventClassAndAggregateId> eventHandlers);

  public List<SagaTypeAndId> findSagas(String aggregateType, String aggregateId, String eventType);
}
