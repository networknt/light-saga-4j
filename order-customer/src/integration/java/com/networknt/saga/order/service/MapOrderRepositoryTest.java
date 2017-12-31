package com.networknt.saga.order.service;

import com.networknt.saga.common.Money;
import com.networknt.saga.order.domain.Order;
import com.networknt.saga.order.domain.OrderDetails;
import com.networknt.saga.order.domain.OrderRepository;
import com.networknt.service.SingletonServiceFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;

public class MapOrderRepositoryTest {
    private static AtomicLong atomicCustomerId = new AtomicLong();
    private OrderRepository orderRepository = (OrderRepository) SingletonServiceFactory.getBean(OrderRepository.class);

    @Test
    public void testMapOrderRepository() {
        for (int i = 0; i < 30; i++) {
            OrderDetails orderDetails =  new OrderDetails(atomicCustomerId.getAndIncrement(), new Money("123.40"));
            Order order = new Order (orderDetails);
            orderRepository.save(order);
        }
        Map<Long, Order> map = orderRepository.findAll();
        // map.forEach((k, v) -> System.out.println("key = " + k + " id = " + v.getId()));
        // if you run this test individually, then the count would be 30, however, it you run
        // all tests together with mvn clean install, another test class created 2 more orders
        Assert.assertTrue(30 == orderRepository.count() || 32 == orderRepository.count());
        for (int i = 1; i < 31; i++) {
            Order order  = orderRepository.findOne(Long.valueOf(i));
            assertEquals(order.getId(), Long.valueOf(i));
        }
    }
}
