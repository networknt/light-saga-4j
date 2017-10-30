package com.networknt.saga.orderservice.order.domain;


import com.networknt.saga.orderservice.common.Money;

public class OrderDetails {

  private Long customerId;


  private Money orderTotal;

  public OrderDetails() {
  }

  public OrderDetails(Long customerId, Money orderTotal) {
    this.customerId = customerId;
    this.orderTotal = orderTotal;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public Money getOrderTotal() {
    return orderTotal;
  }
}
