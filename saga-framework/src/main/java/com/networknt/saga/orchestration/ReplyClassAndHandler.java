package com.networknt.saga.orchestration;

public interface ReplyClassAndHandler {
  RawSagaStateMachineAction getReplyHandler();

  Class<?> getReplyClass();
}
