package org.ict4htw.atomfeed.server.repository;

import org.ict4htw.atomfeed.SpringIntegrationIT;
import org.ict4htw.atomfeed.server.domain.numberbasedchunkingconfiguration.NumberBasedChunkingHistoryEntry;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class NumberBasedChunkingHistoryEntryIT extends SpringIntegrationIT{

    @Autowired
    private ChunkingHistories entry;

    @Test
    public void shouldGetAllNumberBasedChunkingHistoryEntries(){
        List<?> items = entry.all(NumberBasedChunkingHistoryEntry.class);
        assertNotNull(items);
        assertTrue(items.size() > 0);
    }
}
