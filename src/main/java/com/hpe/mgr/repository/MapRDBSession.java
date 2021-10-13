package com.hpe.mgr.repository;

import com.mapr.db.MapRDB;
import java.util.Hashtable;
import java.util.Map;
import org.ojai.store.Connection;
import org.ojai.store.DocumentStore;
import org.ojai.store.DriverManager;
import org.springframework.stereotype.Component;

@Component
class MapRDBSession implements AutoCloseable {

    private final Connection connection = DriverManager.getConnection("ojai:mapr:");

    private Map<String, DocumentStore> stores = new Hashtable<>();

    public DocumentStore getStore(String storePath) {
        if (!stores.containsKey(storePath)) {
            DocumentStore store = connection.getStore(storePath);
            if (store == null) {
                store = MapRDB.createTable(storePath);
            }
            stores.put(storePath, store);
        }

        return stores.get(storePath);
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public void close() throws Exception {
        stores.forEach((path, store) -> store.close());
        connection.close();
    }
}
