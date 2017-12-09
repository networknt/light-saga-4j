package com.networknt.saga.orderservice;


import com.networknt.saga.orchestration.SagaManager;
import com.networknt.saga.orderservice.common.Money;
import com.networknt.saga.orderservice.customer.domain.Customer;
import com.networknt.saga.orderservice.customer.service.CustomerCommandHandler;
import com.networknt.saga.orderservice.customer.service.CustomerService;
import com.networknt.saga.orderservice.order.domain.Order;
import com.networknt.saga.orderservice.order.domain.OrderDetails;
import com.networknt.saga.orderservice.order.domain.OrderRepository;
import com.networknt.saga.orderservice.order.domain.OrderState;
import com.networknt.saga.orderservice.order.saga.createorder.CreateOrderSaga;
import com.networknt.saga.orderservice.order.saga.createorder.CreateOrderSagaData;
import com.networknt.saga.orderservice.order.service.OrderCommandHandler;
import com.networknt.saga.orderservice.order.service.OrderService;
import com.networknt.saga.participant.SagaLockManager;
import com.networknt.service.SingletonServiceFactory;
import com.networknt.tram.command.consumer.CommandDispatcher;
import org.h2.tools.RunScript;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


import javax.sql.DataSource;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public  class OrdersAndCustomersIntegrationTest {

  public static DataSource ds;

  static {
    ds = (DataSource) SingletonServiceFactory.getBean(DataSource.class);
    try (Connection connection = ds.getConnection()) {
      // Runscript doesn't work need to execute batch here.
      String schemaResourceName = "/saga_repository_ddl.sql";
      InputStream in = OrdersAndCustomersIntegrationTest.class.getResourceAsStream(schemaResourceName);

      if (in == null) {
        throw new RuntimeException("Failed to load resource: " + schemaResourceName);
      }
      InputStreamReader reader = new InputStreamReader(in);
      RunScript.execute(connection, reader);

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
   TramCommandsAndEventsIntegrationData tramCommandsAndEventsIntegrationData =  new TramCommandsAndEventsIntegrationData();


  private CustomerService customerService = (CustomerService)SingletonServiceFactory.getBean(CustomerService.class);

  private OrderRepository orderRepository = (OrderRepository)SingletonServiceFactory.getBean(OrderRepository.class);
  private CreateOrderSaga createOrderSaga = (CreateOrderSaga)SingletonServiceFactory.getBean(CreateOrderSaga.class);
  private SagaManager<CreateOrderSagaData> sagaManager = ComponentFactory.getSagaManager(createOrderSaga, tramCommandsAndEventsIntegrationData);
  private SagaLockManager sagaLockManager = (SagaLockManager) SingletonServiceFactory.getBean(SagaLockManager.class);
  private OrderService orderService = new OrderService(orderRepository, sagaManager);
  private OrderCommandHandler orderCommandHandler = (OrderCommandHandler) SingletonServiceFactory.getBean(OrderCommandHandler.class);
  private CustomerCommandHandler customerCommandHandler = (CustomerCommandHandler) SingletonServiceFactory.getBean(CustomerCommandHandler.class);


  private CommandDispatcher orderCommandDispatcher = ComponentFactory.getOrderCommandDispatcher(orderCommandHandler, sagaLockManager,tramCommandsAndEventsIntegrationData);
  private CommandDispatcher customerCommandDispatcher = ComponentFactory.getConsumerCommandDispatcher(customerCommandHandler, sagaLockManager,tramCommandsAndEventsIntegrationData);;
//  private TransactionTemplate transactionTemplate;

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
