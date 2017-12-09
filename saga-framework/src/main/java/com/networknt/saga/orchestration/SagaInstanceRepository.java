package com.networknt.saga.orchestration;


public interface SagaInstanceRepository {

  void save(SagaInstance sagaInstance);
  SagaInstance find(String sagaType, String sagaId);
  void update(SagaInstance sagaInstance);

  <Data> SagaInstanceData<Data> findWithData(String sagaType, String sagaId);
}
