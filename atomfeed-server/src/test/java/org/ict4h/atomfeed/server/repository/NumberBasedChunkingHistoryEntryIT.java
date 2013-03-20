package org.ict4h.atomfeed.server.repository;

import org.ict4h.atomfeed.IntegrationTest;
import org.ict4h.atomfeed.server.domain.numberbasedchunkingconfiguration.NumberBasedChunkingHistoryEntry;
import org.ict4h.atomfeed.server.repository.jdbc.ChunkingEntriesJdbcImpl;
import org.junit.After;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class NumberBasedChunkingHistoryEntryIT extends IntegrationTest {

    private Connection connection;

    @After
    public void after() throws SQLException {
       connection.close();
    }

    @Test
    public void shouldGetAllNumberBasedChunkingHistoryEntries() throws SQLException {
        connection = getConnection();
        List<?> items = new ChunkingEntriesJdbcImpl(getProvider(connection)).all(NumberBasedChunkingHistoryEntry.class);
        assertNotNull(items);
        assertTrue(items.size() > 0);
    }
}
