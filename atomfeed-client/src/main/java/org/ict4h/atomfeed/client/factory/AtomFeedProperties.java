package org.ict4h.atomfeed.client.factory;

import org.apache.log4j.Logger;

public class AtomFeedProperties {
    private int readTimeout = 20000;
    private int connectTimeout = 10000;

    private static Logger logger = Logger.getLogger(AtomFeedProperties.class);

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
}
