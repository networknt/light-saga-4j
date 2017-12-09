package com.networknt.saga.orchestration;


import com.networknt.tram.event.common.DomainEvent;

public class EventClassAndAggregateId {
  private final Class<DomainEvent> eventClass;
  private final Long aggregateId;

  public EventClassAndAggregateId(Class<DomainEvent> eventClass, Long aggregateId) {
    this.eventClass = eventClass;
    this.aggregateId = aggregateId;
  }

  public Class<DomainEvent> getEventClass() {
    return eventClass;
  }

  public Long getAggregateId() {
    return aggregateId;
  }

  public boolean isFor(String aggregateType, long aggregateId, String eventType) {
    return eventClass.getName().equals(eventType) && this.aggregateId.equals(aggregateId);
  }
}
