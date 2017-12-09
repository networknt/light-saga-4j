package com.networknt.saga.participant;



import com.networknt.eventuate.common.impl.JSonMapper;
import com.networknt.saga.common.LockTarget;
import com.networknt.tram.command.common.CommandReplyOutcome;
import com.networknt.tram.command.common.ReplyMessageHeaders;
import com.networknt.tram.command.common.Success;
import com.networknt.tram.message.common.Message;
import com.networknt.tram.message.producer.MessageBuilder;

import java.util.Optional;


public class SagaReplyMessageBuilder extends MessageBuilder {

  private Optional<LockTarget> lockTarget = Optional.empty();

  public SagaReplyMessageBuilder(LockTarget lockTarget) {
    this.lockTarget = Optional.of(lockTarget);
  }

  public static SagaReplyMessageBuilder withLock(Class type, Object id) {
    return new SagaReplyMessageBuilder(new LockTarget(type, id));
  }

  private <T> Message with(T reply, CommandReplyOutcome outcome) {
    this.body = JSonMapper.toJson(reply);
    withHeader(ReplyMessageHeaders.REPLY_OUTCOME, outcome.name());
    withHeader(ReplyMessageHeaders.REPLY_TYPE, reply.getClass().getName());
    return new SagaReplyMessage(body, headers, lockTarget);
  }

  public Message withSuccess(Object reply) {
    return with(reply, CommandReplyOutcome.SUCCESS);
  }

  public Message withSuccess() {
    return withSuccess(new Success());
  }

}
