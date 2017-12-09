package com.networknt.saga.orchestration;


import com.networknt.tram.event.common.DomainEvent;
import com.networknt.tram.event.subscriber.DomainEventEnvelope;

public interface SagaStateMachineEventHandler<Data, EventClass extends DomainEvent> {

  SagaActions<Data> apply(Data data, DomainEventEnvelope<EventClass> event);


}
