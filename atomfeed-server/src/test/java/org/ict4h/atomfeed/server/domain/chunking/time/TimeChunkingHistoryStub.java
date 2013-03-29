package org.ict4h.atomfeed.server.domain.chunking.time;

import java.util.Collections;
import java.util.HashMap;

public class TimeChunkingHistoryStub extends TimeChunkingHistory {
    private HashMap<Integer,TimeRange> map = new HashMap<Integer, TimeRange>();
    private long currentSequenceNumber;

    public void setCurrentSequenceNumber(long currentSequenceNumber){
        this.currentSequenceNumber = currentSequenceNumber;
    }

    @Override
    public TimeRange timeRangeFor(int sequenceNumber) {
        return map.get(new Integer(sequenceNumber));
    }

    @Override
    public long currentSequenceNumber() {
        return this.currentSequenceNumber;
    }

    public void setTimeRange(Integer sequenceNumber, TimeRange range){
        map.put(sequenceNumber,range);
    }
}
