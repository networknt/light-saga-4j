package com.networknt.saga.orderservice;


import com.networknt.saga.orderservice.common.Money;
import com.networknt.saga.orderservice.customer.domain.Customer;
import com.networknt.saga.orderservice.customer.service.CustomerService;
import com.networknt.saga.orderservice.order.domain.Order;
import com.networknt.saga.orderservice.order.domain.OrderDetails;
import com.networknt.saga.orderservice.order.domain.OrderRepository;
import com.networknt.saga.orderservice.order.domain.OrderState;
import com.networknt.saga.orderservice.order.service.OrderService;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public  class OrdersAndCustomersIntegrationTest {


  private CustomerService customerService;


  private OrderService orderService;


  private OrderRepository orderRepository;


//  private TransactionTemplate transactionTemplate;

  @Test
  public void shouldApproveOrder() throws InterruptedException {
    Money creditLimit = new Money("15.00");
    Customer customer = customerService.createCustomer("Fred", creditLimit);
    Order order = orderService.createOrder(new OrderDetails(customer.getId(), new Money("12.34")));

    assertOrderState(order.getId(), OrderState.APPROVED);
  }
  @Test
  public void shouldRejectOrder() throws InterruptedException {
    Money creditLimit = new Money("15.00");
    Customer customer = customerService.createCustomer("Fred", creditLimit);
    Order order = orderService.createOrder(new OrderDetails(customer.getId(), new Money("123.40")));

    assertOrderState(order.getId(), OrderState.REJECTED);
  }

  private void assertOrderState(Long id, OrderState expectedState) throws InterruptedException {
    Order order = null;
    for (int i = 0; i < 30; i++) {
   //   order = transactionTemplate.execute(s -> orderRepository.findOne(id));
      if (order.getState() == expectedState)
        break;
      TimeUnit.MILLISECONDS.sleep(400);
    }

    assertEquals(expectedState, order.getState());
  }
}
