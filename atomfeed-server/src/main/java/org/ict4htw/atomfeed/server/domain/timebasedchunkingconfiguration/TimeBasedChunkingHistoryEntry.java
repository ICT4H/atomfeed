package org.ict4htw.atomfeed.server.domain.timebasedchunkingconfiguration;

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
        LocalDateTime effectiveEndTime = endTime == null ? LocalDateTime.now() : endTime;

        int minutesElapsed = Minutes.minutesBetween(startTime, effectiveEndTime).getMinutes();
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
}