package com.networknt.saga.orderservice;

import com.networknt.saga.core.command.common.ChannelMapping;
import com.networknt.saga.core.command.common.DefaultChannelMapping;
import com.networknt.saga.core.message.consumer.MessageConsumer;
import com.networknt.saga.orchestration.Saga;
import com.networknt.saga.orchestration.SagaManager;
import com.networknt.saga.orchestration.SagaManagerImpl;
import com.networknt.saga.orderservice.order.saga.createorder.CreateOrderSagaData;
import com.networknt.service.SingletonServiceFactory;


/**
 * Created by chenga on 2017-11-09.
 */
public class ComponentFactory {

   // private MessageConsumer messageConsumer =  (MessageConsumer) SingletonServiceFactory.getBean(MessageConsumer.class);

   // TramCommandsAndEventsIntegrationData tramCommandsAndEventsIntegrationData =  new TramCommandsAndEventsIntegrationData();
    public static ChannelMapping getChannelMapping(TramCommandsAndEventsIntegrationData data) {
        return DefaultChannelMapping.builder()
                .with("CustomerAggregate", data.getAggregateDestination())
                .with("customerService", data.getCommandChannel())
                .build();
    }

    public static SagaManager<CreateOrderSagaData> getSagaManager(Saga<CreateOrderSagaData> saga) {
        return new SagaManagerImpl<>(saga, getChannelMapping(new TramCommandsAndEventsIntegrationData()), (MessageConsumer) SingletonServiceFactory.getBean(MessageConsumer.class));
    }


    }
