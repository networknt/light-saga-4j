package com.networknt.saga.customer.service;

import com.networknt.saga.customer.domain.Customer;
import com.networknt.saga.customer.domain.CustomerCreditLimitExceededException;
import com.networknt.saga.customer.domain.CustomerRepository;
import com.networknt.saga.order.saga.participants.ReserveCreditCommand;
import com.networknt.saga.participant.SagaCommandHandlersBuilder;
import com.networknt.tram.command.consumer.CommandHandlerReplyBuilder;
import com.networknt.tram.command.consumer.CommandHandlers;
import com.networknt.tram.command.consumer.CommandMessage;
import com.networknt.tram.message.common.Message;


public class CustomerCommandHandler {


  private CustomerRepository customerRepository;

  public CustomerCommandHandler(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

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
