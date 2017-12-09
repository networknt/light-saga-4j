package com.networknt.saga.dsl;


import com.networknt.tram.command.consumer.CommandWithDestination;
import com.networknt.tram.message.common.Message;

public interface ParticipantInvocation<Data> {
  boolean isSuccessfulReply(Message message);

  CommandWithDestination makeCommandToSend(Data data);
}
