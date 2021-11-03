package com.mapr.mgrweb.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class DownloadService {

    private static final Logger log = LoggerFactory.getLogger(DownloadService.class);

    @Autowired
    RestTemplate restTemplate;

    @Value("${api.host.mapr}")
    private String mapRHost;

    public ResponseEntity<byte[]> getzip(String userid, String password) {
        try {
            HttpHeaders headers = createAuthHeader(userid, password);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
            HttpEntity request = new HttpEntity(headers);
            UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(mapRHost + "/api/getzip")
                .queryParam("userid", userid)
                .queryParam("password", password);
            // make a request
            ResponseEntity<byte[]> response = restTemplate.exchange(builder.build().toUri(), HttpMethod.GET, request, byte[].class);
            return response;
        } catch (Exception e) {
            String errormsg = "Error Encountered: " + e.getMessage();
            log.debug(errormsg);
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<byte[]> getfile(String userid, String password) {
        try {
            HttpHeaders headers = createAuthHeader(userid, password);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
            HttpEntity request = new HttpEntity(headers);
            UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(mapRHost + "/api/getfile")
                .queryParam("userid", userid)
                .queryParam("password", password);
            // make a request
            ResponseEntity<byte[]> response = restTemplate.exchange(builder.build().toUri(), HttpMethod.GET, request, byte[].class);

            return response;
        } catch (Exception e) {
            String errormsg = "Error Encountered: " + e.getMessage();
            log.debug(errormsg);
            throw new RuntimeException(e);
        }
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
