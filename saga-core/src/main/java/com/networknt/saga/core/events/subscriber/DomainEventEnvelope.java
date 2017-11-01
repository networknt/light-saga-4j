package com.networknt.saga.core.events.subscriber;


import com.networknt.saga.core.events.common.DomainEvent;
import com.networknt.saga.core.message.common.Message;

public interface DomainEventEnvelope<T extends DomainEvent> {
  String getAggregateId();
  Message getMessage();
  String getAggregateType();
  String getEventId();

  T getEvent();
}
