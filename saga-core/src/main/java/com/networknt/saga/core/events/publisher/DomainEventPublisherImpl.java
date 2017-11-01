package com.networknt.saga.core.events.publisher;


import com.networknt.eventuate.common.impl.JSonMapper;
import com.networknt.saga.core.events.common.DomainEvent;
import com.networknt.saga.core.events.common.EventMessageHeaders;
import com.networknt.saga.core.message.common.Message;
import com.networknt.saga.core.message.producer.MessageBuilder;
import com.networknt.saga.core.message.producer.MessageProducer;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DomainEventPublisherImpl implements DomainEventPublisher {

  private MessageProducer messageProducer;

  public DomainEventPublisherImpl(MessageProducer messageProducer) {
    this.messageProducer = messageProducer;
  }

  @Override
  public void publish(String aggregateType, Object aggregateId, List<DomainEvent> domainEvents) {
    publish(aggregateType, aggregateId, Collections.emptyMap(), domainEvents);
  }

  @Override
  public void publish(String aggregateType, Object aggregateId, Map<String, String> headers, List<DomainEvent> domainEvents) {
    for (DomainEvent event : domainEvents) {
      messageProducer.send(aggregateType,
              makeMessageForDomainEvent(aggregateType, aggregateId, headers, event));

    }
  }

  public static Message makeMessageForDomainEvent(String aggregateType, Object aggregateId, Map<String, String> headers, DomainEvent event) {
    String aggregateIdAsString = aggregateId.toString();
    return MessageBuilder
            .withPayload(JSonMapper.toJson(event))
            .withExtraHeaders("", headers)
            .withHeader(Message.PARTITION_ID, aggregateIdAsString)
            .withHeader(EventMessageHeaders.AGGREGATE_ID, aggregateIdAsString)
            .withHeader(EventMessageHeaders.AGGREGATE_TYPE, aggregateType)
            .withHeader(EventMessageHeaders.EVENT_TYPE, event.getClass().getName())
            .build();
  }
}
