package com.networknt.saga.common;


import com.networknt.saga.core.command.common.CommandMessageHeaders;

public class SagaCommandHeaders {
  public static final String SAGA_TYPE = CommandMessageHeaders.COMMAND_HEADER_PREFIX + "saga_type";
  public static final String SAGA_ID = CommandMessageHeaders.COMMAND_HEADER_PREFIX + "saga_id";
  public static final String SAGA_REQUEST_ID = CommandMessageHeaders.COMMAND_HEADER_PREFIX + "saga_request_id";

}
