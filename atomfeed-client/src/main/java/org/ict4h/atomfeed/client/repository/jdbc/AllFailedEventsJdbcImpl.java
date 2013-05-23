package org.ict4h.atomfeed.client.repository.jdbc;

import org.ict4h.atomfeed.Configuration;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.domain.FailedEvent;
import org.ict4h.atomfeed.client.repository.AllFailedEvents;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.ict4h.atomfeed.jdbc.JdbcUtils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AllFailedEventsJdbcImpl implements AllFailedEvents {

    public static final String FAILED_EVENTS_TABLE = "failed_events";
    
    private JdbcConnectionProvider connectionProvider;

    public AllFailedEventsJdbcImpl(JdbcConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public FailedEvent get(String feedUri, String eventId) {
        Connection connection;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionProvider.getConnection();
            String sql = String.format(
                    "select id, feed_uri, failed_at, error_message, event_id, event_content from %s where feed_uri = ? and event_id = ?",
                    JdbcUtils.getTableName(Configuration.getInstance().getSchema(), "failed_events"));
            statement = connection.prepareStatement(sql);
            statement.setString(1, feedUri);
            statement.setString(2, eventId);
            resultSet = statement.executeQuery();
            List<FailedEvent> failedEvents = mapFailedEventsFromResultSet(resultSet);
            if ((failedEvents != null) && !failedEvents.isEmpty()) {
                return failedEvents.get(0);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeAll(statement, resultSet);
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

    private List<FailedEvent> mapFailedEventsFromResultSet(ResultSet resultSet) {
        List<FailedEvent> failedEvents = new ArrayList<>();
        try {
            while (resultSet.next()) {
                Event event = new Event(resultSet.getString(5), resultSet.getString(6));
                FailedEvent failedEvent = new FailedEvent(resultSet.getString(2), event,
                        resultSet.getString(4),resultSet.getTimestamp(3).getTime());
                failedEvents.add(failedEvent);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed while mapping failedEvents from database", e);
        }
        return failedEvents;
    }

    @Override
    public void put(FailedEvent failedEvent) {
        FailedEvent existingFailedEvent = get(failedEvent.getFeedUri(), failedEvent.getEventId());

        if (existingFailedEvent != null){
            updateFailedEvent(failedEvent);
            return;
        }

        insertFailedEvent(failedEvent);
    }

    private void insertFailedEvent(FailedEvent failedEvent) {
        String sql = String.format(
                "insert into %s (feed_uri, failed_at, error_message, event_id, event_content) values (?, ?, ?, ?, ?)",
                JdbcUtils.getTableName(Configuration.getInstance().getSchema(), FAILED_EVENTS_TABLE));

        Connection connection;
        PreparedStatement statement = null;
        try {
            connection = connectionProvider.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, failedEvent.getFeedUri());
            statement.setTimestamp(2, new Timestamp(failedEvent.getFailedAt()));
            statement.setString(3, failedEvent.getErrorMessage());
            statement.setString(4, failedEvent.getEventId());
            statement.setString(5, failedEvent.getEvent().getContent());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeAll(statement, null);
        }
    }

    private void updateFailedEvent(FailedEvent failedEvent) {
        String sql = String.format(
                "update %s set failed_at = ?, error_message = ?, event_content = ? where feed_uri = ? and event_id = ?",
                JdbcUtils.getTableName(Configuration.getInstance().getSchema(), FAILED_EVENTS_TABLE));

        Connection connection;
        PreparedStatement statement = null;
        try {
            connection = connectionProvider.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setTimestamp(1, new Timestamp(failedEvent.getFailedAt()));
            statement.setString(2, failedEvent.getErrorMessage());
            statement.setString(3, failedEvent.getEvent().getContent());
            statement.setString(4, failedEvent.getFeedUri());
            statement.setString(5, failedEvent.getEventId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeAll(statement, null);
        }
    }

    @Override
    public List<FailedEvent> getOldestNFailedEvents(String feedUri, int numberOfFailedEvents) {
        Connection connection;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionProvider.getConnection();
            String sql = String.format(
                    "select id, feed_uri, failed_at, error_message, event_id, event_content from %s where feed_uri = ? order by id",
                    JdbcUtils.getTableName(Configuration.getInstance().getSchema(), FAILED_EVENTS_TABLE));
            statement = connection.prepareStatement(sql);
            statement.setString(1, feedUri);
            statement.setMaxRows(numberOfFailedEvents);
            resultSet = statement.executeQuery();

            return mapFailedEventsFromResultSet(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeAll(statement, resultSet);
        }
    }

    @Override
    public int getNumberOfFailedEvents(String feedUri) {
        Connection connection;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionProvider.getConnection();
            statement = connection.prepareStatement(String.format("select count(*) from %s",
                    JdbcUtils.getTableName(Configuration.getInstance().getSchema(), FAILED_EVENTS_TABLE)));
            resultSet = statement.executeQuery();

            return resultSet.next() ? resultSet.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeAll(statement, resultSet);
        }
    }

    @Override
    public void remove(FailedEvent failedEvent) {
        Connection connection;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = connectionProvider.getConnection();
            statement = connection.prepareStatement(String.format("delete from %s where feed_uri = ? and event_id = ?",
                    JdbcUtils.getTableName(Configuration.getInstance().getSchema(), FAILED_EVENTS_TABLE)));
            statement.setString(1, failedEvent.getFeedUri());
            statement.setString(2, failedEvent.getEventId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeAll(statement, resultSet);
        }
    }

}
