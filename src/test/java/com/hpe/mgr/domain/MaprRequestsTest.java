package com.hpe.mgr.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.hpe.mgr.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MaprRequestsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MaprRequests.class);
        MaprRequests maprRequests1 = new MaprRequests();
        maprRequests1.setId("id1");
        MaprRequests maprRequests2 = new MaprRequests();
        maprRequests2.setId(maprRequests1.getId());
        assertThat(maprRequests1).isEqualTo(maprRequests2);
        maprRequests2.setId("id2");
        assertThat(maprRequests1).isNotEqualTo(maprRequests2);
        maprRequests1.setId(null);
        assertThat(maprRequests1).isNotEqualTo(maprRequests2);
    }
}
