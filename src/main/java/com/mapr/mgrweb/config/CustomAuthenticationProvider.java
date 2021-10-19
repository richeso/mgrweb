package com.mapr.mgrweb.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapr.mgrweb.domain.Authority;
import com.mapr.mgrweb.domain.User;
import com.mapr.mgrweb.service.UserService;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    RestTemplate restTemplate;

    @Value("${api.host.pam}")
    private String pamHost;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            log.debug("AUTHENTICATION Login: " + authentication.getName());
            //log.debug("AUTHENTICATION Password: " + authentication.getCredentials().toString());

            String username = authentication.getName();
            String password = authentication.getCredentials().toString();
            String authResults = pamAuthentication(username, password);
            boolean isAuthenticated = !authResults.startsWith("Error");

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

    public String pamAuthentication(String user, String passwd) throws AuthenticationException {
        try {
            HttpHeaders headers = createAuthHeader(user, passwd);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity request = new HttpEntity(headers);
            UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(pamHost + "/api/paminfo")
                .queryParam("userid", user)
                .queryParam("password", passwd);
            // make a request
            ResponseEntity<Map> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, request, Map.class);
            ObjectMapper objectMapper = new ObjectMapper();
            String responseString = objectMapper.writeValueAsString(response.getBody());
            return responseString;
        } catch (Exception e) {
            log.debug("Error Encountered: " + e.getMessage());
            return ("Error encountered: " + e.getMessage());
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private HttpHeaders createAuthHeader(String username, String password) {
        // create auth credentials
        String authStr = username + ":" + password;
        String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());
        // create headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        return headers;
    }
}
