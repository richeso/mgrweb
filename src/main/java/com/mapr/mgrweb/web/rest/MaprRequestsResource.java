package com.mapr.mgrweb.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapr.mgrweb.config.Constants;
import com.mapr.mgrweb.domain.MaprRequests;
import com.mapr.mgrweb.domain.User;
import com.mapr.mgrweb.repository.MaprRequestsRepository;
import com.mapr.mgrweb.security.SecurityUtils;
import com.mapr.mgrweb.service.MapRService;
import com.mapr.mgrweb.service.UserService;
import com.mapr.mgrweb.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link MaprRequests}.
 */
@RestController
@RequestMapping("/api")
public class MaprRequestsResource {

    private final Logger log = LoggerFactory.getLogger(MaprRequestsResource.class);

    private static final String ENTITY_NAME = "maprRequests";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    @Autowired
    private MapRService mapRService;

    @Autowired
    ObjectFactory<HttpSession> httpSessionFactory;

    private final MaprRequestsRepository maprRequestsRepository;

    public MaprRequestsResource(MaprRequestsRepository maprRequestsRepository) {
        this.maprRequestsRepository = maprRequestsRepository;
    }

    /**
     * {@code POST  /mapr-requests} : Create a new maprRequests.
     *
     * @param maprRequests the maprRequests to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new maprRequests, or with status {@code 400 (Bad Request)} if the maprRequests has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/mapr-requests")
    public ResponseEntity<MaprRequests> createMaprRequests(@Valid @RequestBody MaprRequests maprRequests) throws Exception {
        log.debug("REST request to save MaprRequests : {}", maprRequests);
        //HttpSession session = httpSessionFactory.getObject();
        String userid = SecurityUtils.getCurrentUserLogin().get();
        // prefix volume name entered with userid + "."
        String volumeName = userid + "." + maprRequests.getName();

        if (maprRequests.getId() != null) {
            throw new BadRequestAlertException("A new maprRequests cannot already have an ID", ENTITY_NAME, "idexists");
        } else {
            // check whether volume name already exists
            if (volumeExists(volumeName)) {
                throw new BadRequestAlertException("Volume: " + volumeName + " Already exists, Please Use another Volume Name.", "", "");
            }
            maprRequests.initNewId();
            maprRequests.setAction("Create Volume");

            String path = maprRequests.getPath();
            String volpath = "";
            if (path.startsWith("/")) volpath = "/user/" + userid + path; else volpath = "/user/" + userid + "/" + path;
            maprRequests.setPath(volpath);
            maprRequests.setCreatedBy(userid);
            maprRequests.setRequestUser(userid);
            maprRequests.setRequestDate(new Date(System.currentTimeMillis()).toInstant());
            maprRequests.setCreatedDate(new Date(System.currentTimeMillis()).toInstant());
            maprRequests.setLastModifiedBy(userid);
            maprRequests.setStatus(Constants.CREATED_STATUS);
            maprRequests.setLastModifiedDate(new Date(System.currentTimeMillis()).toInstant());
            maprRequests.setType("Volume Create");
            maprRequests.setStatusDate(new Date(System.currentTimeMillis()).toInstant());
        }

        HttpSession session = httpSessionFactory.getObject();
        // String userid = (String) session.getAttribute(Constants.USERNAME);
        // String password = (String) session.getAttribute(Constants.USERPASS);
        String password = SecurityUtils.getMgrWebToken().getDecryptedCredentials();

        String c8volResult = mapRService.c8vol(userid, password, volumeName, maprRequests.getPath(), maprRequests.getExtraProperties());
        String message = getApiMessage(c8volResult);
        if (c8volResult.toUpperCase().indexOf("ERROR") >= 0) {
            throw new BadRequestAlertException(message, "", "");
        }

        maprRequests.setName(volumeName);
        maprRequestsRepository.save(maprRequests);
        Optional<MaprRequests> foundResult = maprRequestsRepository.findById(maprRequests.get_id());
        MaprRequests result = foundResult.isPresent() ? foundResult.get() : new MaprRequests();
        message = " Volume Name: " + maprRequests.getName() + " in Path: " + maprRequests.getPath();
        return ResponseEntity
            .created(new URI("/api/mapr-requests/" + maprRequests.get_id()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, message))
            .body(result);
    }

    /**
     * {@code PUT  /mapr-requests/:id} : Updates an existing maprRequests.
     *
     * @param id the id of the maprRequests to save.
     * @param maprRequests the maprRequests to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated maprRequests,
     * or with status {@code 400 (Bad Request)} if the maprRequests is not valid,
     * or with status {@code 500 (Internal Server Error)} if the maprRequests couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/mapr-requests/{id}")
    public ResponseEntity<MaprRequests> updateMaprRequests(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody MaprRequests maprRequests
    ) throws URISyntaxException {
        log.debug("REST request to update MaprRequests : {}, {}", id, maprRequests);
        if (maprRequests.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        maprRequestsRepository.save(maprRequests);
        Optional<MaprRequests> foundResult = maprRequestsRepository.findById(maprRequests.get_id());
        MaprRequests result = foundResult.isPresent() ? foundResult.get() : new MaprRequests();
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, maprRequests.get_id().toString()))
            .body(result);
    }

    /**
     * {@code GET  /mapr-requests} : get all the maprRequests.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of maprRequests in body.
     */
    @GetMapping("/mapr-requests")
    public List<MaprRequests> getAllMaprRequests() {
        String currentUser = SecurityUtils.getCurrentUserLogin().get();
        log.debug("REST request to get all MaprRequests for user: " + currentUser);
        return maprRequestsRepository.findByRequested(currentUser);
    }

