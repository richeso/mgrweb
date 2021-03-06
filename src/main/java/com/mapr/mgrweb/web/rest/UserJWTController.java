package com.mapr.mgrweb.web.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mapr.mgrweb.security.EncryptUtils;
import com.mapr.mgrweb.security.MgrWebToken;
import com.mapr.mgrweb.security.SecurityUtils;
import com.mapr.mgrweb.security.jwt.JWTFilter;
import com.mapr.mgrweb.security.jwt.TokenProvider;
import com.mapr.mgrweb.web.rest.vm.LoginVM;
import javax.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller to authenticate users.
 */
@RestController
@RequestMapping("/api")
public class UserJWTController {

    private final TokenProvider tokenProvider;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public UserJWTController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginVM loginVM) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            loginVM.getUsername(),
            loginVM.getPassword()
        );

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        MgrWebToken mgrToken = (MgrWebToken) authentication;
        String userpw = mgrToken.getUserpw();
        String credentials = mgrToken.getCopyCredentials();
        //System.out.println("userpw=" + userpw);
        //System.out.println("credentials=" + credentials);
        EncryptUtils enc = new EncryptUtils();
        //String secret = EncryptUtils.generateSecret();
        String encrypted = enc.encrypt(credentials, userpw);
        String decrypted = enc.decrypt(encrypted, userpw);

        //System.out.println("Encrypted Credentials: " + encrypted);
        //System.out.println("Decrypted Credentials: " + decrypted);
        mgrToken.setCopyCredentials(encrypted);
        SecurityUtils.setAuthentication(mgrToken);

        String jwt = tokenProvider.createToken(mgrToken, loginVM.isRememberMe());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        //System.out.println("Decrypted Credentials: " + mgrToken.getDecryptedCredentials());
        return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    static class JWTToken {

        private String idToken;

        JWTToken(String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }
}
