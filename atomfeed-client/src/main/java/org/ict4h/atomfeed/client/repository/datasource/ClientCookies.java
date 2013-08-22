package org.ict4h.atomfeed.client.repository.datasource;

import org.apache.commons.lang3.StringUtils;
import java.util.Map;

public class ClientCookies {
    private Map<String, String> cookieEntries;

    public ClientCookies(Map<String, String> cookieEntries) {
        this.cookieEntries = cookieEntries;
    }

    public String getHttpRequestPropertyValue() {
        if (cookieEntries != null && cookieEntries.size() > 0) {
            String[] cookieEntryAndValues = new String[cookieEntries.size()];
            int i = 0;
            for (String key : cookieEntries.keySet()) {
                cookieEntryAndValues[i] = String.format(" %s=%s ", key, cookieEntries.get(key));
                i++;
            }
            return StringUtils.join(cookieEntryAndValues, ";");
        }
        return null;
    }
}