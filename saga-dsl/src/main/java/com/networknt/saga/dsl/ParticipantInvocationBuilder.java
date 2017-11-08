package com.networknt.saga.dsl;


import com.networknt.eventuate.common.Command;

import java.util.Map;

import static java.util.Collections.singletonMap;

public class ParticipantInvocationBuilder {
  private Map<String, String> params;


  public ParticipantInvocationBuilder(String key, String value) {
    this.params = singletonMap(key, value);
  }

  public <C extends Command>  ParticipantParamsAndCommand<C> withCommand(C command) {
    return new ParticipantParamsAndCommand<>(params, command);
  }
}
