package com.mapr.mgrweb.security.jwt;

import com.mapr.mgrweb.security.EncryptUtils;
import com.mapr.mgrweb.security.MgrWebToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import tech.jhipster.config.JHipsterProperties;

@Component
public class TokenProvider {

    private final Logger log = LoggerFactory.getLogger(TokenProvider.class);

    private static final String AUTHORITIES_KEY = "auth";
    private static final String COPYCC_KEY = "copycc";
    private static final String USERPW_KEY = "userpw";

    private final Key key;

    private final JwtParser jwtParser;

    private final long tokenValidityInMilliseconds;

    private final long tokenValidityInMillisecondsForRememberMe;

    public TokenProvider(JHipsterProperties jHipsterProperties) {
        byte[] keyBytes;
        String secret = jHipsterProperties.getSecurity().getAuthentication().getJwt().getBase64Secret();

        if (!ObjectUtils.isEmpty(secret)) {
            log.debug("Using a Base64-encoded JWT secret key");
            keyBytes = Decoders.BASE64.decode(secret);
        } else {
            log.warn(
                "Warning: the JWT key used is not Base64-encoded. " +
                "We recommend using the `jhipster.security.authentication.jwt.base64-secret` key for optimum security."
            );
            secret = jHipsterProperties.getSecurity().getAuthentication().getJwt().getSecret();
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        key = Keys.hmacShaKeyFor(keyBytes);
        jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
        this.tokenValidityInMilliseconds = 1000 * jHipsterProperties.getSecurity().getAuthentication().getJwt().getTokenValidityInSeconds();
        this.tokenValidityInMillisecondsForRememberMe =
            1000 * jHipsterProperties.getSecurity().getAuthentication().getJwt().getTokenValidityInSecondsForRememberMe();
    }

    public String createToken(Authentication authentication, boolean rememberMe) {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        String copycc = "";
        String userpw = "";
        if (authentication instanceof MgrWebToken) {
            MgrWebToken mgrToken = (MgrWebToken) authentication;
            copycc = mgrToken.getCopyCredentials();
            userpw = mgrToken.getUserpw();
        }
        long now = (new Date()).getTime();
        Date validity;
        if (rememberMe) {
            validity = new Date(now + this.tokenValidityInMillisecondsForRememberMe);
        } else {
            validity = new Date(now + this.tokenValidityInMilliseconds);
        }

        String jwtToken = Jwts
            .builder()
            .setSubject(authentication.getName())
            .claim(AUTHORITIES_KEY, authorities)
            .claim(COPYCC_KEY, copycc)
            .claim(USERPW_KEY, userpw)
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(validity)
            .compact();
        String encryptedToken = this.encryptToken(jwtToken);
        return encryptedToken;
    }

    public Authentication getAuthentication(String inputToken) {
        String token = decryptToken(inputToken);
        Claims claims = jwtParser.parseClaimsJws(token).getBody();

        Collection<? extends GrantedAuthority> authorities = Arrays
            .stream(claims.get(AUTHORITIES_KEY).toString().split(","))
            .filter(auth -> !auth.trim().isEmpty())
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

        Object cc = claims.get(COPYCC_KEY);
        Object pw = claims.get(USERPW_KEY);
        String copycc = "";
        String userpw = "";
        if (cc != null) copycc = cc.toString();
        if (pw != null) userpw = pw.toString();

        User principal = new User(claims.getSubject(), "", authorities);
        MgrWebToken mgrWebToken = new MgrWebToken(principal, token, authorities);
        mgrWebToken.setUserpw(userpw);
        mgrWebToken.setCopyCredentials(copycc);
        return mgrWebToken;
    }

    public boolean validateToken(String inputToken) {
        try {
            String authToken = decryptToken(inputToken);
            jwtParser.parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace.", e);
        }
        return false;
    }

    public String encryptToken(String authToken) {
        EncryptUtils enc = new EncryptUtils();
        String encrypted = enc.encrypt(authToken, new String(key.getEncoded()));
        return encrypted;
    }

    public String decryptToken(String authToken) {
        EncryptUtils enc = new EncryptUtils();
        String decrypted = enc.decrypt(authToken, new String(key.getEncoded()));
        return decrypted;
    }
}