    /**
     * {@code GET  /mapr-requests/:id} : get the "id" maprRequests.
     *
     * @param id the id of the maprRequests to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the maprRequests, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/mapr-requests/{id}")
    public ResponseEntity<MaprRequests> getMaprRequests(@PathVariable String id) {
        log.debug("REST request to get MaprRequests : {}", id);
        Optional<MaprRequests> maprRequests = maprRequestsRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(maprRequests);
    }

    /**
     * {@code DELETE  /mapr-requests/:id} : delete the "id" maprRequests.
     *
     * @param id the id of the maprRequests to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/mapr-requests/{id}")
    public ResponseEntity<Void> deleteMaprRequests(@PathVariable String id) throws Exception {
        Optional<MaprRequests> maprRequests = maprRequestsRepository.findById(id);
        MaprRequests aRequest = maprRequests.get();

        if (aRequest != null) {
            //HttpSession session = httpSessionFactory.getObject();
            //String userid = (String) session.getAttribute(Constants.USERNAME);
            //String password = (String) session.getAttribute(Constants.USERPASS);
            if (!aRequest.getStatus().equals(Constants.CREATED_STATUS)) {
                log.debug("REST request to delete MaprRequests : {}", id);
                throw new BadRequestAlertException("Cannot Delete an already Deleted Request", ENTITY_NAME, "already_deleted");
            }
            String userid = SecurityUtils.getCurrentUserLogin().get();
            String password = SecurityUtils.getMgrWebToken().getDecryptedCredentials();

            String deleteResults = mapRService.deletevol(userid, password, aRequest.getName());
            String message = getApiMessage(deleteResults);
            if (deleteResults.toUpperCase().indexOf("ERROR") >= 0) {
                throw new BadRequestAlertException(message, "", "");
            }
            maprRequestsRepository.delete(id);
        }

        String message = "Successfully Deleted New Volume with Name: " + aRequest.getName() + " in Path: " + aRequest.getPath();
        return ResponseEntity.noContent().headers(HeaderUtil.createAlert(applicationName, message, "")).build();
    }

    /**
     * {@code GET  /mapr-requests} : get all the maprRequests.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of maprRequests in body.
     */
    @GetMapping("/mapr-listvolumes")
    public List<MaprRequests> getMaprVolumeList() throws Exception {
        String userid = SecurityUtils.getCurrentUserLogin().get();
        String password = SecurityUtils.getMgrWebToken().getDecryptedCredentials();

        List<MaprRequests> results = mapRService.volumeList(userid, password, userid);
        log.debug("REST request to get VolumeList for ae user: " + userid);
        return results;
    }

    private boolean volumeExists(String name) {
        boolean exists = false;
        List<MaprRequests> namedList = maprRequestsRepository.findActiveByName(name);
        if (namedList != null && !namedList.isEmpty()) exists = true;
        return exists;
    }

    private String getApiMessage(String result) throws Exception {
        // This extracts messages/errors returning from MapR api
        // payload
        String searchFor = "messages";
        if (result.toUpperCase().indexOf("ERROR") >= 0) {
            return getApiErrors(result);
        }
        String errorDesc = result;
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> apiMessages = objectMapper.readValue(result, Map.class);
        List<String> messages = (List<String>) apiMessages.get(searchFor);
        if (messages != null && !messages.isEmpty()) {
            boolean first = true;
            for (String item : messages) {
                if (first) errorDesc = item; else {
                    first = false;
                    errorDesc += "\n" + item;
                }
            }
        }

        return errorDesc;
    }

    private String getApiErrors(String result) throws Exception {
        // This extracts messages/errors returning from MapR api
        // payload
        String searchFor = "errors";
        String errorDesc = result;
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> apiMessages = objectMapper.readValue(result, Map.class);
        List<Object> errors = (List<Object>) apiMessages.get(searchFor);
        for (Object item : errors) {
            Map<String, Object> errorMap = (Map<String, Object>) item;
            errorDesc = (String) errorMap.get("desc");
            break;
        }
        return errorDesc;
    }
}
