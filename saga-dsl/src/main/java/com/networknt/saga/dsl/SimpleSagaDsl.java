package com.networknt.saga.dsl;

public interface SimpleSagaDsl<Data> {

  default StepBuilder<Data> step() {
    SimpleSagaDefinitionBuilder<Data> builder = new SimpleSagaDefinitionBuilder<>();
    return new StepBuilder<>(builder);
  }

}
