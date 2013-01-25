package org.ict4htw.atomfeed.server.domain.numberbasedchunkingconfiguration;

import javax.persistence.*;

@Entity
@Table(name ="number_based_chunking_histories")
public class NumberBasedChunkingHistoryEntry {
    public class Range {
        public final Integer first;
        public final Integer last;

        public Range(Integer first, Integer last) {
            this.first = first;
            this.last = last;
        }
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int seqNum;

    @Column(name = "chunk_size")
    private int chunkSize;

    @Column(name = "start_pos")
    private int startPos;

    @Transient
    private int endPos;
    private static final int UNBOUNDED = -1;

    public NumberBasedChunkingHistoryEntry(int seqNum, int chunkSize, Integer startPos) {
        this.seqNum = seqNum;
        this.chunkSize = chunkSize;
        this.startPos = (startPos == null) ? 1 : startPos;
        this.endPos = UNBOUNDED;
    }

    public NumberBasedChunkingHistoryEntry(){}

    public int getFeedCount(int upperBound) {
        int endPosition = isOpen() ? upperBound : endPos;
        int count = ((Math.min(endPosition, upperBound) - startPos) + 1);
        if (count <= 0) return 0;
        Double feedCount = Math.ceil((count * 1.0) / chunkSize);
        return feedCount.intValue();
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