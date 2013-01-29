package org.ict4htw.atomfeed.server.repository;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.sql.DataSource;

import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.domain.timebasedchunkingconfiguration.TimeRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

@Repository(value = "allEventRecords")
public class AllEventRecordsJdbcImpl implements AllEventRecords {

	private DataSource dataSource;
	private String schema = "atomfeed";

	@Autowired
	public AllEventRecordsJdbcImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void setSchema(String dbSchema) {
		this.schema = dbSchema;
	}

	@Override
	public void add(EventRecord eventRecord) {
		Connection connection = null;
		try {
			connection = getDbConnection();
			String insertSql = String.format("insert into %s (uuid, title, uri, object) values (?, ?, ?, ?)", getTableName("event_records"));
			PreparedStatement stmt = connection.prepareStatement(insertSql);
			stmt.setString(1, eventRecord.getUuid());
			stmt.setString(2, eventRecord.getTitle());
			stmt.setString(3, eventRecord.getUri());
			stmt.setString(4, eventRecord.getObject());
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private Connection getDbConnection() throws SQLException {
		return DataSourceUtils.getConnection(dataSource);
		//return dataSource.getConnection();
	}

	@Override
	public EventRecord get(String uuid) {
		Connection connection = null;
		try {
			connection = getDbConnection();
			String sql = String.format("select id, uuid, title, timestamp, uri, object from %s where uuid = ?", getTableName("event_records"));
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setString(1, uuid);
			ResultSet results = stmt.executeQuery();
			List<EventRecord> events = mapEventRecords(results);
			if ((events != null) && !events.isEmpty()) {
				return events.get(0);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}	

	@Override
	public int getTotalCount() {
		Connection connection = null;
		try {
			connection = getDbConnection();
			PreparedStatement stmt = connection.prepareStatement(String.format("select count(*) from %s", getTableName("event_records")));
			ResultSet results = stmt.executeQuery();
			return results.next() ? results.getInt(1) : 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<EventRecord> getEventsFromRange(Integer first, Integer last) {
		Connection connection = null;
		try {
			connection = getDbConnection();
			String sql = String.format("select id, uuid, title, timestamp, uri, object from %s where id >= ? and id <= ?", getTableName("event_records"));
			PreparedStatement stmt = connection.prepareStatement(sql);
			stmt.setInt(1, first); 
			stmt.setInt(2, last);
			ResultSet results = stmt.executeQuery();
			return mapEventRecords(results);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<EventRecord> getEventsFromTimeRange(TimeRange timeRange) {
		return Collections.emptyList();
	}
	
	private String getTableName(String table) {
		if ((schema != null) && (!"".equals(schema))) {
			return schema + "." + table;
		} else {
			return table;
		}
	}

	private List<EventRecord> mapEventRecords(ResultSet results) {
		ResultSetMapper<EventRecord> resultSetMapper = new ResultSetMapper<EventRecord>();
		List<EventRecord> events = resultSetMapper.mapResultSetToObject(results, EventRecord.class);
		return events;
	}
	
	private class ResultSetMapper<T> {
		@SuppressWarnings("unchecked")
		public List<T> mapResultSetToObject(ResultSet rs, Class<T> outputClass) {
			List<T> outputList = null;
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
					return null;
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
			return outputList;
		}
	}

}
