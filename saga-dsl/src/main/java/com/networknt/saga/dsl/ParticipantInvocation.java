package com.networknt.saga.dsl;


import com.networknt.saga.core.command.consumer.CommandWithDestination;
import com.networknt.saga.core.message.common.Message;

public interface ParticipantInvocation<Data> {
  boolean isSuccessfulReply(Message message);

  CommandWithDestination makeCommandToSend(Data data);
}
