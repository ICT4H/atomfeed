package org.ict4h.atomfeed.server.repository;

import org.ict4h.atomfeed.IntegrationTest;
import org.ict4h.atomfeed.server.domain.chunking.ChunkingHistoryEntry;
import org.ict4h.atomfeed.server.repository.jdbc.ChunkingEntriesJdbcImpl;
import org.junit.After;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class ChunkingHistoryEntryIT extends IntegrationTest {

    private Connection connection;

    @After
    public void after() throws SQLException {
       connection.close();
    }

    @Test
    public void shouldGetAllChunkingHistoryEntries() throws SQLException {
        connection = getConnection();
        List<?> items = new ChunkingEntriesJdbcImpl(getProvider(connection)).all();
        assertNotNull(items);
        assertTrue(items.size() > 0);
    }
}
