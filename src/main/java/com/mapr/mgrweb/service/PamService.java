package com.mapr.mgrweb.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapr.mgrweb.config.TotpUtility;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class PamService {

    private final String URI_VOLUME_INFO = "/volume/info";
    private final String URI_VOLUME_CREATE = "/volume/create";
    private final String URI_VOLUME_REMOVE = "/volume/remove";
    private static final Logger log = LoggerFactory.getLogger(PamService.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    TotpUtility totp;

    @Value("${api.host.pam}")
    private String pamHost;

    @Value("${api.host.mapr}")
    private String mapRHost;

    public String authenticate(String userid, String password) {
        try {
            HttpHeaders headers = createAuthHeader(userid, password);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity request = new HttpEntity(headers);
            UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(pamHost + "/api/paminfo")
                .queryParam("userid", userid)
                .queryParam("password", password);
            // make a request
            ResponseEntity<Map> response = restTemplate.exchange(builder.build().toUri(), HttpMethod.GET, request, Map.class);
            String responseBody = response.getBody().toString();
            String responseString = responseBody;
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                responseString = objectMapper.writeValueAsString(response.getBody());
            } catch (Exception e) {}
            return responseString;
        } catch (Exception e) {
            String errormsg = "Error Encountered: " + e.getMessage();
            log.debug(errormsg);
            return errormsg;
        }
    }

    private HttpHeaders createAuthHeader(String username, String password) throws Exception {
        // create auth credentials
        String authStr = username + ":" + password;
        String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());
        // create headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        String authtoken = totp.generateCode();
        headers.add("authtoken", authtoken);
        return headers;
    }
}
