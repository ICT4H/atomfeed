package org.ict4h.atomfeed;


import org.apache.commons.lang3.StringUtils;

import java.util.ResourceBundle;

public class Configuration {

    public static final String DEFAULT_PROPERTY_FILENAME = "atomfeed";

    private static Object lockObject = new Object();

    private static Configuration instance;

    private ResourceBundle resourceBundle;

    private Configuration(String propertyFilename) {
        this.resourceBundle = ResourceBundle.getBundle(propertyFilename);
    }

    public static Configuration getInstance(String propertyFilename) {
        synchronized (lockObject) {
            if (instance == null) instance = new Configuration(propertyFilename);
            return instance;
        }
    }

    public static Configuration getInstance() {
        return getInstance(DEFAULT_PROPERTY_FILENAME);
    }

    public String getJdbcUrl() {
        return resourceBundle.getString("jdbc.url");
    }

    public String getJdbcUsername() {
        return resourceBundle.getString("jdbc.username");
    }

    public String getJdbcPassword() {
        return resourceBundle.getString("jdbc.password");
    }

    public String getSchema() {
        return resourceBundle.getString("atomdb.default_schema");
    }

    public boolean getUpdateAtomFeedMarkerFlag() {
        if(StringUtils.isBlank(resourceBundle.getString("update.atomfeed.marker")))
            return true;
        return Boolean.parseBoolean(resourceBundle.getString("update.atomfeed.marker"));
    }
}
