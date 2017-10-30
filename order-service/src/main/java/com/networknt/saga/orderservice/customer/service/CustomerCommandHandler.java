package com.networknt.saga.orderservice.customer.service;

import com.networknt.saga.core.command.comsumer.CommandHandlerReplyBuilder;
import com.networknt.saga.core.command.comsumer.CommandHandlers;
import com.networknt.saga.core.command.comsumer.CommandMessage;
import com.networknt.saga.core.message.common.Message;
import com.networknt.saga.orderservice.customer.domain.Customer;
import com.networknt.saga.orderservice.customer.domain.CustomerCreditLimitExceededException;
import com.networknt.saga.orderservice.customer.domain.CustomerRepository;
import com.networknt.saga.orderservice.order.saga.participants.ReserveCreditCommand;
import com.networknt.saga.participant.SagaCommandHandlersBuilder;

public class CustomerCommandHandler {


  private CustomerRepository customerRepository;

  public CommandHandlers commandHandlerDefinitions() {
    return SagaCommandHandlersBuilder
            .fromChannel("customerService")
            .onMessage(ReserveCreditCommand.class, this::reserveCredit)
            .build();
  }

  public Message reserveCredit(CommandMessage<ReserveCreditCommand> cm) {
    ReserveCreditCommand cmd = cm.getCommand();
    long customerId = cmd.getCustomerId();
    Customer customer = (Customer)customerRepository.findOne(customerId);
    // TODO null check
    try {
      customer.reserveCredit(cmd.getOrderId(), cmd.getOrderTotal());
      return CommandHandlerReplyBuilder.withSuccess(new CustomerCreditReserved());
    } catch (CustomerCreditLimitExceededException e) {
      return CommandHandlerReplyBuilder.withFailure(new CustomerCreditReservationFailed());
    }
  }

  // withLock(Customer.class, customerId).
  // TODO @Validate to trigger validation and error reply


}
