package org.ict4htw.atomfeed.server.repository;

import org.ict4htw.atomfeed.SpringIntegrationIT;
import org.ict4htw.atomfeed.server.domain.numberbasedchunkingconfiguration.NumberBasedChunkingHistoryEntry;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class NumberBasedChunkingHistoryEntryIT extends SpringIntegrationIT{

    @Test
    public void shouldGetAllNumberBasedChunkingHistoryEntries(){
        ChunkingHistoryEntryImpl entry = new ChunkingHistoryEntryImpl(template);
        List<NumberBasedChunkingHistoryEntry> items = entry.all();
        assertNotNull(items);
        assertTrue(items.size() > 0);
    }
}
