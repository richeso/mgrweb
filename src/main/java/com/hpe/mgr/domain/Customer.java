package com.hpe.mgr.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer implements Serializable {

    private String customerId;

    public Customer() {}

    @JsonProperty("_id")
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        StringBuilder rval = new StringBuilder();

        rval.append("{\"_id\":\"").append(customerId);
        rval.append("]}");
        return rval.toString();
    }
}
