package com.mapr.mgrweb.repository;

import com.mapr.mgrweb.config.Constants;
import com.mapr.mgrweb.domain.MaprRequests;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.ojai.store.Connection;
import org.ojai.store.DocumentStore;
import org.ojai.store.Query;
import org.ojai.store.QueryCondition;
import org.ojai.types.OTimestamp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class MaprRequestsRepository extends MapRDBRepository<MaprRequests> {

    @Value("${mapr.tables.maprRequests}")
    private String maprRequestsTable;

    @Override
    public String getTable() {
        return maprRequestsTable;
    }

    @Override
    public Page<MaprRequests> findAll(Pageable pageable) {
        return super.findAll(pageable, MaprRequests.class);
    }

    public Optional<MaprRequests> findById(String id) {
        return super.findById(id, MaprRequests.class);
    }

    public List<MaprRequests> findAll() {
        return this.findAll(MaprRequests.class);
    }

    public void delete(String id) throws RuntimeException {
        DocumentStore store = mapRDBSession.getStore(getTable());
        // do a logical delete .. not a physical delete
        MaprRequests request = findById(id).orElse(null);
        request.setStatus(Constants.DELETED_STATUS);
        request.setStatusDate(new Date(System.currentTimeMillis()).toInstant());
        this.save(request);
    }

    public void deletePhysical(String id) {
        DocumentStore store = mapRDBSession.getStore(getTable());
        store.delete(id);
        store.flush();
    }

    public List<MaprRequests> findByRequested(String requestedBy) {
        DocumentStore store = mapRDBSession.getStore(maprRequestsTable);
        Connection connection = mapRDBSession.getConnection();
        QueryCondition condition = mapRDBSession
            .getConnection()
            .newCondition()
            .is("requestUser", QueryCondition.Op.EQUAL, requestedBy)
            .build();
        Query query = connection.newQuery().where(condition).build();

        return StreamSupport
            .stream(store.find(query).spliterator(), false)
            .map(doc -> Mapper.read(doc.asJsonString(), MaprRequests.class))
            .collect(Collectors.toList());
    }

    public List<MaprRequests> findActiveByName(String name) {
        DocumentStore store = mapRDBSession.getStore(maprRequestsTable);
        Connection connection = mapRDBSession.getConnection();
        QueryCondition condition = connection
            .newCondition()
            .and()
            .condition(connection.newCondition().is("name", QueryCondition.Op.EQUAL, name).build())
            .condition(connection.newCondition().is("status", QueryCondition.Op.EQUAL, Constants.CREATED_STATUS).build())
            .close()
            .build();

        Query query = connection.newQuery().where(condition).build();

        return StreamSupport
            .stream(store.find(query).spliterator(), false)
            .map(doc -> Mapper.read(doc.asJsonString(), MaprRequests.class))
            .collect(Collectors.toList());
    }

    public List<MaprRequests> findByName(String name) {
        DocumentStore store = mapRDBSession.getStore(maprRequestsTable);
        Connection connection = mapRDBSession.getConnection();
        QueryCondition condition = connection
            .newCondition()
            .and()
            .condition(connection.newCondition().is("name", QueryCondition.Op.EQUAL, name).build())
            .close()
            .build();

        Query query = connection.newQuery().where(condition).build();

        return StreamSupport
            .stream(store.find(query).spliterator(), false)
            .map(doc -> Mapper.read(doc.asJsonString(), MaprRequests.class))
            .collect(Collectors.toList());
    }
}
