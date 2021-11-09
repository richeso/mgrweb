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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class MapRService {

    private final String URI_VOLUME_INFO = "/api/volinfo";
    private final String URI_VOLUME_CREATE = "/api/c8vol";
    private final String URI_VOLUME_REMOVE = "/api/deletevol";
    private final String URI_ALL = "/api/mapr";
    private final String URI_VOLUME_LIST = "/volume/list";
    private final String VOLUME_LIST_COLUMNS = "volumename,mountdir,quota,advisoryqota,dareEnabled,wireSecurity";
    private String FILTER_COLUMN = "[aename==@userid]";
    private static final Logger log = LoggerFactory.getLogger(MapRService.class);

    @Autowired
    RestTemplate restTemplate;

    @Value("${api.host.pam}")
    private String pamHost;

    @Value("${api.host.mapr}")
    private String maprHost;

    public String c8vol(String username, String password, String volume, String volumePath, Map<String, String> extraProperties)
        throws Exception {
        try {
            HttpHeaders headers = createAuthHeader(username, password);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity request = new HttpEntity(headers);
            UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(maprHost + URI_VOLUME_CREATE)
                .queryParam("userid", username)
                .queryParam("password", password)
                .queryParam("volume", volume)
                .queryParam("path", volumePath);

            // add extra parameters
            // looping over keys
            for (String key : extraProperties.keySet()) {
                // search  for value
                String value = extraProperties.get(key);
                // default quota/advisoryquota to M - MegaBytes
                if (value != null && Integer.parseInt(value) > 0 && key.indexOf("quota") >= 0) builder.queryParam(
                    key,
                    value + "M"
                ); else builder.queryParam(key, value);
            }

            // make a request
            ResponseEntity<Map> response = restTemplate.exchange(builder.build().toUri(), HttpMethod.POST, request, Map.class);
            ObjectMapper objectMapper = new ObjectMapper();
            String responseString = objectMapper.writeValueAsString(response.getBody());
            log.debug("MaprResult = " + responseString);
            return responseString;
        } catch (Exception e) {
            //e.printStackTrace();
            String errormsg = "Error Encountered: " + e.getMessage();
            log.debug(errormsg);
            return errormsg;
        }
    }

    public String deletevol(String username, String password, String volume) throws Exception {
        try {
            HttpHeaders headers = createAuthHeader(username, password);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity request = new HttpEntity(headers);
            UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(maprHost + URI_VOLUME_REMOVE)
                .queryParam("userid", username)
                .queryParam("password", password)
                .queryParam("volume", volume);
            // make a request
            ResponseEntity<Map> response = restTemplate.exchange(builder.build().toUri(), HttpMethod.POST, request, Map.class);
            ObjectMapper objectMapper = new ObjectMapper();
            String responseString = objectMapper.writeValueAsString(response.getBody());
            log.debug("MaprResult = " + responseString);
            return responseString;
        } catch (Exception e) {
            //e.printStackTrace();
            String errormsg = "Error Encountered: " + e.getMessage();
            log.debug(errormsg);
            return errormsg;
        }
    }

    public String volinfo(String username, String password, String volume) throws Exception {
        try {
            HttpHeaders headers = createAuthHeader(username, password);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity request = new HttpEntity(headers);
            UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(maprHost + URI_VOLUME_INFO)
                .queryParam("userid", username)
                .queryParam("password", password)
                .queryParam("volume", volume);
            // make a request
            ResponseEntity<Map> response = restTemplate.exchange(builder.build().toUri(), HttpMethod.POST, request, Map.class);
            ObjectMapper objectMapper = new ObjectMapper();
            String responseString = objectMapper.writeValueAsString(response.getBody());
            return responseString;
        } catch (Exception e) {
            //e.printStackTrace();
            String errormsg = "Error Encountered: " + e.getMessage();
            log.debug(errormsg);
            return errormsg;
        }
    }

    public String volumeList(String userid, String password, String aename) throws Exception {
        try {
            HttpHeaders headers = createAuthHeader(userid, password);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity request = new HttpEntity(headers);
            String aefilter = FILTER_COLUMN.replaceAll("@userid", userid);
            UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(maprHost + URI_ALL)
                .queryParam("userid", userid)
                .queryParam("password", password)
                .queryParam("uri", URI_VOLUME_LIST)
                .queryParam("filter", aefilter)
                .queryParam("columns", VOLUME_LIST_COLUMNS);
            // make a request
            ResponseEntity<Map> response = restTemplate.exchange(builder.build().toUri(), HttpMethod.POST, request, Map.class);
            ObjectMapper objectMapper = new ObjectMapper();
            String responseString = objectMapper.writeValueAsString(response.getBody());
            return responseString;
        } catch (Exception e) {
            //e.printStackTrace();
            String errormsg = "Error Encountered: " + e.getMessage();
            log.debug(errormsg);
            return errormsg;
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
