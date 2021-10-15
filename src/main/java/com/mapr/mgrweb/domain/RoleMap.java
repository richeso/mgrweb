package com.mapr.mgrweb.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleMap implements Serializable {

    private String id;
    private String role;
    private List<Customer> customers;

    public RoleMap() {}

    @JsonProperty("_id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("role")
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @JsonProperty("customers")
    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public void addCustomer(Customer customer) {
        if (customers == null) customers = new ArrayList<Customer>();
        customers.add(customer);
    }

    @Override
    public String toString() {
        StringBuilder rval = new StringBuilder();

        rval.append("{\"_id\":\"").append(id).append("\",\"role\":\"").append(role).append("\",\"customers\":[");
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
