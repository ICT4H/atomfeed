package org.ict4h.atomfeed.server.domain.timebasedchunkingconfiguration;

import org.joda.time.LocalDateTime;

import java.sql.Timestamp;

public class TimeRange {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Timestamp startTimestamp;
    private Timestamp endTimeStamp;

    public TimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public TimeRange(Timestamp startTimestamp, Timestamp endTimeStamp){

        this.startTimestamp = startTimestamp;
        this.endTimeStamp = endTimeStamp;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Timestamp getStartTimestamp() {
        return startTimestamp;
    }

    public Timestamp getEndTimeStamp() {
        return endTimeStamp;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeRange timeRange = (TimeRange) o;

        if (endTime != null ? !endTime.equals(timeRange.endTime) : timeRange.endTime != null) return false;
        if (!startTime.equals(timeRange.startTime)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = startTime.hashCode();
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TimeRange{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}