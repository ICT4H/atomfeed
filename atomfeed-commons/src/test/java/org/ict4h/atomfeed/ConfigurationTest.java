package org.ict4h.atomfeed;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfigurationTest {
    @Test
    public void testGetInstance() throws Exception {
        Configuration configuration1 = Configuration.getInstance();
        Configuration configuration2 = Configuration.getInstance();

        assertEquals(configuration1, configuration2);
    }

    //@Test
    public void testProperties() throws Exception {
        Configuration configuration = Configuration.getInstance();

        assertEquals("jdbc:postgresql://localhost/atomfeed/", configuration.getJdbcUrl());
        assertEquals("postgres", configuration.getJdbcUsername());
        assertEquals("admin", configuration.getJdbcPassword());
        assertEquals("atomfeed", configuration.getSchema());
    }
}
