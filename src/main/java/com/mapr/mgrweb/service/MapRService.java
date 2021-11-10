package com.mapr.mgrweb.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapr.mgrweb.config.Constants;
import com.mapr.mgrweb.domain.MaprRequests;
import java.time.Instant;
import java.util.*;
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
    private final String _creator = "creator";
    private final String _volumename = "volumename";
    private final String _mountdir = "mountdir";
    private final String _quota = "quota";
    private final String _dareEnabled = "dareEnabled";
    private final String _advisoryquota = "advisoryquota";
    private final String _wireSecurity = "wireSecurity";
    private final String _creationTime = "creationTime";

    private final String VOLUME_LIST_COLUMNS =
        _creator +
        "," +
        _volumename +
        "," +
        _mountdir +
        "," +
        _quota +
        "," +
        _dareEnabled +
        "," +
        _advisoryquota +
        "," +
        _wireSecurity +
        "," +
        _creationTime;

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

    public Optional<MaprRequests> volinfo(String username, String password, String volumename) throws Exception {
        try {
            HttpHeaders headers = createAuthHeader(username, password);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity request = new HttpEntity(headers);
            UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(maprHost + URI_VOLUME_INFO)
                .queryParam("userid", username)
                .queryParam("password", password)
                .queryParam("volume", volumename);
            // make a request
            ResponseEntity<Map> response = restTemplate.exchange(builder.build().toUri(), HttpMethod.POST, request, Map.class);
            // ObjectMapper objectMapper = new ObjectMapper();
            // String responseString = objectMapper.writeValueAsString(response.getBody());
            // return responseString;
            List<MaprRequests> results = getMaprRequests(response);
            Optional<MaprRequests> aRequest = Optional.of(results.get(0));
            return aRequest;
        } catch (Exception e) {
            //e.printStackTrace();
            String errormsg = "Error Encountered: " + e.getMessage();
            log.debug(errormsg);
            throw e;
        }
    }

    public List<MaprRequests> volumeList(String userid, String password, String aename) throws Exception {
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
            // Make the request to list all volumes belonging to ae
            System.out.println("uri=" + builder.build().encode().toUriString());
            ResponseEntity<Map> response = restTemplate.exchange(
                builder.build().encode().toUriString(),
                HttpMethod.POST,
                request,
                Map.class
            );
            List<MaprRequests> results = getMaprRequests(response);
            return results;
        } catch (Exception e) {
            //e.printStackTrace();
            String errormsg = "Error Encountered: " + e.getMessage();
            log.debug(errormsg);
            throw e;
        }
    }

    private List<MaprRequests> getMaprRequests(ResponseEntity<Map> response) {
        // ObjectMapper objectMapper = new ObjectMapper();
        // String responseString = objectMapper.writeValueAsString(response.getBody());
        Map<String, Object> listmap = null;
        ArrayList<Map<String, Object>> volumemap = null;
        try {
            listmap = (Map<String, Object>) response.getBody();
            volumemap = (ArrayList<Map<String, Object>>) listmap.get("data");
        } catch (Exception e) {
            // swallow exception...assuming nothing was returned from the rest call
        }

        List<MaprRequests> results = new ArrayList<MaprRequests>();
        Iterator i = volumemap.iterator();
        int num = 0;
        while (i.hasNext()) {
            Map<String, Object> aVolume = (Map<String, Object>) i.next();
            MaprRequests aRequest = new MaprRequests();
            //VOLUME_LIST_COLUMNS = "volumename,mountdir,quota,advisoryqota,dareEnabled,wireSecurity";
            ++num;
            populateMaprRequest(Constants.MAPR_VOLUME_ID + num, aVolume, aRequest);
            results.add(aRequest);
        }
        return results;
    }

    private void populateMaprRequest(String volumeid, Map<String, Object> aVolume, MaprRequests aRequest) {
        Long ctime = (Long) aVolume.get(_creationTime);
        Instant cins = new Date(ctime.longValue()).toInstant();
        String creator = (String) aVolume.get(_creator);
        String volumeName = (String) aVolume.get(_volumename);
        aRequest.setCreatedDate(cins);
        aRequest.setStatusDate(cins);
        aRequest.setRequestUser(creator);
        aRequest.setName(volumeName);
        aRequest.setId(volumeid + "-" + volumeName);
        aRequest.setPath((String) aVolume.get(_mountdir));
        aRequest.setCreatedBy(creator);
        aRequest.getExtraProperties().put(_advisoryquota, aVolume.get(_advisoryquota) + "");
        aRequest.getExtraProperties().put(_quota, aVolume.get(_quota) + "");
        aRequest.getExtraProperties().put(_dareEnabled, aVolume.get(_dareEnabled) + "");
        aRequest.getExtraProperties().put(_wireSecurity, aVolume.get(_wireSecurity) + "");
    }

    private Instant stringToInstant(String aString) {
        long aTime = new Long(aString).longValue();
        Instant anInstant = new Date(aTime).toInstant();
        return anInstant;
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
