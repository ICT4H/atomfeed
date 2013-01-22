package org.ict4htw.atomfeed.server.domain.timebasedchunkingconfiguration;

import org.ict4htw.atomfeed.server.exceptions.AtomFeedRuntimeException;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.joda.time.Minutes;

public class TimeBasedChunkingHistoryEntry {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Duration duration;

    public TimeBasedChunkingHistoryEntry(LocalDateTime startTime, LocalDateTime endTime, Duration duration) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
    }

    public int numberOfFeeds() {
        if (isUnbounded()) throw new AtomFeedRuntimeException("Number of feeds is not defined for an unbounded entry.");
        return numberOfFeeds(startTime, endTime);
    }

    public int numberOfFeedsUpTo(LocalDateTime cutoff) {
        return numberOfFeeds(startTime, LocalDateTime.now());
    }

    private int numberOfFeeds(LocalDateTime start, LocalDateTime end) {
        int minutesElapsed = Minutes.minutesBetween(start, end).getMinutes();
        long minutesInTheDuration = duration.getStandardMinutes();
        return (int) (minutesElapsed / minutesInTheDuration);
    }

    public TimeRange getTimeRangeForChunk(int chunkNumber) {
        return new TimeRange(timeAtStartOf(chunkNumber - 1), timeAtStartOf(chunkNumber));
    }

    private LocalDateTime timeAtStartOf(int chunkNumber) {
        return startTime.plusMinutes(chunkNumber * (int) duration.getStandardMinutes());
    }

    @Override
    public String toString() {
        return "TimeBasedChunkingHistoryEntry{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", duration=" + duration +
                '}';
    }

    public boolean isUnbounded() {
        return endTime == null;
    }
}