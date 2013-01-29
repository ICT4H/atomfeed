package org.ict4htw.atomfeed.server.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.domain.timebasedchunkingconfiguration.TimeRange;
import org.springframework.jdbc.datasource.DataSourceUtils;

public class AllEventRecordsJdbcImpl implements AllEventRecords {

	private DataSource dataSource;
	private String schema = "atomfeed";

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
			connection.setAutoCommit(false);
			String insertSql = String.format("insert into %s (uuid, title, uri, object) values (?, ?, ?, ?)", getTableName("event_records"));
			PreparedStatement stmt = connection.prepareStatement(insertSql);
			stmt.setString(1, eventRecord.getUuid());
			stmt.setString(2, eventRecord.getTitle());
			stmt.setString(3, eventRecord.getUri());
			stmt.setString(4, eventRecord.getObject());
			stmt.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			closeAll(connection, null, null);
		}
	}

	private Connection getDbConnection() throws SQLException {
		return DataSourceUtils.doGetConnection(dataSource);
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
		} finally {
			closeAll(connection, null, null);
		}
		return null;
	}
	
	private void closeAll(Connection con, PreparedStatement stmt, ResultSet rs) {
//		if (con != null) {
//			try {
//				con.close();
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
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
		} finally {
			closeAll(connection, null, null);
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
		} finally {
			closeAll(connection, null, null);
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
		JdbcResultSetMapper<EventRecord> resultSetMapper = new JdbcResultSetMapper<EventRecord>();
		List<EventRecord> events = resultSetMapper.mapResultSetToObject(results, EventRecord.class);
		return events;
	}
	
	

}
