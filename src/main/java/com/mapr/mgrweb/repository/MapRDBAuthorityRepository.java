package com.mapr.mgrweb.repository;

import com.mapr.mgrweb.domain.Authority;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class MapRDBAuthorityRepository extends MapRDBRepository<Authority> {

    @Value("${mapr.tables.authoritiesTable}")
    private String authoritiesTable;

    @Override
    public String getTable() {
        return authoritiesTable;
    }

    @Override
    public Page<Authority> findAll(Pageable pageable) {
        return super.findAll(pageable, Authority.class);
    }

    @Override
    public List<Authority> findAll() {
        return super.findAll(Authority.class);
    }

    @Override
    public Optional<Authority> findById(String id) {
        return super.findById(id, Authority.class);
    }
}
