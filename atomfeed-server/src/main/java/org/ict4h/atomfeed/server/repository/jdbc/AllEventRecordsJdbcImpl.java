package org.ict4h.atomfeed.server.repository.jdbc;

import org.ict4h.atomfeed.Configuration;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.ict4h.atomfeed.jdbc.JdbcResultSetMapper;
import org.ict4h.atomfeed.jdbc.JdbcUtils;
import org.ict4h.atomfeed.server.domain.EventRecord;
import org.ict4h.atomfeed.server.domain.chunking.time.TimeRange;
import org.ict4h.atomfeed.server.exceptions.AtomFeedRuntimeException;
import org.ict4h.atomfeed.server.repository.AllEventRecords;
import org.joda.time.DateTime;

import java.sql.*;
import java.util.*;
import java.util.Date;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class AllEventRecordsJdbcImpl implements AllEventRecords {

    private JdbcConnectionProvider provider;

    public AllEventRecordsJdbcImpl(JdbcConnectionProvider provider) {
        this.provider = provider;
    }

    @Override
    public void add(EventRecord eventRecord) {
        Connection connection = null;
        PreparedStatement stmt = null;
        try {
            connection = provider.getConnection();
            String insertSql = String.format("insert into %s (uuid, title, uri, object,category, date_created) values (?, ?, ?, ?,?,?)",
                    JdbcUtils.getTableName(Configuration.getInstance().getSchema(), "event_records"));
            stmt = connection.prepareStatement(insertSql);
            stmt.setString(1, eventRecord.getUuid());
            stmt.setString(2, eventRecord.getTitle());
            stmt.setString(3, eventRecord.getUri());
            stmt.setString(4, eventRecord.getContents());
            stmt.setString(5, eventRecord.getCategory());
            stmt.setTimestamp(6, getSqlTimeStamp(eventRecord));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new AtomFeedRuntimeException(e);
        } finally {
            close(stmt);
        }
    }

    private Timestamp getSqlTimeStamp(EventRecord eventRecord) {
        java.util.Date timeStamp = eventRecord.getDateCreated();
        if (timeStamp == null) {
            timeStamp = new Date();
        }
        return new Timestamp(timeStamp.getTime());
    }

    @Override
    public EventRecord get(String uuid) {
        Connection connection;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            connection = provider.getConnection();
            String sql = String.format("select id, uuid, title, timestamp, uri, object, category, date_created from %s where uuid = ?",
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
        close(rs);
        close(stmt);
    }

    private void close(AutoCloseable rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (Exception e) {
            throw new AtomFeedRuntimeException(e);
        }
    }


    @Override
    public int getTotalCountForCategory(String category) {
        Connection connection;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            connection = provider.getConnection();
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
    public List<EventRecord> getEventsFromRangeForCategory(String category, Integer offset, Integer limit, Integer startId) {
        Connection connection;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            connection = provider.getConnection();
            statement = buildSelectStatement(connection, category, offset, limit, startId);
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
            connection = provider.getConnection();
            statement = buildSelectStatement(connection, timeRange, category);
            return mapEventRecords(statement.executeQuery());
        } catch (SQLException ex) {
            throw new AtomFeedRuntimeException(ex);
        } finally {
            closeAll(statement, resultSet);
        }
    }

    @Override
    public int getTotalCountForCategory(String category, Integer beyondIndex, Integer endIndex) {
        Connection connection;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList<Object> params = new ArrayList<>();
        String tableName = JdbcUtils.getTableName(Configuration.getInstance().getSchema(), "event_records");
        StringBuffer query = new StringBuffer(String.format("select count(id) from %s where 1=1", tableName));

        if (!isBlank(category)) {
            query.append(" and category = ?");
            params.add(category);
        }
        if (beyondIndex != null) {
            query.append(" and id > ? ");
            params.add(beyondIndex);
        }
        if ((endIndex != null) && (endIndex > 0)) {
            query.append(" and id <= ? ");
            params.add(endIndex);
        }
        try {
            connection = provider.getConnection();
            stmt = connection.prepareStatement(query.toString());
            for (int pIndex = 1; pIndex <= params.size(); pIndex++) {
                stmt.setObject(pIndex, params.get(pIndex - 1));
            }
            rs = stmt.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new AtomFeedRuntimeException(e);
        } finally {
            closeAll(stmt, rs);
        }
    }

    @Override
    public List<String> findCategories() {
        Connection connection;
        PreparedStatement statement = null;
        ResultSet results = null;
        List<String> categories = new ArrayList<>();
        try {
            connection = provider.getConnection();
            String tableName = JdbcUtils.getTableName(Configuration.getInstance().getSchema(), "event_records");
            statement = connection.prepareStatement("select distinct category from " + tableName);
            results = statement.executeQuery();
            while (results.next()) {
                categories.add(results.getString(1));
            }
            return categories;
        } catch (SQLException e) {
            throw new AtomFeedRuntimeException(e);
        } finally {
            closeAll(statement, results);
        }

    }

    private PreparedStatement buildSelectStatement(Connection connection, TimeRange timeRange, String category) throws SQLException {
        String tableName = JdbcUtils.getTableName(Configuration.getInstance().getSchema(), "event_records");
        if (isBlank(category)) {
            String sql = String.format("select id, uuid, title, timestamp, uri, object, date_created from %s where timestamp BETWEEN ? AND ? order by timestamp asc",
                    tableName);
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setTimestamp(1, timeRange.getStartTimestamp());
            statement.setTimestamp(2, timeRange.getEndTimestamp());
            return statement;
        } else {
            String sql = String.format("select id, uuid, title, timestamp, uri, object, date_created from %s where category = ? AND timestamp BETWEEN ? AND ? order by timestamp asc",
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
        if (isBlank(category)) {
            return connection.prepareStatement(String.format("select count(id) from %s", tableName));
        } else {
            PreparedStatement statement = connection.prepareStatement(String.format("select count(id) from %s where category = ?", tableName));
            statement.setString(1, category);
            return statement;
        }
    }

    private PreparedStatement buildSelectStatement(Connection connection, String category, Integer offset, Integer limit, Integer startId) throws SQLException {
        String schema = Configuration.getInstance().getSchema();
        String tableName = JdbcUtils.getTableName(schema, "event_records");
        if (isBlank(category)) {
            PreparedStatement statement = connection.prepareStatement(
                    String.format("select id, uuid, title, timestamp, uri, object, date_created from %s where id > ? order by id asc limit ? offset ?", tableName));
            statement.setInt(1, startId);
            statement.setInt(2, limit);
            statement.setInt(3, offset);
            return statement;
        } else {
            PreparedStatement statement = connection.prepareStatement(
                    String.format("select id, uuid, title, timestamp, uri, object, category, date_created from %s where id > ? and category = ? order by id asc limit ? offset ?",
                            tableName));
            statement.setInt(1, startId);
            statement.setString(2, category);
            statement.setInt(3, limit);
            statement.setInt(4, offset);
            return statement;
        }
    }

    private List<EventRecord> mapEventRecords(ResultSet results) {
        return new JdbcResultSetMapper<EventRecord>().mapResultSetToObject(results, EventRecord.class);
    }
}
