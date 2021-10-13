package com.hpe.mgr.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerMap implements Serializable {

    private String id;
    private List<Customer> customers = new ArrayList<Customer>();

    public CustomerMap() {}

    @JsonProperty("_id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("customers")
    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public void addCustomer(Customer customer) {
        //if (customers == null) customers = new ArrayList<Customer>();
        customers.add(customer);
    }

    @Override
    public String toString() {
        StringBuilder rval = new StringBuilder();

        rval.append("{\"_id\":\"").append(id).append("\",\"customers\":[");
        if (customers != null) {
            for (Customer c : customers) {
                rval.append(c.toString()).append(",");
            }
            rval.deleteCharAt(rval.length() - 1);
        }
        rval.append("]}");
        return rval.toString();
    }
}
