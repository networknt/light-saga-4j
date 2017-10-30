package com.networknt.saga.core.command.comsumer;


import com.networknt.eventuate.common.impl.JSonMapper;
import com.networknt.saga.core.command.common.CommandReplyOutcome;
import com.networknt.saga.core.command.common.Failure;
import com.networknt.saga.core.command.common.ReplyMessageHeaders;
import com.networknt.saga.core.command.common.Success;
import com.networknt.saga.core.message.common.Message;
import com.networknt.saga.core.message.producer.MessageBuilder;

public class CommandHandlerReplyBuilder {


  private static <T> Message with(T reply, CommandReplyOutcome outcome) {
    MessageBuilder messageBuilder = MessageBuilder
            .withPayload(JSonMapper.toJson(reply))
            .withHeader(ReplyMessageHeaders.REPLY_OUTCOME, outcome.name())
            .withHeader(ReplyMessageHeaders.REPLY_TYPE, reply.getClass().getName());
    return messageBuilder.build();
  }

  public static Message withSuccess(Object reply) {
    return with(reply, CommandReplyOutcome.SUCCESS);
  }

  public static Message withSuccess() {
    return withSuccess(new Success());
  }

  public static Message withFailure() {
    return withFailure(new Failure());
  }
  public static Message withFailure(Object reply) {
    return with(reply, CommandReplyOutcome.FAILURE);
  }

}
