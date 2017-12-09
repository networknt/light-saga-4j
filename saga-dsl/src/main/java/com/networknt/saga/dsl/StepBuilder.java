package com.networknt.saga.dsl;



import com.networknt.eventuate.common.Command;
import com.networknt.tram.command.consumer.CommandWithDestination;

import java.util.function.Consumer;
import java.util.function.Function;

public class StepBuilder<Data>  {

  private final SimpleSagaDefinitionBuilder<Data> parent;

  public StepBuilder(SimpleSagaDefinitionBuilder<Data> builder) {
    this.parent = builder;
  }

  public LocalStepBuilder<Data> invokeLocal(Consumer<Data> localFunction) {
    return new LocalStepBuilder<Data>(parent, localFunction);
  }


  public <C extends Command> InvokeParticipantStepBuilder<Data> invokeParticipant(Function<Data, CommandWithDestination> action) {
    return new InvokeParticipantStepBuilder<>(parent).withAction(action);
  }


  public <C extends Command> InvokeParticipantStepBuilder<Data> withCompensation(Function<Data, CommandWithDestination> compensation) {
    return new InvokeParticipantStepBuilder<>(parent).withCompensation(compensation);
  }
}
