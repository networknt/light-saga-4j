package com.networknt.saga.dsl;


import com.networknt.saga.orchestration.SagaDefinition;

import java.util.LinkedList;
import java.util.List;

public class SimpleSagaDefinitionBuilder<Data> {

  private List<SagaStep<Data>> sagaSteps = new LinkedList<>();

  public void addStep(SagaStep<Data> sagaStep) {
    sagaSteps.add(sagaStep);
  }

  public SagaDefinition<Data> build() {
    return new SimpleSagaDefinition<>(sagaSteps);
  }
}
