package com.networknt.saga.orderservice.order.service;


import com.networknt.saga.core.command.consumer.CommandHandlers;
import com.networknt.saga.core.command.consumer.CommandMessage;
import com.networknt.saga.core.message.common.Message;
import com.networknt.saga.orderservice.order.domain.Order;
import com.networknt.saga.orderservice.order.domain.OrderRepository;
import com.networknt.saga.orderservice.order.saga.participants.ApproveOrderCommand;
import com.networknt.saga.orderservice.order.saga.participants.RejectOrderCommand;
import com.networknt.saga.participant.SagaCommandHandlersBuilder;

import static com.networknt.saga.core.command.consumer.CommandHandlerReplyBuilder.withSuccess;

public class OrderCommandHandler {

  private OrderRepository<Order, Long> orderRepository;

  public CommandHandlers commandHandlerDefinitions() {
    return SagaCommandHandlersBuilder
            .fromChannel("orderService")
            .onMessage(ApproveOrderCommand.class, this::approve)
            .onMessage(RejectOrderCommand.class, this::reject)
            .build();
  }

  public Message approve(CommandMessage<ApproveOrderCommand> cm) {
    long orderId = cm.getCommand().getOrderId();
    Order order = orderRepository.findOne(orderId);
    order.noteCreditReserved();
    return withSuccess();
  }

  public Message reject(CommandMessage<RejectOrderCommand> cm) {
    long orderId = cm.getCommand().getOrderId();
    Order order = orderRepository.findOne(orderId);
    order.noteCreditReservationFailed();
    return withSuccess();
  }

}
