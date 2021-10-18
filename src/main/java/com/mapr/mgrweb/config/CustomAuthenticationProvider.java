package com.mapr.mgrweb.config;

import com.mapr.mgrweb.domain.Authority;
import com.mapr.mgrweb.domain.User;
import com.mapr.mgrweb.service.UserService;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            log.debug("AUTHENTICATION Login: " + authentication.getName());
            //log.debug("AUTHENTICATION Password: " + authentication.getCredentials().toString());

            String username = authentication.getName();
            String password = authentication.getCredentials().toString();
            boolean isAuthenticated = pamAuthentication(username, password);

            User user = userService.getUserWithAuthoritiesByLogin(username).orElse(user = null);

            if (isAuthenticated) {
                if (user == null) {
                    // Auto Register User - later -- for now throw exception
                    throw new BadCredentialsException("Invalid username or password");
                }

                List<GrantedAuthority> grantedAuths = new ArrayList<>();
                for (Authority authority : user.getAuthorities()) grantedAuths.add(new SimpleGrantedAuthority(authority.getName()));

                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                    user.getLogin(),
                    authentication.getCredentials(),
                    grantedAuths
                );
                token.setDetails(authentication.getDetails());

                //List<GrantedAuthority> grantedAuths = new ArrayList<>();
                //grantedAuths.add(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN));
                //return new UsernamePasswordAuthenticationToken(username, password, grantedAuths);
                return token;
            } else {
                throw new BadCredentialsException("Invalid username or password");
            }
        } catch (Exception e) {
            throw new AuthenticationServiceException("Failed to login", e);
        }
    }

    public boolean pamAuthentication(String user, String passwd) throws AuthenticationException {
        return true;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
