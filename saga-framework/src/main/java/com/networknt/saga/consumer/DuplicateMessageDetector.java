package com.networknt.saga.consumer;

public interface DuplicateMessageDetector {

  boolean isDuplicate(String consumerId, String messageId);
}
