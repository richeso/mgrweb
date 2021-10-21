package com.mapr.mgrweb.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapr.mgrweb.domain.Authority;
import com.mapr.mgrweb.domain.User;
import com.mapr.mgrweb.service.PamService;
import com.mapr.mgrweb.service.UserService;
import com.mapr.mgrweb.service.dto.AdminUserDTO;
import java.util.*;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private static final Logger log = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PamService pamService;

    @Autowired
    ObjectFactory<HttpSession> httpSessionFactory;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            log.debug("AUTHENTICATION Login: " + authentication.getName());
            //log.debug("AUTHENTICATION Password: " + authentication.getCredentials().toString());

            String username = authentication.getName();
            String password = authentication.getCredentials().toString();

            String authResults = pamService.authenticate(username, password);
            boolean isAuthenticated = authResults.indexOf("Error") < 0;

            if (isAuthenticated) {
                User user = userService.getUserWithAuthoritiesByLogin(username).orElse(user = null);
                if (user == null) {
                    // Auto Register User - later -- for now throw exception
                    // instantiate new user
                    AdminUserDTO userDTO = new AdminUserDTO();
                    userDTO.setActivated(true);
                    userDTO.setFirstName(username);
                    userDTO.setLastName(username);
                    userDTO.setCreatedBy(username);
                    userDTO.setLogin(username);
                    userDTO.setLangKey(Constants.DEFAULT_LANGUAGE);
                    userDTO.setEmail(username + "@mail.com");
                    userDTO.setCreatedDate(new Date(System.currentTimeMillis()).toInstant());
                    userDTO.setLastModifiedBy(username);
                    userDTO.setLastModifiedDate(userDTO.getCreatedDate());
                    userDTO.setAuthorities(new HashSet<>());
                    userDTO.getAuthorities().add(Constants.ROLE_USER);
                    if (username.equals(Constants.ADMIN_USERNAME)) userDTO.getAuthorities().add(Constants.ROLE_ADMIN);
                    try {
                        user = userService.createUser(userDTO);
                    } catch (Exception e) {
                        throw new BadCredentialsException("Unable to Create Your Registration Record : " + username);
                    }
                }
                HttpSession session = httpSessionFactory.getObject();
                session.setAttribute(Constants.USERPASS, password);
                session.setAttribute(Constants.USERNAME, username);
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
                throw new BadCredentialsException(authResults);
            }
        } catch (Exception e) {
            throw new AuthenticationServiceException("Failed to login", e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
