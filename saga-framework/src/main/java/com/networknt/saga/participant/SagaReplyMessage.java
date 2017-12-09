package com.networknt.saga.participant;



import com.networknt.saga.common.LockTarget;
import com.networknt.tram.message.common.MessageImpl;

import java.util.Map;
import java.util.Optional;

public class SagaReplyMessage extends MessageImpl {
  private Optional<LockTarget> lockTarget;

  public SagaReplyMessage(String body, Map<String, String> headers, Optional<LockTarget> lockTarget) {
    super(body, headers);
    this.lockTarget = lockTarget;
  }

  public Optional<LockTarget> getLockTarget() {
    return lockTarget;
  }

  public boolean hasLockTarget() {
    return lockTarget.isPresent();
  }
}
