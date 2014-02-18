package org.ict4h.atomfeed.server.repository.jdbc;

import org.ict4h.atomfeed.Configuration;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.ict4h.atomfeed.jdbc.JdbcResultSetMapper;
import org.ict4h.atomfeed.jdbc.JdbcUtils;
import org.ict4h.atomfeed.server.domain.EventRecordsOffsetMarker;
import org.ict4h.atomfeed.server.exceptions.AtomFeedRuntimeException;
import org.ict4h.atomfeed.server.repository.AllEventRecordsOffsetMarkers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class AllEventRecordsOffsetMarkersJdbcImpl implements AllEventRecordsOffsetMarkers {

    public static final String EVENT_RECORDS_OFFSET_MARKER = "event_records_offset_marker";

    private JdbcConnectionProvider provider;

    public AllEventRecordsOffsetMarkersJdbcImpl(JdbcConnectionProvider provider) {
        this.provider = provider;
    }

    @Override
    public void addOrUpdate(String category, Integer offsetId, Integer countTillOffSetId) {
        Connection connection = null;
        PreparedStatement stmt = null;
        Integer offsetMarkerCountForCategory = getOffsetMarkerCountForCategory(category);
        boolean autoCommit = true;
        try {
            connection = provider.getConnection();
            autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            String tableName = JdbcUtils.getTableName(Configuration.getInstance().getSchema(), "event_records_offset_marker");
            String sqlString = String.format("insert into %s (event_id, event_count, category) values (?, ?, ?)", tableName);
            if (offsetMarkerCountForCategory > 0) {
                sqlString = String.format("update %s set event_id=?, event_count=? where category=?", tableName);
            }
            stmt = connection.prepareStatement(sqlString);
            stmt.setObject(1, offsetId);
            stmt.setObject(2, countTillOffSetId);
            stmt.setObject(3, category);
            stmt.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new AtomFeedRuntimeException(e);
        } finally {
            close(stmt);
            try {
                connection.setAutoCommit(autoCommit);
                provider.closeConnection(connection);
            } catch (SQLException e) {
                throw new AtomFeedRuntimeException(e);
            }
        }
    }

    @Override
    public List<EventRecordsOffsetMarker> getAll() {
        Connection connection;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            connection = provider.getConnection();
            String sql = String.format("select id, event_id, event_count, category from %s",
                    JdbcUtils.getTableName(Configuration.getInstance().getSchema(), EVENT_RECORDS_OFFSET_MARKER));
            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();
            return new JdbcResultSetMapper<EventRecordsOffsetMarker>().mapResultSetToObject(rs, EventRecordsOffsetMarker.class);
        } catch (SQLException e) {
            throw new AtomFeedRuntimeException(e);
        } finally {
            closeAll(stmt, rs);
        }
    }

    private Integer getOffsetMarkerCountForCategory(String category) {
        PreparedStatement statement = null;
        ResultSet rs = null;
        String tableName = JdbcUtils.getTableName(Configuration.getInstance().getSchema(), "event_records_offset_marker");
        try {
            Connection connection = provider.getConnection();
            if (isBlank(category)) {
                statement = connection.prepareStatement(String.format("select count(id) from %s where category = ''", tableName));
            } else {
                statement = connection.prepareStatement(String.format("select count(id) from %s where category = ?", tableName));
                statement.setString(1, category);
            }
            rs = statement.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new AtomFeedRuntimeException(e);
        } finally {
            closeAll(statement, rs);
        }
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

}
