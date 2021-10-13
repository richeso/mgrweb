package com.hpe.mgr.service;

import com.hpe.mgr.domain.Customer;
import com.hpe.mgr.repository.CustomerRepository;
import com.hpe.mgr.service.dto.CustomerDto;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerDto> getCustomers() {
        List<Customer> customers = customerRepository.getAll();
        List<CustomerDto> customerDtos = new ArrayList<>();

        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            CustomerDto dto = new CustomerDto();
            dto.setName(customer.getCustomerId());
            customerDtos.add(dto);
        }

        return customerDtos;
    }

    public List<String> getCustomersNames() {
        return customerRepository.getAll().stream().map(x -> x.getCustomerId()).collect(Collectors.toList());
    }
}
