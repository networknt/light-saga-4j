package com.networknt.saga;


import com.networknt.saga.orchestration.SagaManager;
import com.networknt.saga.common.Money;
import com.networknt.saga.customer.domain.Customer;
import com.networknt.saga.customer.service.CustomerCommandHandler;
import com.networknt.saga.customer.service.CustomerService;
import com.networknt.saga.order.domain.Order;
import com.networknt.saga.order.domain.OrderDetails;
import com.networknt.saga.order.domain.OrderRepository;
import com.networknt.saga.order.domain.OrderState;
import com.networknt.saga.order.saga.createorder.CreateOrderSaga;
import com.networknt.saga.order.saga.createorder.CreateOrderSagaData;
import com.networknt.saga.order.service.OrderCommandHandler;
import com.networknt.saga.order.service.OrderService;
import com.networknt.saga.participant.SagaLockManager;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.tram.command.consumer.CommandDispatcher;
import org.h2.tools.RunScript;
import org.junit.BeforeClass;
import org.junit.Test;


import javax.sql.DataSource;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public  class OrdersAndCustomersIntegrationIT {

  public static DataSource ds;

  static {
    ds = (DataSource) SingletonServiceFactory.getBean(DataSource.class);
    try (Connection connection = ds.getConnection()) {
      // Runscript doesn't work need to execute batch here.
      String schemaResourceName = "/saga_repository_ddl.sql";
      InputStream in = OrdersAndCustomersIntegrationIT.class.getResourceAsStream(schemaResourceName);

      if (in == null) {
        throw new RuntimeException("Failed to load resource: " + schemaResourceName);
      }
      InputStreamReader reader = new InputStreamReader(in);
      RunScript.execute(connection, reader);

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private CustomerService customerService = SingletonServiceFactory.getBean(CustomerService.class);
  private OrderRepository orderRepository = SingletonServiceFactory.getBean(OrderRepository.class);
  private OrderService orderService = SingletonServiceFactory.getBean(OrderService.class);
  private CommandDispatcher orderCommandDispatcher = ComponentFactory.getOrderCommandDispatcher();
  private CommandDispatcher customerCommandDispatcher = ComponentFactory.getCustomerCommandDispatcher();

  @BeforeClass
  public static void setUp() {

  }


  @Test
  public void shouldApproveOrder() throws InterruptedException {
    orderCommandDispatcher.initialize();
    customerCommandDispatcher.initialize();
    Money creditLimit = new Money("15.00");
    Customer customer = customerService.createCustomer("Fred", creditLimit);
    Order order = orderService.createOrder(new OrderDetails(customer.getId(), new Money("12.34")));
    assertOrderState(order.getId(), OrderState.APPROVED);
  }

  @Test
  public void shouldRejectOrder() throws InterruptedException {
    orderCommandDispatcher.initialize();
    customerCommandDispatcher.initialize();
    Money creditLimit = new Money("15.00");
    Customer customer = customerService.createCustomer("Fred", creditLimit);
    Order order = orderService.createOrder(new OrderDetails(customer.getId(), new Money("123.40")));
    assertOrderState(order.getId(), OrderState.REJECTED);
  }

  private void assertOrderState(Long id, OrderState expectedState) throws InterruptedException {
    Order order = null;
    for (int i = 0; i < 30; i++) {
   //   order = transactionTemplate.execute(s -> orderRepository.findOne(id));
      order = orderRepository.findOne(id);
      if (order.getState() == expectedState)
        break;
      TimeUnit.MILLISECONDS.sleep(200);
    }

    assertEquals(expectedState, order.getState());
  }
}
