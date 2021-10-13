package com.hpe.mgr.repository;

import com.hpe.mgr.domain.MaprRequests;
import java.util.List;
import java.util.Optional;
import org.ojai.store.DocumentStore;
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

    public void delete(String id) {
        DocumentStore store = mapRDBSession.getStore(getTable());
        store.delete(id);
        store.flush();
    }
}
