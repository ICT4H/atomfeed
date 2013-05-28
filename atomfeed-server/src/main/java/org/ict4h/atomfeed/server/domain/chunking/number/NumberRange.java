package org.ict4h.atomfeed.server.domain.chunking.number;

public class NumberRange {
    Integer offset;
    Integer limit;

    public NumberRange(Integer offset, Integer limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public Integer getLimit() {
        return limit;
    }
}
