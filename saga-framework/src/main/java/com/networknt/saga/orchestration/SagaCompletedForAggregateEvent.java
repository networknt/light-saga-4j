package com.networknt.saga.orchestration;


import com.networknt.tram.event.common.DomainEvent;

public class SagaCompletedForAggregateEvent implements DomainEvent {
  public SagaCompletedForAggregateEvent(String sagaId) {
  }
}
