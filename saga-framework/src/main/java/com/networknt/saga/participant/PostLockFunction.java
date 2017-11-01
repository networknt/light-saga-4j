package com.networknt.saga.participant;


import com.networknt.saga.common.LockTarget;
import com.networknt.saga.core.command.consumer.CommandMessage;
import com.networknt.saga.core.command.consumer.PathVariables;
import com.networknt.saga.core.message.common.Message;

public interface PostLockFunction<C> {

  public LockTarget apply(CommandMessage<C> cm, PathVariables pvs, Message reply);
}
