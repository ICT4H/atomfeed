package org.ict4h.atomfeed.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AtomFeedProperties {
    private int readTimeout = 20000;
    private int connectTimeout = 10000;
    private boolean controlsEventProcessing = true;
    private int maxFailedEvents = 5;
    private int failedEventMaxRetry = 5;
    private int failedEventsBatchProcessSize = 5;

    private Logger logger = LogManager.getLogger(AtomFeedProperties.class);
    private boolean handleRedirection = false;

    public int getReadTimeout() {
        return readTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        if (readTimeout == 0)
            logger.warn("Setting readTimeout to zero. WebClient would wait infinitely to read");
        this.readTimeout = readTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        if (connectTimeout == 0)
            logger.warn("Setting connectTimeout to zero. WebClient would wait infinitely to connect.");
        this.connectTimeout = connectTimeout;
    }

    public void setControlsEventProcessing(boolean value) {
        this.controlsEventProcessing = value;
    }

    public boolean controlsEventProcessing() {
        return controlsEventProcessing;
    }

    public int getMaxFailedEvents() {
        return maxFailedEvents;
    }

    public void setMaxFailedEvents(int maxFailedEvents) {
        this.maxFailedEvents = maxFailedEvents;
    }

    public int getFailedEventMaxRetry() {
        return failedEventMaxRetry;
    }

    public void setFailedEventMaxRetry(int failedEventMaxRetry) {
        this.failedEventMaxRetry = failedEventMaxRetry;
    }

    public int getFailedEventsBatchProcessSize() {
        return failedEventsBatchProcessSize;
    }

    public void setFailedEventsBatchProcessSize(int failedEventsBatchProcessSize) {
        this.failedEventsBatchProcessSize = failedEventsBatchProcessSize;
    }

    public boolean isHandleRedirection() {
        return handleRedirection;
    }

    public void setHandleRedirection(boolean handleRedirection) {
        this.handleRedirection = handleRedirection;
    }
}
