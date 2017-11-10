package com.networknt.saga.orderservice;

import com.networknt.saga.core.command.common.ChannelMapping;
import com.networknt.saga.core.command.common.DefaultChannelMapping;


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


}
