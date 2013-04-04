package org.ict4h.atomfeed.server.service.helper;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ResourceHelper {

    public static final String bundleName = "atomfeed";

    public String fetchKeyOrDefault(String key, String defaultValue){
        ResourceBundle bundle;
        try
        {
            bundle = ResourceBundle.getBundle(bundleName);
        }catch (MissingResourceException ex){
            return defaultValue;
        }
        if(bundle.containsKey(key)){
            return bundle.getString(key).toLowerCase();
        }
        return defaultValue;
    }
}
