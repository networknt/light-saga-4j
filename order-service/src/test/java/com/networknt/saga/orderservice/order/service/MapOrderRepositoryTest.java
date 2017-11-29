package com.networknt.saga.orderservice.order.service;

import com.networknt.saga.orderservice.common.Money;
import com.networknt.saga.orderservice.order.domain.Order;
import com.networknt.saga.orderservice.order.domain.OrderDetails;
import com.networknt.saga.orderservice.order.domain.OrderRepository;
import com.networknt.service.SingletonServiceFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class MapOrderRepositoryTest {

    private OrderRepository orderRepository = (OrderRepository) SingletonServiceFactory.getBean(OrderRepository.class);

    @Test
    public void testMapOrderRepository() {
        for (int i = 100; i < 130; i++) {
            OrderDetails orderDetails =  new OrderDetails(i +100L, new Money("123.40"));
            Order order = new Order (orderDetails);
            orderRepository.save(order);
        }
        System.out.println(orderRepository.count());
        //assertEquals(30, orderRepository.count());
        Assert.assertTrue(orderRepository.count() >= 30);
        for (int i = 100; i < 130; i++) {
            Order order  = orderRepository.findOne(Long.valueOf(i));
            assertEquals(order.getId(), Long.valueOf(i));
        }

    }

}
