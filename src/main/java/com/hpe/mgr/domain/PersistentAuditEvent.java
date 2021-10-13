package com.hpe.mgr.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hpe.mgr.repository.MapRDBEntity;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;

/**
 * Persist AuditEvent managed by the Spring Boot actuator.
 *
 * @see org.springframework.boot.actuate.audit.AuditEvent
 */

public class PersistentAuditEvent implements Serializable, MapRDBEntity {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    private String principal;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Instant auditEventDate;

    private String auditEventType;

    private Map<String, String> data = new HashMap<>();

    public String get_id() {
        return id;
    }

    @Override
    public void set_id(String _id) {
        this.id = _id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PersistentAuditEvent() {
        setId(UUID.randomUUID().toString());
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public Instant getAuditEventDate() {
        return auditEventDate;
    }

    public void setAuditEventDate(Instant auditEventDate) {
        this.auditEventDate = auditEventDate;
    }

    public String getAuditEventType() {
        return auditEventType;
    }

    public void setAuditEventType(String auditEventType) {
        this.auditEventType = auditEventType;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PersistentAuditEvent)) {
            return false;
        }
        return id != null && id.equals(((PersistentAuditEvent) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return (
            "PersistentAuditEvent{" +
            "principal='" +
            principal +
            '\'' +
            ", auditEventDate=" +
            auditEventDate +
            ", auditEventType='" +
            auditEventType +
            '\'' +
            '}'
        );
    }
}
