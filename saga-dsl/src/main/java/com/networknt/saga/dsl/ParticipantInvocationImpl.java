package com.networknt.saga.dsl;


import com.networknt.eventuate.common.Command;
import com.networknt.tram.command.common.CommandReplyOutcome;
import com.networknt.tram.command.common.ReplyMessageHeaders;
import com.networknt.tram.command.consumer.CommandWithDestination;
import com.networknt.tram.message.common.Message;

import java.util.function.Function;

public class ParticipantInvocationImpl<Data, C extends Command> implements ParticipantInvocation<Data> {
  private Function<Data, CommandWithDestination> commandBuilder;


  public ParticipantInvocationImpl(Function<Data, CommandWithDestination> commandBuilder) {
    this.commandBuilder = commandBuilder;
  }

  @Override
  public boolean isSuccessfulReply(Message message) {
    return CommandReplyOutcome.SUCCESS.name().equals(message.getRequiredHeader(ReplyMessageHeaders.REPLY_OUTCOME));
  }

  @Override
  public CommandWithDestination makeCommandToSend(Data data) {
    return commandBuilder.apply(data);
  }
}
