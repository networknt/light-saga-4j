package com.networknt.saga.core.events.publisher;



import com.networknt.saga.core.events.common.DomainEvent;

import java.util.List;
import java.util.Map;

public interface DomainEventPublisher {

  void publish(String aggregateType, Object aggregateId, List<DomainEvent> domainEvents);
  void publish(String aggregateType, Object aggregateId, Map<String, String> headers, List<DomainEvent> domainEvents);

  default void publish(Class<?> aggregateType, Object aggregateId, List<DomainEvent> domainEvents) {
    publish(aggregateType.getName(), aggregateId, domainEvents);
  }
}
