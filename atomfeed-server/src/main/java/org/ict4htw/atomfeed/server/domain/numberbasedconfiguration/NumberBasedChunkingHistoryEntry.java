package org.ict4htw.atomfeed.server.domain.numberbasedconfiguration;


public class NumberBasedChunkingHistoryEntry {
    public class Range {
        public final Integer first;
        public final Integer last;

        public Range(Integer first, Integer last) {
            this.first = first;
            this.last = last;
        }
    }

    private int seqNum;
    private int chunkSize;
    private int endPos;
    private int startPos;

    private static final int UNBOUNDED = -1;

    public NumberBasedChunkingHistoryEntry(int seqNum, int chunkSize, Integer startPos) {
        this.seqNum = seqNum;
        this.chunkSize = chunkSize;
        this.startPos = (startPos == null) ? 1 : startPos;
        this.endPos = UNBOUNDED;
    }

    public int getFeedCount(int upperBound) {
        int endPosition = isOpen() ? upperBound : endPos;
        int count = ((Math.min(endPosition, upperBound) - startPos) + 1);
        if (count <= 0) return 0;
        Double feedCnt = Math.ceil((count * 1.0) / chunkSize);
        return feedCnt.intValue();
    }

    public Range getRange(Integer relativeFeedId) {
        int start = startPos + (relativeFeedId - 1) * chunkSize;
        int naturalLimit = start + chunkSize - 1;
        int end = isOpen() ? naturalLimit : Math.min(endPos, naturalLimit);
        return new Range(start, end);
    }

    public boolean isOpen() {
        return (endPos == UNBOUNDED);
    }

    public int getSeqNum() {
        return seqNum;
    }

    public void close(int endPosition) {
        this.endPos = endPosition;
    }
}