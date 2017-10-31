package com.networknt.saga.orderservice.order.service;


import com.networknt.saga.core.events.subscriber.ResultWithEvents;
import com.networknt.saga.orchestration.SagaManager;
import com.networknt.saga.orderservice.order.domain.Order;
import com.networknt.saga.orderservice.order.domain.OrderDetails;
import com.networknt.saga.orderservice.order.domain.OrderRepository;
import com.networknt.saga.orderservice.order.saga.createorder.CreateOrderSagaData;

public class OrderService {


  private SagaManager<CreateOrderSagaData> createOrderSagaManager;


  private OrderRepository orderRepository;


  public Order createOrder(OrderDetails orderDetails) {
    ResultWithEvents<Order> oe = Order.createOrder(orderDetails);
    Order order = oe.result;
    orderRepository.save(order);
    CreateOrderSagaData data = new CreateOrderSagaData(order.getId(), orderDetails);
    createOrderSagaManager.create(data, Order.class, order.getId());
    return order;
  }

}
