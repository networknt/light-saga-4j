package com.networknt.saga.orderservice;

import com.networknt.saga.orchestration.Saga;
import com.networknt.saga.orchestration.SagaManager;
import com.networknt.saga.orchestration.SagaManagerImpl;
import com.networknt.saga.orderservice.customer.service.CustomerCommandHandler;
import com.networknt.saga.orderservice.order.saga.createorder.CreateOrderSagaData;
import com.networknt.saga.orderservice.order.service.OrderCommandHandler;
import com.networknt.saga.participant.SagaCommandDispatcher;
import com.networknt.saga.participant.SagaLockManager;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.tram.command.common.ChannelMapping;
import com.networknt.tram.command.common.DefaultChannelMapping;
import com.networknt.tram.command.consumer.CommandDispatcher;
import com.networknt.tram.message.consumer.MessageConsumer;
import com.networknt.tram.message.producer.MessageProducer;


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

    public static SagaManager<CreateOrderSagaData> getSagaManager(Saga<CreateOrderSagaData> saga, TramCommandsAndEventsIntegrationData data) {
        return new SagaManagerImpl<>(saga, getChannelMapping(data), (MessageConsumer) SingletonServiceFactory.getBean(MessageConsumer.class));
    }

    public static CommandDispatcher getConsumerCommandDispatcher(CustomerCommandHandler target,
                                                                 SagaLockManager sagaLockManager, TramCommandsAndEventsIntegrationData data) {
        MessageProducer messageProducer =  (MessageProducer) SingletonServiceFactory.getBean(MessageProducer.class);
        MessageConsumer messageConsumer =  (MessageConsumer) SingletonServiceFactory.getBean(MessageConsumer.class);

        return new SagaCommandDispatcher("customerCommandDispatcher", target.commandHandlerDefinitions(),getChannelMapping(data), messageConsumer, messageProducer,sagaLockManager );
    }

    public static CommandDispatcher getOrderCommandDispatcher(OrderCommandHandler target, SagaLockManager sagaLockManager, TramCommandsAndEventsIntegrationData data) {
        MessageProducer messageProducer =  (MessageProducer) SingletonServiceFactory.getBean(MessageProducer.class);
        MessageConsumer messageConsumer =  (MessageConsumer) SingletonServiceFactory.getBean(MessageConsumer.class);

        return new SagaCommandDispatcher("orderCommandDispatcher", target.commandHandlerDefinitions(),getChannelMapping(data), messageConsumer, messageProducer,sagaLockManager );
    }


    }
