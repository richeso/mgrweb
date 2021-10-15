package com.mapr.mgrweb.repository;

import com.mapr.mgrweb.domain.Customer;
import java.util.ArrayList;
import java.util.List;
import org.ojai.store.Connection;
import org.ojai.store.DocumentStore;
import org.ojai.store.DriverManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerRepository {

    private Connection connection = DriverManager.getConnection("ojai:mapr:");

    @Value("${mapr.tables.customer}")
    private String customerMapTable;

    public List<Customer> getAll() {
        List<Customer> list = new ArrayList<>();
        DocumentStore store = connection.getStore(customerMapTable);
        store.find().iterator().forEachRemaining(x -> list.add(x.toJavaBean(Customer.class)));
        store.close();
        return list;
    }

    public Customer findById(String id) {
        DocumentStore store = connection.getStore(customerMapTable);
        Customer customer = store.findById(id).toJavaBean(Customer.class);
        store.close();
        return customer;
    }
}
