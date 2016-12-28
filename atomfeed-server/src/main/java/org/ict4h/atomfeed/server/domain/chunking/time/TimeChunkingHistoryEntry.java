package org.ict4h.atomfeed.server.domain.chunking.time;

import org.ict4h.atomfeed.server.exceptions.AtomFeedRuntimeException;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeChunkingHistoryEntry {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration duration;

    public TimeChunkingHistoryEntry(LocalDateTime startTime, LocalDateTime endTime, Duration duration) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
    }

    public int numberOfFeeds() {
        if (isUnbounded()) throw new AtomFeedRuntimeException("Number of feeds is not defined for an unbounded entry.");
        return numberOfFeeds(startTime, endTime);
    }

    private int numberOfFeeds(LocalDateTime start, LocalDateTime end) {
        long minutesElapsed = Duration.between(start,end).toMinutes();
        long minutesInTheDuration = duration.toMinutes();
        return (int) (minutesElapsed / minutesInTheDuration);
    }

    public TimeRange getTimeRangeForChunk(int chunkNumber) {
        return new TimeRange(timeAtStartOf(chunkNumber - 1), timeAtStartOf(chunkNumber));
    }

    private LocalDateTime timeAtStartOf(int chunkNumber) {
        return startTime.plusMinutes(chunkNumber * (int) duration.toMinutes());
    }

    @Override
    public String toString() {
        return "TimeChunkingHistoryEntry{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", duration=" + duration +
                '}';
    }

    public boolean isUnbounded() {
        return endTime == null;
    }

    public void enforceRightBound(long rightBound) {
        endTime = Instant.ofEpochMilli(rightBound).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public long numberOfEncapsulatedFeeds() {
        if(isUnbounded())
        {
            return numberOfFeeds(startTime, LocalDateTime.now());
        }
        return numberOfFeeds(startTime,endTime);
    }
}