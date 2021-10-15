package com.mapr.mgrweb.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mapr.mgrweb.repository.MapRDBEntity;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MaprRequests extends AbstractAuditingEntity implements Serializable, MapRDBEntity {

    private static final long serialVersionUID = 1L;

    private String id;
    private String type;
    private String action;
    private String name;
    private String path;
    private String source;
    private String quota;
    private String advisoryQuota;
    private String requestUser;
    private Instant requestDate;
    private String requestStatus;
    private Instant statusChangedDate;
    private String previousStatus;

    public MaprRequests() {
        setId(null);
    }

    public void initNewId() {
        setId(UUID.randomUUID().toString());
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String get_id() {
        return id;
    }

    @Override
    public void set_id(String _id) {
        this.id = _id;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("action")
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("path")
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @JsonProperty("source")
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @JsonProperty("quota")
    public String getQuota() {
        return quota;
    }

    public void setQuota(String quota) {
        this.quota = quota;
    }

    @JsonProperty("advisoryQuota")
    public String getAdvisoryQuota() {
        return advisoryQuota;
    }

    public void setAdvisoryQuota(String advisoryQuota) {
        this.advisoryQuota = advisoryQuota;
    }

    @JsonProperty("requestUser")
    public String getRequestUser() {
        return requestUser;
    }

    public void setRequestUser(String requestUser) {
        this.requestUser = requestUser;
    }

    @JsonProperty("requestDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    public Instant getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Instant requestDate) {
        this.requestDate = requestDate;
    }

    @JsonProperty("requestStatus")
    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    @JsonProperty("statusChangedDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", timezone = "UTC")
    public Instant getStatusChangedDate() {
        return statusChangedDate;
    }

    public void setStatusChangedDate(Instant statusChangedDate) {
        this.statusChangedDate = statusChangedDate;
    }

    @JsonProperty("previousStatus")
    public String getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MaprRequests)) {
            return false;
        }
        return id != null && id.equals(((MaprRequests) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        StringBuilder rval = new StringBuilder();
        rval
            .append("{\"id\":\"")
            .append(id)
            .append("\",\"type\":\"")
            .append(type)
            .append("\",\"action:\"")
            .append(action)
            .append("\",\"name:\"")
            .append(name)
            .append("\",\"path:\"")
            .append(path)
            .append("\",\"source:\"")
            .append(source)
            .append("\",\"quota:\"")
            .append(quota)
            .append("\",\"advisoryQuota:\"")
            .append(advisoryQuota)
            .append("\",\"requestUser:\"")
            .append(requestUser)
            .append("\",\"requestDate:\"")
            .append(requestDate)
            .append("\",\"requestStatus:\"")
            .append(requestStatus)
            .append("\",\"statusChangedDate:\"")
            .append(statusChangedDate)
            .append("\",\"previousStatus:\"")
            .append(previousStatus)
            .append("\"}");
        return rval.toString();
    }
}
