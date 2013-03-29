package org.ict4h.atomfeed.server.domain.chunking.number;

public class NumberChunkingHistoryEntry {

    private int sequenceNumber;
    private int chunkLength;
    private int leftBound;
    private int rightBound;
    private static final int UNBOUNDED = -1;

    public NumberChunkingHistoryEntry(int sequenceNumber, int chunkSize, Integer startPos) {
        this.sequenceNumber = sequenceNumber;
        this.chunkLength = chunkSize;
        this.leftBound = (startPos == null) ? 1 : startPos;
        this.rightBound = UNBOUNDED;
    }

    public int getFeedCount(int upperBound) {
        int endPosition = isOpen() ? upperBound : rightBound;
        int count = ((Math.min(endPosition, upperBound) - leftBound) + 1);
        if (count <= 0) return 0;
        Double feedCount = Math.ceil((count * 1.0) / chunkLength);
        return feedCount.intValue();
    }

    public NumberRange getRange(Integer relativeFeedId) {
        int start = leftBound + (relativeFeedId - 1) * chunkLength;
        int naturalLimit = start + chunkLength - 1;
        int end = isOpen() ? naturalLimit : Math.min(rightBound, naturalLimit);
        return new NumberRange(start, end);
    }

    public boolean isOpen() {
        return (rightBound == UNBOUNDED);
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public int getChunkSize() {
        return chunkLength;
    }

    public int getStartPosition() {
        return leftBound;
    }


    public void close(int endPosition) {
        this.rightBound = endPosition;
    }
}