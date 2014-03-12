package org.ict4h.atomfeed.server.repository;

import org.ict4h.atomfeed.IntegrationTest;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsJdbcImpl;
import org.ict4h.atomfeed.server.repository.jdbc.ChunkingEntriesJdbcImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@Ignore
public class ChunkingHistoryEntryIT extends IntegrationTest {

    private JdbcConnectionProvider connectionProvider;

    @Before
    public void before() throws SQLException {
        connectionProvider = getConnectionProvider();
    }

    @After
    public void after() throws SQLException {
        connectionProvider.getConnection().close();
    }

    @Test
    public void shouldGetAllChunkingHistoryEntries() throws SQLException {
        List<?> items = new ChunkingEntriesJdbcImpl(getConnectionProvider()).all();
        assertNotNull(items);
        assertTrue(items.size() > 0);
    }
}
