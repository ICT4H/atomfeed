package org.ict4h.atomfeed.client.repository.jdbc;

import org.apache.log4j.Logger;
import org.ict4h.atomfeed.Configuration;
import org.ict4h.atomfeed.client.domain.Event;
import org.ict4h.atomfeed.client.domain.FailedEvent;
import org.ict4h.atomfeed.client.exceptions.AtomFeedClientException;
import org.ict4h.atomfeed.client.repository.AllFailedEvents;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.ict4h.atomfeed.jdbc.JdbcUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AllFailedEventsJdbcImpl implements AllFailedEvents {
    private static Logger logger = Logger.getLogger(AllFailedEventsJdbcImpl.class);
    public static final String FAILED_EVENTS_TABLE = "failed_events";
    public static final int ERROR_MSG_MAX_LEN = 4000;

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
                    "select id, feed_uri, failed_at, error_message, event_id, event_content, title from %s where feed_uri = ? and event_id = ?",
                    JdbcUtils.getTableName(Configuration.getInstance().getSchema(), "failed_events"));
            statement = connection.prepareStatement(sql);
            statement.setString(1, feedUri);
            statement.setString(2, eventId);
            resultSet = statement.executeQuery();
            List<FailedEvent> failedEvents = mapFailedEventsFromResultSet(resultSet);
            if ((failedEvents != null) && !failedEvents.isEmpty()) {
                return failedEvents.get(0);
            }
            logger.info(String.format("Reading failed event - feedUri=%s, eventId=%s", feedUri, eventId));
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
            throw new AtomFeedClientException(e);
        }
    }

    private List<FailedEvent> mapFailedEventsFromResultSet(ResultSet resultSet) {
        List<FailedEvent> failedEvents = new ArrayList<>();
        try {
            while (resultSet.next()) {
                Event event = new Event(resultSet.getString(5), resultSet.getString(6), resultSet.getString(7));
                FailedEvent failedEvent = new FailedEvent(resultSet.getString(2), event,
                        resultSet.getString(4), resultSet.getTimestamp(3).getTime());
                failedEvents.add(failedEvent);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed while mapping failedEvents from database", e);
        }
        return failedEvents;
    }

    @Override
    public void addOrUpdate(FailedEvent failedEvent) {
        FailedEvent existingFailedEvent = get(failedEvent.getFeedUri(), failedEvent.getEventId());

        if (existingFailedEvent != null) {
            updateFailedEvent(failedEvent);
            return;
        }

        insertFailedEvent(failedEvent);
    }

    private void insertFailedEvent(FailedEvent failedEvent) {
        String sql = String.format(
                "insert into %s (feed_uri, failed_at, error_message, event_id, event_content, error_hash_code) values (?, ?, ?, ?, ?, ?)",
                JdbcUtils.getTableName(Configuration.getInstance().getSchema(), FAILED_EVENTS_TABLE));

        // DB limit is 4000. reduce to ensure it doesn't cross that.
        String errorMessage = failedEvent.getErrorMessage().length() > ERROR_MSG_MAX_LEN
                ? failedEvent.getErrorMessage().substring(0, ERROR_MSG_MAX_LEN) : failedEvent.getErrorMessage();

        Connection connection;
        PreparedStatement statement = null;
        try {
            connection = connectionProvider.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, failedEvent.getFeedUri());
            statement.setTimestamp(2, new Timestamp(failedEvent.getFailedAt()));
            statement.setString(3, errorMessage);
            statement.setString(4, failedEvent.getEventId());
            statement.setString(5, failedEvent.getEvent().getContent());
            statement.setInt(6, errorMessage.hashCode());
            statement.executeUpdate();
            logger.info(String.format("Created a new %s", failedEvent.toString()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeAll(statement, null);
        }
    }

    private void updateFailedEvent(FailedEvent failedEvent) {
        String sql = String.format(
                "update %s set failed_at = ?, error_message = ?, event_content = ?, error_hash_code = ? where feed_uri = ? and event_id = ?",
                JdbcUtils.getTableName(Configuration.getInstance().getSchema(), FAILED_EVENTS_TABLE));

        // DB limit is 4000. reduce to ensure it doesn't cross that.
        String errorMessage = failedEvent.getErrorMessage().length() > ERROR_MSG_MAX_LEN
                ? failedEvent.getErrorMessage().substring(0, ERROR_MSG_MAX_LEN) : failedEvent.getErrorMessage();

        Connection connection;
        PreparedStatement statement = null;
        try {
            connection = connectionProvider.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setTimestamp(1, new Timestamp(failedEvent.getFailedAt()));
            statement.setString(2, errorMessage);
            statement.setString(3, failedEvent.getEvent().getContent());
            statement.setLong(4, errorMessage.hashCode());
            statement.setString(5, failedEvent.getFeedUri());
            statement.setString(6, failedEvent.getEventId());
            statement.executeUpdate();
            logger.info(String.format("Updated %s", failedEvent.toString()));
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
                    "select id, feed_uri, failed_at, error_message, event_id, event_content, title from %s where feed_uri = ? order by id",
                    JdbcUtils.getTableName(Configuration.getInstance().getSchema(), FAILED_EVENTS_TABLE));
            statement = connection.prepareStatement(sql);
            statement.setString(1, feedUri);
            statement.setMaxRows(numberOfFailedEvents);
            resultSet = statement.executeQuery();

            List<FailedEvent> failedEvents = mapFailedEventsFromResultSet(resultSet);
            logger.info(String.format("Loaded %d failed events for %s", failedEvents.size(), feedUri));
            return failedEvents;
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

            int numberOfFailedEvents = resultSet.next() ? resultSet.getInt(1) : 0;
            logger.info(String.format("There are %d failed events for %s", numberOfFailedEvents, feedUri));
            return numberOfFailedEvents;
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
            logger.info(String.format("Deleted %s", failedEvent.toString()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeAll(statement, resultSet);
        }
    }

}
