package com.networknt.saga.repository;


import com.networknt.eventuate.common.Command;

/**
 * Order command model.
 */
public class OrderCommand implements Command {

  private long orderId;

  public OrderCommand() {
  }

  public void setOrderId(long orderId) {
    this.orderId = orderId;
  }

  public OrderCommand(long orderId) {

    this.orderId = orderId;
  }

  public long getOrderId() {
    return orderId;
  }
}
