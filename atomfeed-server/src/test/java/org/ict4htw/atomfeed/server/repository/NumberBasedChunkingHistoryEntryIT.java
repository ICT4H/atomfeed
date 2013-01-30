package org.ict4htw.atomfeed.server.repository;

import org.ict4htw.atomfeed.SpringIntegrationIT;
import org.ict4htw.atomfeed.server.domain.numberbasedchunkingconfiguration.NumberBasedChunkingHistoryEntry;
import org.ict4htw.atomfeed.server.repository.jdbc.AllChunkingEntriesJdbcImpl;
import org.junit.After;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class NumberBasedChunkingHistoryEntryIT extends SpringIntegrationIT{

    private Connection connection;

    @After
    public void after() throws SQLException {
       connection.close();
    }

    @Test
    public void shouldGetAllNumberBasedChunkingHistoryEntries() throws SQLException {
        connection = getConnection();
        ChunkingHistories entry = new AllChunkingEntriesJdbcImpl(getProvider(connection));
        List<?> items = entry.all(NumberBasedChunkingHistoryEntry.class);
        assertNotNull(items);
        assertTrue(items.size() > 0);
    }
}
