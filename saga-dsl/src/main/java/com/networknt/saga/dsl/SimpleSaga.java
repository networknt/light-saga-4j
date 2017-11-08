package com.networknt.saga.dsl;


import com.networknt.saga.orchestration.Saga;

public interface SimpleSaga<Data> extends Saga<Data>, SimpleSagaDsl<Data> {
}
