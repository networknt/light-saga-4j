package com.networknt.saga.orderservice;

import com.networknt.saga.core.command.common.ChannelMapping;
import com.networknt.saga.core.command.common.DefaultChannelMapping;
import com.networknt.saga.orchestration.Saga;
import com.networknt.saga.orchestration.SagaManager;
import com.networknt.saga.orchestration.SagaManagerImpl;
import com.networknt.saga.orderservice.order.saga.createorder.CreateOrderSagaData;


/**
 * Created by chenga on 2017-11-09.
 */
public class ComponentFactory {


   // TramCommandsAndEventsIntegrationData tramCommandsAndEventsIntegrationData =  new TramCommandsAndEventsIntegrationData();
    public static ChannelMapping getChannelMapping(TramCommandsAndEventsIntegrationData data) {
        return DefaultChannelMapping.builder()
                .with("CustomerAggregate", data.getAggregateDestination())
                .with("customerService", data.getCommandChannel())
                .build();
    }

    public static SagaManager<CreateOrderSagaData> getSagaManager(Saga<CreateOrderSagaData> saga) {
        return new SagaManagerImpl<>(saga, getChannelMapping(new TramCommandsAndEventsIntegrationData()));
    }


    }
