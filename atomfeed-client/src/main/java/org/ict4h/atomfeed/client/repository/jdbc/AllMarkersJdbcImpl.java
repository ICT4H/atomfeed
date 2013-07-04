package org.ict4h.atomfeed.client.repository.jdbc;

import org.ict4h.atomfeed.Configuration;
import org.ict4h.atomfeed.client.domain.Marker;
import org.ict4h.atomfeed.client.exceptions.AtomFeedClientException;
import org.ict4h.atomfeed.client.repository.AllMarkers;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.ict4h.atomfeed.jdbc.JdbcUtils;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AllMarkersJdbcImpl implements AllMarkers {

    private JdbcConnectionProvider connectionProvider;

    public AllMarkersJdbcImpl(JdbcConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public Marker get(URI feedUri) {
        Connection connection;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        try {
            connection = connectionProvider.getConnection();
            String sql = String.format("select feed_uri, last_read_entry_id, feed_uri_for_last_read_entry from %s where feed_uri = ?",
                    JdbcUtils.getTableName(Configuration.getInstance().getSchema(), "markers"));
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, feedUri.toString());
            resultSet = stmt.executeQuery();
            List<Marker> markers = mapMarkersFromResultSet(resultSet);
            if ((markers != null) && !markers.isEmpty()) {
                return markers.get(0);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeAll(stmt, resultSet);
        }
        return null;
    }

    private List<Marker> mapMarkersFromResultSet(ResultSet resultSet) {
        List<Marker> markers = new ArrayList<>();
        try {
            while (resultSet.next()) {
                Marker marker = new Marker(new URI(resultSet.getString(1)), resultSet.getString(2), new URI(resultSet.getString(3)));
                markers.add(marker);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed while mapping markers from database", e);
        }
        return markers;
    }

    @Override
    public void put(URI feedUri, String entryId, URI entryFeedUri) {
        Marker existingMarker = get(feedUri);

        if (existingMarker != null) {
            updateMarker(feedUri, entryId, entryFeedUri);
            return;
        }

        insertMaker(feedUri, entryId, entryFeedUri);
    }

    private void updateMarker(URI feedUri, String entryId, URI entryFeedUri) {
        String sql = String.format(
                "update %s set last_read_entry_id = ?, feed_uri_for_last_read_entry = ? where feed_uri = ?",
                JdbcUtils.getTableName(Configuration.getInstance().getSchema(), "markers"));

        Connection connection;
        PreparedStatement statement = null;
        try {
            connection = connectionProvider.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, entryId);
            statement.setString(2, entryFeedUri.toString());
            statement.setString(3, feedUri.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeAll(statement, null);
        }
    }

    private void insertMaker(URI feedUri, String entryId, URI entryFeedUri) {
        String sql = String.format(
                "insert into %s (feed_uri, last_read_entry_id, feed_uri_for_last_read_entry) values (?, ?, ?)",
                JdbcUtils.getTableName(Configuration.getInstance().getSchema(), "markers"));

        Connection connection;
        PreparedStatement statement = null;
        try {
            connection = connectionProvider.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, feedUri.toString());
            statement.setString(2, entryId);
            statement.setString(3, entryFeedUri.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeAll(statement, null);
        }
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
            throw new AtomFeedClientException(e);
        }
    }

}
