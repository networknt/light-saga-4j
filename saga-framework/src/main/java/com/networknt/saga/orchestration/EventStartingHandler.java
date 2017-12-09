package com.networknt.saga.orchestration;


import com.networknt.tram.event.common.DomainEvent;
import com.networknt.tram.event.subscriber.DomainEventEnvelope;

public interface EventStartingHandler<Data, EventClass extends DomainEvent> {
  void apply(Data data, DomainEventEnvelope<EventClass> event);
}
