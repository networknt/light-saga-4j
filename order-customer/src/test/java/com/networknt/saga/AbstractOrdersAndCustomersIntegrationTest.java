package com.networknt.saga;

import com.networknt.saga.common.Money;
import com.networknt.saga.customer.domain.Customer;
import com.networknt.saga.customer.service.CustomerService;
import com.networknt.saga.order.domain.Order;
import com.networknt.saga.order.domain.OrderDetails;
import com.networknt.saga.order.domain.OrderRepository;
import com.networknt.saga.order.domain.OrderState;
import com.networknt.saga.order.service.OrderService;
import com.networknt.service.SingletonServiceFactory;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public abstract class AbstractOrdersAndCustomersIntegrationTest {

  private CustomerService customerService = SingletonServiceFactory.getBean(CustomerService.class);

  private OrderService orderService = SingletonServiceFactory.getBean(OrderService.class);

  private OrderRepository orderRepository = SingletonServiceFactory.getBean(OrderRepository.class);

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
      order = orderRepository.findOne(id);
      if (order.getState() == expectedState)
        break;
      TimeUnit.MILLISECONDS.sleep(400);
    }

    assertEquals(expectedState, order.getState());
  }
}
