package com.networknt.saga.core.command.consumer;


import com.networknt.saga.core.message.common.Message;

import java.util.List;

public class CommandExceptionHandler {
  public List<Message> invoke(Throwable cause) {
    throw new UnsupportedOperationException();
  }
}
