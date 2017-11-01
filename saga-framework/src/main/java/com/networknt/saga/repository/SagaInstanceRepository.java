package com.networknt.saga.repository;

import com.networknt.saga.orchestration.SagaInstance;
import com.networknt.saga.orchestration.SagaInstanceData;

public interface SagaInstanceRepository {

  void save(SagaInstance sagaInstance);
  SagaInstance find(String sagaType, String sagaId);
  void update(SagaInstance sagaInstance);

  <Data> SagaInstanceData<Data> findWithData(String sagaType, String sagaId);
}
