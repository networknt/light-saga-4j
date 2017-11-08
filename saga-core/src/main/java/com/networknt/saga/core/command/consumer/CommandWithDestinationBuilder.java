package com.networknt.saga.core.command.consumer;


import com.networknt.eventuate.common.Command;
import com.networknt.saga.core.command.common.paths.ResourcePathPattern;

public class CommandWithDestinationBuilder {
  private Command command;
  private String destinationChannel;
  private String resource;

  public CommandWithDestinationBuilder(Command command) {
    this.command = command;
  }

  public static CommandWithDestinationBuilder send(Command command) {
    return new CommandWithDestinationBuilder(command);
  }

  public CommandWithDestinationBuilder to(String destinationChannel) {
    this.destinationChannel = destinationChannel;
    return this;
  }

  public CommandWithDestinationBuilder forResource(String resource, Object... pathParams) {
    this.resource = new ResourcePathPattern(resource).replacePlaceholders(pathParams).toPath();
    return this;
  }

  public CommandWithDestination build() {
    return new CommandWithDestination(destinationChannel, resource, command);
  }
}
