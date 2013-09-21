package org.ict4h.atomfeed.server.repository.jdbc;

import org.ict4h.atomfeed.Configuration;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.ict4h.atomfeed.jdbc.JdbcResultSetMapper;
import org.ict4h.atomfeed.jdbc.JdbcUtils;
import org.ict4h.atomfeed.server.domain.EventRecord;
import org.ict4h.atomfeed.server.domain.chunking.time.TimeRange;
import org.ict4h.atomfeed.server.domain.criterion.Criterion;
import org.ict4h.atomfeed.server.exceptions.AtomFeedRuntimeException;
import org.ict4h.atomfeed.server.repository.AllEventRecords;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AllEventRecordsJdbcImpl implements AllEventRecords {

    private JdbcConnectionProvider provider;

    public AllEventRecordsJdbcImpl(JdbcConnectionProvider provider) {
        this.provider = provider;
    }

    @Override
    public void add(EventRecord eventRecord) {
        Connection connection;
        PreparedStatement stmt = null;
        try {
            connection = getDbConnection();
            String insertSql = String.format("insert into %s (uuid, title, uri, object,category) values (?, ?, ?, ?,?)",
                    JdbcUtils.getTableName(Configuration.getInstance().getSchema(), "event_records"));
            stmt = connection.prepareStatement(insertSql);
            stmt.setString(1, eventRecord.getUuid());
            stmt.setString(2, eventRecord.getTitle());
            stmt.setString(3, eventRecord.getUri());
            stmt.setString(4, eventRecord.getContents());
            stmt.setString(5, eventRecord.getCategory());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new AtomFeedRuntimeException(e);
        } finally {
            closeAll(stmt, null);
        }
    }

    private Connection getDbConnection() throws SQLException {
        return provider.getConnection();
    }

    @Override
    public EventRecord get(String uuid) {
        Connection connection;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            connection = getDbConnection();
            String sql = String.format("select id, uuid, title, timestamp, uri, object, category from %s where uuid = ?",
                    JdbcUtils.getTableName(Configuration.getInstance().getSchema(), "event_records"));
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, uuid);
            rs = stmt.executeQuery();
            List<EventRecord> events = mapEventRecords(rs);
            if ((events != null) && !events.isEmpty()) {
                return events.get(0);
            }
        } catch (SQLException e) {
            throw new AtomFeedRuntimeException(e);
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
            throw new AtomFeedRuntimeException(e);
        }
    }


    @Override
    public int getTotalCountForCategory(String category) {
        Connection connection;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            connection = getDbConnection();
            stmt = buildCountStatement(category, connection);
            rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new AtomFeedRuntimeException(e);
        } finally {
            closeAll(stmt, rs);
        }
    }

    @Override
    //TODO: Offset - Cannot be negative for initial feed with no entries. Fix this
    //TODO: Order By is required to ensure that the generated query plan is returns events in the same order all the time.
    public List<EventRecord> getEventsFromRangeForCategory(String category, Integer offset, Integer limit) {
        Connection connection;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            connection = getDbConnection();
            statement = buildSelectStatement(connection, category, offset, limit);
            ResultSet results = statement.executeQuery();
            return mapEventRecords(results);
        } catch (SQLException e) {
            throw new AtomFeedRuntimeException(e);
        } finally {
            closeAll(statement, rs);
        }
    }

    @Override
    public List<EventRecord> getEventsFromTimeRange(TimeRange timeRange, String category) {
        Connection connection;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getDbConnection();
            statement = buildSelectStatement(connection, timeRange, category);
            return mapEventRecords(statement.executeQuery());
        } catch (SQLException ex) {
            throw new AtomFeedRuntimeException(ex);
        } finally {
            closeAll(statement, resultSet);
        }
    }

    private PreparedStatement buildSelectStatement(Connection connection, TimeRange timeRange, String category) throws SQLException {
        String tableName = JdbcUtils.getTableName(Configuration.getInstance().getSchema(), "event_records");
        if (category == null) {
            String sql = String.format("select id, uuid, title, timestamp, uri, object from %s where timestamp BETWEEN ? AND ? order by timestamp asc",
                    tableName);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setTimestamp(1, timeRange.getStartTimestamp());
            statement.setTimestamp(2, timeRange.getEndTimestamp());
            return statement;
        } else {
            String sql = String.format("select id, uuid, title, timestamp, uri, object from %s where category = ? AND timestamp BETWEEN ? AND ? order by timestamp asc",
                    tableName);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, category);
            statement.setTimestamp(2, timeRange.getStartTimestamp());
            statement.setTimestamp(3, timeRange.getEndTimestamp());
            return statement;
        }
    }

    private PreparedStatement buildCountStatement(String category, Connection connection) throws SQLException {
        String tableName = JdbcUtils.getTableName(Configuration.getInstance().getSchema(), "event_records");
        if (category == null) {
            return connection.prepareStatement(String.format("select count(*) from %s", tableName));
        } else {
            PreparedStatement statement = connection.prepareStatement(String.format("select count(*) from %s where category = ?", tableName));
            statement.setString(1, category);
            return statement;
        }
    }

    private PreparedStatement buildSelectStatement(Connection connection, String category, Integer offset, Integer limit) throws SQLException {
        String schema = Configuration.getInstance().getSchema();
        String tableName = JdbcUtils.getTableName(schema, "event_records");
        if (category == null) {
            PreparedStatement statement = connection.prepareStatement(
                    String.format("select * from %s order by id asc limit ? offset ?", tableName));
            statement.setInt(1, limit);
            statement.setInt(2, offset);
            return statement;
        } else {
            PreparedStatement statement = connection.prepareStatement(
                    String.format("select * from %s where category = ? order by id asc limit ? offset ?",
                            tableName));
            statement.setString(1, category);
            statement.setInt(2, limit);
            statement.setInt(3, offset);
            return statement;
        }
    }

    private List<EventRecord> mapEventRecords(ResultSet results) {
        return new JdbcResultSetMapper<EventRecord>().mapResultSetToObject(results, EventRecord.class);
    }
}
