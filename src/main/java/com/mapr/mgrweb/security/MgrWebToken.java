package com.mapr.mgrweb.security;

import java.util.Collection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class MgrWebToken extends UsernamePasswordAuthenticationToken {

    protected String userpw;
    protected String copyCredentials;

    public MgrWebToken(Object principal, Object credentials) {
        super(principal, credentials);
        copyCredentials = (String) credentials;
    }

    public MgrWebToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
        copyCredentials = (String) credentials;
    }

    public String getUserpw() {
        return userpw;
    }

    public void setUserpw(String userpw) {
        this.userpw = userpw;
    }

    public void setCopyCredentials(String aCred) {
        this.copyCredentials = aCred;
    }

    public String getCopyCredentials() {
        return this.copyCredentials;
    }

    public String getDecryptedCredentials() {
        EncryptUtils enc = new EncryptUtils();
        return enc.decrypt(copyCredentials, userpw);
    }
}
