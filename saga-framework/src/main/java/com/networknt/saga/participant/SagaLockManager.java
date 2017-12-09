package com.networknt.saga.participant;


import com.networknt.tram.message.common.Message;

import java.util.Optional;

public interface SagaLockManager {

  boolean claimLock(String sagaType, String sagaId, String target);

  void stashMessage(String sagaType, String sagaId, String target, Message message);

  Optional<Message> unlock(String sagaId, String target);
}
