package com.mapr.mgrweb.security;

import java.util.Collection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class MgrWebToken extends UsernamePasswordAuthenticationToken {

    protected String userpw = "";

    public MgrWebToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public MgrWebToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

    @Override
    public void eraseCredentials() {
        // don't erase
        System.out.println("Asking to erase this: " + getCredentials());
    }

    public String getUserpw() {
        return userpw;
    }

    public void setUserpw(String userpw) {
        this.userpw = userpw;
    }
}
