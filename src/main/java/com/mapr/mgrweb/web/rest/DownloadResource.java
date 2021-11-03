package com.mapr.mgrweb.web.rest;

import com.mapr.mgrweb.config.Constants;
import com.mapr.mgrweb.domain.MaprRequests;
import com.mapr.mgrweb.repository.MaprRequestsRepository;
import com.mapr.mgrweb.security.SecurityUtils;
import com.mapr.mgrweb.service.DownloadService;
import com.mapr.mgrweb.service.MapRService;
import com.mapr.mgrweb.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link MaprRequests}.
 */
@RestController
public class DownloadResource {

    private final Logger log = LoggerFactory.getLogger(DownloadResource.class);

    @Autowired
    ObjectFactory<HttpSession> httpSessionFactory;

    @Autowired
    private DownloadService downloadService;

    public DownloadResource() {}

    @GetMapping("/api/getzip/{id}")
    public ResponseEntity<byte[]> getzip(HttpServletResponse response) {
        log.debug("REST request to getZipFile");
        HttpSession session = httpSessionFactory.getObject();
        String password = SecurityUtils.getMgrWebToken().getDecryptedCredentials();
        String userid = SecurityUtils.getCurrentUserLogin().get();
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=tmpfile.zip");
        return downloadService.getzip(userid, password);
    }

    @GetMapping("/api/getfile/{id}")
    public ResponseEntity<byte[]> getfile(HttpServletResponse response) {
        log.debug("REST request to getFile");
        HttpSession session = httpSessionFactory.getObject();
        // String userid = (String) session.getAttribute(Constants.USERNAME);
        // String password = (String) session.getAttribute(Constants.USERPASS);
        String password = SecurityUtils.getMgrWebToken().getDecryptedCredentials();
        String userid = SecurityUtils.getCurrentUserLogin().get();
        ResponseEntity<byte[]> downloadResponse = downloadService.getfile(userid, password);

        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tmpfile.csv");
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        byte[] content = downloadResponse.getBody();
        return ResponseEntity
            .ok()
            .headers(header)
            .contentLength(content.length)
            .contentType(MediaType.parseMediaType("application/octet-stream"))
            .body(content);
    }
}
