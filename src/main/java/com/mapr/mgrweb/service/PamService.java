package com.mapr.mgrweb.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
public class PamService {

    private final String URI_VOLUME_INFO = "/volume/info";
    private final String URI_VOLUME_CREATE = "/volume/create";
    private final String URI_VOLUME_REMOVE = "/volume/remove";
    private static final Logger log = LoggerFactory.getLogger(PamService.class);

    @Autowired
    RestTemplate restTemplate;

    @Value("${api.host.pam}")
    private String pamHost;

    @Value("${api.host.mapr}")
    private String mapRHost;

    @GetMapping("/pamapi/pam")
    public ResponseEntity<String> pamauthenticate(@RequestParam Map<String, String> payload) {
        System.out.println(payload);
        String userid = (String) payload.get("userid");
        String password = (String) payload.get("password");
        try {
            HttpHeaders headers = createAuthHeader(userid, password);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity request = new HttpEntity(headers);
            UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(pamHost + "/api/paminfo")
                .queryParam("userid", userid)
                .queryParam("password", password);
            // make a request
            ResponseEntity<Map> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, request, Map.class);
            ObjectMapper objectMapper = new ObjectMapper();
            String responseString = objectMapper.writeValueAsString(response.getBody());
            return ResponseEntity.ok(responseString);
        } catch (Exception e) {
            log.debug("Error Encountered: " + e.getMessage());
            return ResponseEntity.ok("error encountered: " + e.getMessage());
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
