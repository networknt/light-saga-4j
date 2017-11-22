package com.networknt.saga.cdc.mysql;


import com.networknt.eventuate.cdc.mysql.PublishingStrategy;
import com.networknt.eventuate.common.impl.JSonMapper;
import com.networknt.saga.core.message.common.Message;

import java.util.Optional;

public class MessageWithDestinationPublishingStrategy implements PublishingStrategy<MessageWithDestination> {

  @Override
  public String partitionKeyFor(MessageWithDestination messageWithDestination) {
    String id = messageWithDestination.getMessage().getId();
    return messageWithDestination.getMessage().getHeader(Message.PARTITION_ID).orElse(id);
  }

  @Override
  public String topicFor(MessageWithDestination messageWithDestination) {
    return messageWithDestination.getDestination();
  }

  @Override
  public String toJson(MessageWithDestination messageWithDestination) {
    return JSonMapper.toJson(messageWithDestination.getMessage());
  }

  @Override
  public Optional<Long> getCreateTime(MessageWithDestination messageWithDestination) {
    return Optional.empty(); // TODO
  }
}
