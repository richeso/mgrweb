package com.mapr.mgrweb.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mapr.mgrweb.repository.MapRDBEntity;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.data.annotation.Id;

/**
 * A MaprRequests.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MaprRequests extends AbstractAuditingEntity implements Serializable, MapRDBEntity {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Size(min = 3)
    private String type;

    @NotNull
    @Size(min = 3)
    private String action;

    @NotNull
    @Size(min = 3)
    private String name;

    @NotNull
    @Size(min = 3)
    private String path;

    @NotNull
    @Size(min = 3)
    private String requestUser;

    @NotNull
    private Instant requestDate;

    @NotNull
    @Size(min = 3)
    private String status;

    @NotNull
    private Instant statusDate;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public MaprRequests id(String id) {
        this.setId(id);
        return this;
    }

    public void initNewId() {
        setId(UUID.randomUUID().toString());
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

    public String getType() {
        return this.type;
    }

    public MaprRequests type(String type) {
        this.setType(type);
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return this.action;
    }

    public MaprRequests action(String action) {
        this.setAction(action);
        return this;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getName() {
        return this.name;
    }

    public MaprRequests name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return this.path;
    }

    public MaprRequests path(String path) {
        this.setPath(path);
        return this;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRequestUser() {
        return this.requestUser;
    }

    public MaprRequests requestUser(String requestUser) {
        this.setRequestUser(requestUser);
        return this;
    }

    public void setRequestUser(String requestUser) {
        this.requestUser = requestUser;
    }

    public Instant getRequestDate() {
        return this.requestDate;
    }

    public MaprRequests requestDate(Instant requestDate) {
        this.setRequestDate(requestDate);
        return this;
    }

    public void setRequestDate(Instant requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return this.status;
    }

    public MaprRequests status(String status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getStatusDate() {
        return this.statusDate;
    }

    public MaprRequests statusDate(Instant statusDate) {
        this.setStatusDate(statusDate);
        return this;
    }

    public void setStatusDate(Instant statusDate) {
        this.statusDate = statusDate;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

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
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MaprRequests{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", action='" + getAction() + "'" +
            ", name='" + getName() + "'" +
            ", path='" + getPath() + "'" +
            ", requestUser='" + getRequestUser() + "'" +
            ", requestDate='" + getRequestDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", statusDate='" + getStatusDate() + "'" +
            "}";
    }
}
