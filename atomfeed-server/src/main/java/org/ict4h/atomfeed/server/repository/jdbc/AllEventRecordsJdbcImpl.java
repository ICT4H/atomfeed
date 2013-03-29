package org.ict4h.atomfeed.server.repository.jdbc;

import java.sql.*;
import java.util.List;

import org.ict4h.atomfeed.server.domain.EventRecord;
import org.ict4h.atomfeed.server.domain.chunking.time.TimeRange;
import org.ict4h.atomfeed.server.repository.AllEventRecords;

public class AllEventRecordsJdbcImpl implements AllEventRecords {

	
	private String schema = "atomfeed";
	private JdbcConnectionProvider provider;

	public AllEventRecordsJdbcImpl(JdbcConnectionProvider provider) {
		this.provider = provider;
	}
	
	public void setSchema(String dbSchema) {
		this.schema = dbSchema;
	}

	@Override
	public void add(EventRecord eventRecord) {
		Connection connection = null;
		PreparedStatement stmt = null;
		try {
			connection = getDbConnection();
            connection.setAutoCommit(false);
			String insertSql = String.format("insert into %s (uuid, title, uri, object) values (?, ?, ?, ?)", getTableName("event_records"));
			stmt = connection.prepareStatement(insertSql);
			stmt.setString(1, eventRecord.getUuid());
			stmt.setString(2, eventRecord.getTitle());
			stmt.setString(3, eventRecord.getUri());
			stmt.setString(4, eventRecord.getContents());
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			closeAll(stmt, null);
		}
	}

	private Connection getDbConnection() throws SQLException {
        return provider.getConnection();
	}

	@Override
	public EventRecord get(String uuid) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			connection = getDbConnection();
			String sql = String.format("select id, uuid, title, timestamp, uri, object from %s where uuid = ?", getTableName("event_records"));
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, uuid);
			rs = stmt.executeQuery();
			List<EventRecord> events = mapEventRecords(rs);
			if ((events != null) && !events.isEmpty()) {
				return events.get(0);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			closeAll(stmt, rs);
		}
		return null;
	}
	
	private void closeAll(PreparedStatement stmt, ResultSet rs) {
		try {
			if (rs != null) {
					rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	@Override
	public int getTotalCount() {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			connection = getDbConnection();
			stmt = connection.prepareStatement(String.format("select count(*) from %s", getTableName("event_records")));
			rs = stmt.executeQuery();
			return rs.next() ? rs.getInt(1) : 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			closeAll(stmt, rs);
		}
	}

	@Override
	public List<EventRecord> getEventsFromRange(Integer first, Integer last) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			connection = getDbConnection();
			String sql = String.format("select id, uuid, title, timestamp, uri, object from %s where id >= ? and id <= ?", getTableName("event_records"));
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, first); 
			stmt.setInt(2, last);
			ResultSet results = stmt.executeQuery();
			return mapEventRecords(results);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			closeAll(stmt, rs);
		}
	}

    @Override
    public List<EventRecord> getEventsFromTimeRange(TimeRange timeRange) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try
        {
            connection = getDbConnection();
            String sql = String.format("select id, uuid, title, timestamp, uri, object from %s where timestamp BETWEEN ? AND ?",getTableName("event_records"));
            statement = connection.prepareStatement(sql);
            statement.setTimestamp(1, timeRange.getStartTimestamp());
            statement.setTimestamp(2, timeRange.getEndTimestamp());
            ResultSet results = statement.executeQuery();
            List<EventRecord> eventRecords = mapEventRecords(results);
            return eventRecords;
        }
        catch (SQLException ex){
            throw new RuntimeException(ex);
        }
        finally {
            closeAll(statement,resultSet);
        }
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
