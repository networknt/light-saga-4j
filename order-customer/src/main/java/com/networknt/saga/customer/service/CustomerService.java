package com.networknt.saga.customer.service;


import com.networknt.saga.common.Money;
import com.networknt.saga.customer.domain.Customer;
import com.networknt.saga.customer.domain.CustomerRepository;

public class CustomerService {

  private CustomerRepository customerRepository;

  public CustomerService(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  public Customer createCustomer(String name, Money creditLimit) {
    Customer customer  = new Customer(name, creditLimit);
    return customerRepository.save(customer);
  }
}
