package com.networknt.saga.orderservice.order.saga.createorder;


import com.networknt.saga.dsl.SimpleSaga;
import com.networknt.saga.orchestration.SagaDefinition;
import com.networknt.saga.orderservice.common.Money;
import com.networknt.saga.orderservice.order.saga.participants.ApproveOrderCommand;
import com.networknt.saga.orderservice.order.saga.participants.RejectOrderCommand;
import com.networknt.saga.orderservice.order.saga.participants.ReserveCreditCommand;
import com.networknt.tram.command.consumer.CommandWithDestination;
import com.networknt.tram.command.consumer.CommandWithDestinationBuilder;

public class CreateOrderSaga implements SimpleSaga<CreateOrderSagaData> {

  private SagaDefinition<CreateOrderSagaData> sagaDefinition =
          step()
            .withCompensation(this::reject)
          .step()
            .invokeParticipant(this::reserveCredit)
          .step()
            .invokeParticipant(this::approve)
          .build();


  @Override
  public SagaDefinition<CreateOrderSagaData> getSagaDefinition() {
    return this.sagaDefinition;
  }


  private CommandWithDestination reserveCredit(CreateOrderSagaData data) {
    long orderId = data.getOrderId();
    Long customerId = data.getOrderDetails().getCustomerId();
    Money orderTotal = data.getOrderDetails().getOrderTotal();
    return CommandWithDestinationBuilder.send(new ReserveCreditCommand(customerId, orderId, orderTotal))
            .to("customerService")
            .build();
  }

  public CommandWithDestination reject(CreateOrderSagaData data) {
    return CommandWithDestinationBuilder.send(new RejectOrderCommand(data.getOrderId()))
            .to("orderService")
            .build();
  }

  private CommandWithDestination approve(CreateOrderSagaData data) {
    return CommandWithDestinationBuilder.send(new ApproveOrderCommand(data.getOrderId()))
            .to("orderService")
            .build();
  }


}
