package org.ict4h.atomfeed.jdbc;

public class JdbcUtils {

    public static String getTableName(String schema, String table) {
        if ((schema != null) && (!"".equals(schema))) {
            return schema + "." + table;
        } else {
            return table;
        }
    }
}
