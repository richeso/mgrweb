package com.hpe.mgr.repository;

import com.hpe.mgr.domain.PersistentAuditEvent;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.ojai.store.Connection;
import org.ojai.store.Query;
import org.ojai.store.QueryCondition;
import org.ojai.types.OTimestamp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class MapRAuditEventRepository extends MapRDBRepository<PersistentAuditEvent> {

    @Value("${mapr.tables.auditeEventsTable}")
    private String auditEventsTable;

    @Override
    public String getTable() {
        return auditEventsTable;
    }

    @Override
    public Page<PersistentAuditEvent> findAll(Pageable pageable) {
        return super.findAll(pageable, PersistentAuditEvent.class);
    }

    @Override
    public List<PersistentAuditEvent> findAll() {
        return super.findAll(PersistentAuditEvent.class);
    }

    @Override
    public Optional<PersistentAuditEvent> findById(String id) {
        return super.findById(id, PersistentAuditEvent.class);
    }

    public List<PersistentAuditEvent> findByPrincipal(String principal) {
        QueryCondition condition = mapRDBSession.getConnection().newCondition().is("principal", QueryCondition.Op.EQUAL, principal).build();

        Query query = mapRDBSession.getConnection().newQuery().where(condition).build();

        return StreamSupport
            .stream(mapRDBSession.getStore(auditEventsTable).find(query).spliterator(), false)
            .map(doc -> Mapper.read(doc.asJsonString(), PersistentAuditEvent.class))
            .collect(Collectors.toList());
    }

    public List<PersistentAuditEvent> findByPrincipalAndAuditEventDateAfterAndAuditEventType(String principal, Instant after, String type) {
        Connection connection = mapRDBSession.getConnection();

        QueryCondition condition = connection
            .newCondition()
            .and()
            .condition(connection.newCondition().is("principal", QueryCondition.Op.EQUAL, principal).build())
            .condition(
                connection
                    .newCondition()
                    .is("auditEventDate", QueryCondition.Op.GREATER, new OTimestamp(after.toEpochMilli()).toUTCString())
                    .build()
            )
            .condition(connection.newCondition().is("auditEventType", QueryCondition.Op.EQUAL, type).build())
            .close()
            .build();

        Query query = mapRDBSession.getConnection().newQuery().where(condition).build();

        return StreamSupport
            .stream(mapRDBSession.getStore(auditEventsTable).find(query).spliterator(), false)
            .map(doc -> Mapper.read(doc.asJsonString(), PersistentAuditEvent.class))
            .collect(Collectors.toList());
    }

    public Page<PersistentAuditEvent> findAllByAuditEventDateBetween(Instant fromDate, Instant toDate, Pageable pageable) {
        Connection connection = mapRDBSession.getConnection();

        QueryCondition condition = connection
            .newCondition()
            .and()
            .condition(
                connection
                    .newCondition()
                    .is("auditEventDate", QueryCondition.Op.GREATER_OR_EQUAL, new OTimestamp(fromDate.toEpochMilli()).toUTCString())
                    .build()
            )
            .condition(
                connection
                    .newCondition()
                    .is("auditEventDate", QueryCondition.Op.LESS_OR_EQUAL, new OTimestamp(toDate.toEpochMilli()).toUTCString())
                    .build()
            )
            .close()
            .build();

        Query query = mapRDBSession.getConnection().newQuery().where(condition);

        Query queryWithPage = addPagingSupport(query, pageable).build();

        List<PersistentAuditEvent> result = StreamSupport
            .stream(mapRDBSession.getStore(auditEventsTable).find(queryWithPage).spliterator(), false)
            .map(doc -> Mapper.read(doc.asJsonString(), PersistentAuditEvent.class))
            .collect(Collectors.toList());

        return new PageImpl<>(result, pageable, count());
    }

    public List<PersistentAuditEvent> findByAuditEventDateBefore(Instant before) {
        Connection connection = mapRDBSession.getConnection();

        QueryCondition condition = connection
            .newCondition()
            .is("auditEventDate", QueryCondition.Op.LESS_OR_EQUAL, new OTimestamp(before.toEpochMilli()).toUTCString())
            .build();

        Query query = mapRDBSession.getConnection().newQuery().where(condition).build();

        return StreamSupport
            .stream(mapRDBSession.getStore(auditEventsTable).find(query).spliterator(), false)
            .map(doc -> Mapper.read(doc.asJsonString(), PersistentAuditEvent.class))
            .collect(Collectors.toList());
    }
}
