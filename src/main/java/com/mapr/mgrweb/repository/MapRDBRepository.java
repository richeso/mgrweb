package com.mapr.mgrweb.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.ojai.Document;
import org.ojai.store.DocumentStore;
import org.ojai.store.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

abstract class MapRDBRepository<A extends MapRDBEntity> {

    public abstract String getTable();

    @Autowired
    protected MapRDBSession mapRDBSession;

    public void deleteAll() {
        DocumentStore store = mapRDBSession.getStore(getTable());

        store.find().forEach(store::delete);

        store.flush();
    }

    public void delete(A entity) {
        DocumentStore store = mapRDBSession.getStore(getTable());
        store.delete(entity.get_id());
        store.flush();
    }

    public void save(A entity) {
        DocumentStore store = mapRDBSession.getStore(getTable());
        String json = Mapper.write(entity);
        store.insertOrReplace(mapRDBSession.getConnection().newDocument(json));
        store.flush();
    }

    public int count() {
        Query query = mapRDBSession.getConnection().newQuery().select("_id").build();

        return StreamSupport
            .stream(mapRDBSession.getStore(getTable()).find(query).spliterator(), false)
            .map(doc -> 1)
            .reduce(0, Integer::sum);
    }

    protected List<A> findAll(Class<A> theClass) {
        return StreamSupport
            .stream(mapRDBSession.getStore(getTable()).find().spliterator(), false)
            .map(doc -> Mapper.read(doc.asJsonString(), theClass))
            .collect(Collectors.toList());
    }

    protected Page<A> findAll(Pageable pageable, Class<A> theClass) {
        Query query = addPagingSupport(findAllQuery(), pageable).build();

        List<A> result = StreamSupport
            .stream(mapRDBSession.getStore(getTable()).find(query).spliterator(), false)
            .map(doc -> Mapper.read(doc.asJsonString(), theClass))
            .collect(Collectors.toList());

        return new PageImpl<>(result, pageable, count());
    }

    public abstract Page<A> findAll(Pageable pageable);

    public abstract List<A> findAll();

    protected Optional<A> findById(String id, Class<A> theClass) {
        Document doc = mapRDBSession.getStore(getTable()).findById(id);

        if (doc == null || doc.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(Mapper.read(doc.asJsonString(), theClass));
        }
    }

    private Query findAllQuery() {
        return mapRDBSession.getConnection().newQuery();
    }

    protected Query addPagingSupport(Query query, Pageable pageable) {
        return query.offset(pageable.getOffset()).limit(pageable.getPageSize());
    }

    public abstract Optional<A> findById(String id);
}
