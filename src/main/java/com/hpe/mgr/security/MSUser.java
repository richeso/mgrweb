package com.hpe.mgr.security;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class MSUser extends User {

    private List<String> customers;

    public MSUser(String username, String password, Collection<? extends GrantedAuthority> authorities, List<String> customers) {
        super(username, password, authorities);
        this.customers = customers;
    }

    public List<String> getCustomers() {
        return customers;
    }
}
