package com.networknt.saga.participant;


import com.networknt.saga.common.LockTarget;
import com.networknt.saga.common.SagaCommandHeaders;
import com.networknt.saga.common.SagaReplyHeaders;
import com.networknt.saga.common.SagaUnlockCommand;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.tram.command.common.ChannelMapping;
import com.networknt.tram.command.common.CommandMessageHeaders;
import com.networknt.tram.command.consumer.*;
import com.networknt.tram.message.common.Message;
import com.networknt.tram.message.consumer.MessageConsumer;
import com.networknt.tram.message.producer.MessageBuilder;
import com.networknt.tram.message.producer.MessageProducer;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SagaCommandDispatcher extends CommandDispatcher {

  private SagaLockManager sagaLockManager = (SagaLockManager) SingletonServiceFactory.getBean(SagaLockManager.class);;

  public SagaCommandDispatcher(String commandDispatcherId, CommandHandlers target, ChannelMapping channelMapping,
                               MessageConsumer messageConsumer, MessageProducer messageProducer, SagaLockManager sagaLockManager) {
    super(commandDispatcherId, target, channelMapping, messageConsumer, messageProducer);
    this.sagaLockManager = sagaLockManager;
  }

  @Override
  public void messageHandler(Message message) {
    System.out.println("Message  -----------------------> " + message.getPayload());
    if (isUnlockMessage(message)) {
      String sagaType = getSagaType(message);
      String sagaId = getSagaId(message);
      String target = message.getRequiredHeader(CommandMessageHeaders.RESOURCE);
      sagaLockManager.unlock(sagaId, target).ifPresent(m -> super.messageHandler(message));
    } else {
      try {
        super.messageHandler(message);
      } catch (StashMessageRequiredException e) {
        String sagaType = getSagaType(message);
        String sagaId = getSagaId(message);
        String target = e.getTarget();
        sagaLockManager.stashMessage(sagaType, sagaId, target, message);
      }
    }
  }

  private String getSagaId(Message message) {
    return message.getRequiredHeader(SagaCommandHeaders.SAGA_ID);
  }

  private String getSagaType(Message message) {
    return message.getRequiredHeader(SagaCommandHeaders.SAGA_TYPE);
  }


  @Override
  protected List<Message> invoke(CommandHandler commandHandler, CommandMessage cm, Map<String, String> pathVars) {
    Optional<String> lockedTarget = Optional.empty();
    if (commandHandler instanceof SagaCommandHandler) {
      SagaCommandHandler sch = (SagaCommandHandler) commandHandler;
      if (sch.getPreLock().isPresent()) {
        LockTarget lockTarget = sch.getPreLock().get().apply(cm, new PathVariables(pathVars)); // TODO
        Message message = cm.getMessage();
        String sagaType = getSagaType(message);
        String sagaId = getSagaId(message);
        String target = lockTarget.getTarget();
        lockedTarget = Optional.of(target);
        if (!sagaLockManager.claimLock(sagaType, sagaId, target))
          throw new StashMessageRequiredException(target);
      }
    }

    List<Message> messages = super.invoke(commandHandler, cm, pathVars);

    if (lockedTarget.isPresent())
      return addLockedHeader(messages, lockedTarget.get());
    else {
      Optional<LockTarget> lt = getLock(messages);
      if (lt.isPresent()) {
        Message message = cm.getMessage();
        String sagaType = getSagaType(message);
        String sagaId = getSagaId(message);
        if (!sagaLockManager.claimLock(sagaType, sagaId, lt.get().getTarget())) {
          throw new IllegalArgumentException(sagaId + " must be claim locked");
        }

        return addLockedHeader(messages, lt.get().getTarget());
      }
      else
        return messages;
    }
  }

  private Optional<LockTarget> getLock(List<Message> messages) {
    return messages.stream().filter(m -> m instanceof SagaReplyMessage && ((SagaReplyMessage) m).hasLockTarget()).findFirst().flatMap(m -> ((SagaReplyMessage)m).getLockTarget());
  }

  private List<Message> addLockedHeader(List<Message> messages, String lockedTarget) {
    // TODO - what about the isEmpty case??
    // TODO - sagas must return messages
    return messages.stream().map(m -> MessageBuilder.withMessage(m).withHeader(SagaReplyHeaders.REPLY_LOCKED, lockedTarget).build()).collect(Collectors.toList());
  }


  private boolean isUnlockMessage(Message message) {
    return message.getRequiredHeader(CommandMessageHeaders.COMMAND_TYPE).equals(SagaUnlockCommand.class.getName());
  }

}
