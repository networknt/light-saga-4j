package com.networknt.saga.core.message.common;

import java.util.Map;
import java.util.Optional;

/**
 * A message
 * @see com.networknt.saga.core.message.producer.MessageBuilder
 */
public interface Message {
  String ID = "ID";
  String PARTITION_ID = "PARTITION_ID";

  String getId();

  Map<String, String> getHeaders();
  String getPayload();

  Optional<String> getHeader(String name);
  String getRequiredHeader(String name);

  boolean hasHeader(String name);
}
