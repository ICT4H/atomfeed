package org.ict4htw.atomfeed.server.domain.numberbasedchunkingconfiguration;

public class NumberRange {
    Integer first;
    Integer last;

    public NumberRange(Integer first, Integer last) {
        this.first = first;
        this.last = last;
    }

    public Integer getFirst() {
        return first;
    }

    public Integer getLast() {
        return last;
    }
}
