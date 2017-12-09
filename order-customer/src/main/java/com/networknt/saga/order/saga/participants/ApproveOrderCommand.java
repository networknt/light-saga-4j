package com.networknt.saga.order.saga.participants;


import com.networknt.tram.command.common.Command;

public class ApproveOrderCommand implements Command {
  private long orderId;

  private ApproveOrderCommand() {
  }


  public ApproveOrderCommand(long orderId) {

    this.orderId = orderId;
  }

  public long getOrderId() {
    return orderId;
  }

  public void setOrderId(long orderId) {
    this.orderId = orderId;
  }
}
