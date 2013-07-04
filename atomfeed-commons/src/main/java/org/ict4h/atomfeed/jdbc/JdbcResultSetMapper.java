package org.ict4h.atomfeed.jdbc;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class JdbcResultSetMapper<T> {
    @SuppressWarnings("unchecked")
    public List<T> mapResultSetToObject(ResultSet rs, Class<T> outputClass) {
        List<T> outputList = new ArrayList<T>();
        try {
            if (rs != null) {
                if (outputClass.isAnnotationPresent(Entity.class)) {
                    ResultSetMetaData rsmd = rs.getMetaData();
                    Field[] fields = outputClass.getDeclaredFields();
                    while (rs.next()) {
                        T instance = (T) outputClass.newInstance();
                        for (int itr = 0; itr < rsmd.getColumnCount(); itr++) {
                            String columnName = rsmd.getColumnName(itr + 1);
                            Object columnValue = rs.getObject(itr + 1);
                            for (Field field : fields) {
                                if (field.isAnnotationPresent(Column.class)) {
                                    Column column = field.getAnnotation(Column.class);
                                    if (column.name().equalsIgnoreCase(columnName) && columnValue != null) {
                                        field.setAccessible(true);
                                        field.set(instance, columnValue);
                                        break;
                                    }
                                }
                            }
                        }
                        if (outputList == null) {
                            outputList = new ArrayList<T>();
                        }
                        outputList.add(instance);
                    }
                } else {
                    throw new RuntimeException("Can not map to a class not marked with javax.persistence.Entity annotation");
                }
            } else {
                return new ArrayList<T>();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return outputList;
    }
}
