package com.networknt.saga.orderservice.customer.service;


import com.networknt.saga.orderservice.common.Money;
import com.networknt.saga.orderservice.customer.domain.Customer;
import com.networknt.saga.orderservice.customer.domain.CustomerRepository;

public class CustomerService {

  private CustomerRepository customerRepository;

  public Customer createCustomer(String name, Money creditLimit) {
    Customer customer  = new Customer(name, creditLimit);
    return (Customer)customerRepository.save(customer);
  }
}
