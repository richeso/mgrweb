package com.hpe.mgr.repository;

import com.hpe.mgr.domain.User;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.ojai.Document;
import org.ojai.store.Connection;
import org.ojai.store.DocumentStore;
import org.ojai.store.Query;
import org.ojai.store.QueryCondition;
import org.ojai.types.OTimestamp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class MapRUserRepository extends MapRDBRepository<User> {

    @Value("${mapr.tables.usersTable}")
    private String usersTable;

    public static final String USERS_BY_LOGIN_CACHE = "usersByLogin";

    public static final String USERS_BY_EMAIL_CACHE = "usersByEmail";

    @Override
    public String getTable() {
        return usersTable;
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return super.findAll(pageable, User.class);
    }

    public Optional<User> findOneByActivationKey(String activationKey) {
        QueryCondition condition = mapRDBSession
            .getConnection()
            .newCondition()
            .is("activationKey", QueryCondition.Op.EQUAL, activationKey)
            .build();

        return findFirst(condition);
    }

    public List<User> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant dateTime) {
        DocumentStore store = mapRDBSession.getStore(usersTable);
        Connection connection = mapRDBSession.getConnection();

        QueryCondition condition = connection
            .newCondition()
            .and()
            .condition(connection.newCondition().is("activated", QueryCondition.Op.EQUAL, false).build())
            .condition(connection.newCondition().exists("activationKey").build())
            .condition(
                connection
                    .newCondition()
                    .is("createdDate", QueryCondition.Op.LESS, new OTimestamp(dateTime.toEpochMilli()).toUTCString())
                    .build()
            )
            .close()
            .build();

        Query query = connection.newQuery().where(condition).build();

        return StreamSupport
            .stream(store.find(query).spliterator(), false)
            .map(doc -> Mapper.read(doc.asJsonString(), User.class))
            .collect(Collectors.toList());
    }

    public Optional<User> findOneByResetKey(String resetKey) {
        QueryCondition condition = mapRDBSession.getConnection().newCondition().is("resetKey", QueryCondition.Op.EQUAL, resetKey).build();

        return findFirst(condition);
    }

    @Cacheable(cacheNames = USERS_BY_EMAIL_CACHE)
    public Optional<User> findOneByEmailIgnoreCase(String email) {
        QueryCondition condition = mapRDBSession
            .getConnection()
            .newCondition()
            .is("email", QueryCondition.Op.EQUAL, email.toLowerCase())
            .build();

        return findFirst(condition);
    }

    @Cacheable(cacheNames = USERS_BY_LOGIN_CACHE)
    public Optional<User> findOneByLogin(String login) {
        QueryCondition condition = mapRDBSession.getConnection().newCondition().is("login", QueryCondition.Op.EQUAL, login).build();

        return findFirst(condition);
    }

    public Page<User> findAllByLoginNot(Pageable pageable, String login) {
        QueryCondition condition = mapRDBSession.getConnection().newCondition().is("login", QueryCondition.Op.NOT_EQUAL, login).build();

        Query query = mapRDBSession.getConnection().newQuery().where(condition).build();

        List<User> result = StreamSupport
            .stream(mapRDBSession.getStore(usersTable).find(query).spliterator(), false)
            .map(doc -> Mapper.read(doc.asJsonString(), User.class))
            .collect(Collectors.toList());

        return new PageImpl<>(result);
    }

    public Optional<User> findById(String id) {
        return super.findById(id, User.class);
    }

    public List<User> findAll() {
        return this.findAll(User.class);
    }

    private Optional<User> findFirst(QueryCondition condition) {
        Query query = mapRDBSession.getConnection().newQuery().where(condition).build();

        Iterator<Document> users = mapRDBSession.getStore(usersTable).find(query).iterator();

        if (users.hasNext()) {
            String string = users.next().asJsonString();
            User user = Mapper.read(string, User.class);
            return Optional.ofNullable(user);
        } else {
            return Optional.empty();
        }
    }
}
