package com.networknt.saga.orderservice.order.domain;



import com.networknt.saga.core.events.ResultWithEvents;

import java.util.Collections;


public class Order {


  private Long id;

  private OrderState state;


  private OrderDetails orderDetails;

  public Order() {
  }

  public Order(OrderDetails orderDetails) {
    this.orderDetails = orderDetails;
    this.state = OrderState.PENDING;
  }

  public static ResultWithEvents<Order> createOrder(OrderDetails orderDetails) {
    return new ResultWithEvents<Order>(new Order(orderDetails), Collections.emptyList());
  }

  public Long getId() {
    return id;
  }

  public void noteCreditReserved() {
    this.state = OrderState.APPROVED;
  }

  public void noteCreditReservationFailed() {
    this.state = OrderState.REJECTED;
  }

  public OrderState getState() {
    return state;
  }
}
