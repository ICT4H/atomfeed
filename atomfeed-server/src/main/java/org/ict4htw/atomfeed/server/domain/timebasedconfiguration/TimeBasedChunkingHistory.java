package org.ict4htw.atomfeed.server.domain.timebasedconfiguration;

import org.ict4htw.atomfeed.server.exceptions.AtomFeedRuntimeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimeBasedChunkingHistory {
    private List<TimeBasedConfigurationItem> configurationItems;

    public TimeBasedChunkingHistory(TimeBasedConfigurationItem... configurationItems) {
        this.configurationItems = new ArrayList<TimeBasedConfigurationItem>();
        Collections.addAll(this.configurationItems, configurationItems);
    }

    public long currentSequenceNumber() {
        int totalNumberOfFeeds = 0;
        for (int i = 0; i < configurationItems.size(); i++) {
            totalNumberOfFeeds += configurationItems.get(i).numberOfFeeds();
        }

        return totalNumberOfFeeds + 1;
    }

    public TimeRange timeRangeFor(int sequenceNumber) {
        int totalNumberOfFeeds = 0;
        for (TimeBasedConfigurationItem timeBasedConfigurationItem : configurationItems) {
            int numberOfFeedsInCurrentConfigurationItem = timeBasedConfigurationItem.numberOfFeeds();
            if (sequenceNumber <= numberOfFeedsInCurrentConfigurationItem + totalNumberOfFeeds) return timeBasedConfigurationItem.getTimeRangeForChunk(sequenceNumber - totalNumberOfFeeds);
            totalNumberOfFeeds += numberOfFeedsInCurrentConfigurationItem;
        }
        throw new AtomFeedRuntimeException(String.format("The sequence number:%d lies in future", sequenceNumber));
    }
}