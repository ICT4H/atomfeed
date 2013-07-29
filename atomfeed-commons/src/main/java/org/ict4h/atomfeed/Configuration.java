package org.ict4h.atomfeed;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
    private static final String DEFAULT_PROPERTY_FILENAME = "atomfeed.properties";

    private static final Object lockObject = new Object();

    private static Configuration instance;
    private Properties properties;

    private void init(String propertyFilename) throws IOException {
        InputStream propertiesFileStream = null;
        try {
            propertiesFileStream = getClass().getClassLoader().getResourceAsStream(propertyFilename);
            this.properties = new Properties();
            properties.load(propertiesFileStream);
        } finally {
            if (propertiesFileStream != null) propertiesFileStream.close();
        }
    }

    public static Configuration getInstance(String propertyFilename) {
        try {
            if (instance == null) {
                synchronized (lockObject) {
                    if (instance == null) {
                        instance = new Configuration();
                        instance.init(propertyFilename);
                    }
                }
            }
            return instance;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Configuration getInstance() {
        return getInstance(DEFAULT_PROPERTY_FILENAME);
    }

    public String getJdbcUrl() {
        return properties.getProperty("jdbc.url");
    }

    public String getJdbcUsername() {
        return properties.getProperty("jdbc.username");
    }

    public String getJdbcPassword() {
        return properties.getProperty("jdbc.password");
    }

    public String getSchema() {
        return properties.getProperty("atomdb.default_schema");
    }
}
