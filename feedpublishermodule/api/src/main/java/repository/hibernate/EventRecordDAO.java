package repository.hibernate;

import org.apache.commons.lang.NotImplementedException;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.hibernate.jdbc.Work;
import org.ict4htw.atomfeed.server.domain.EventRecord;
import org.ict4htw.atomfeed.server.domain.timebasedchunkingconfiguration.TimeRange;
import org.ict4htw.atomfeed.server.repository.AllEventRecords;
import org.ict4htw.atomfeed.server.repository.jdbc.AllEventRecordsJdbcImpl;
import org.ict4htw.atomfeed.server.repository.jdbc.JdbcConnectionProvider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class EventRecordDAO implements AllEventRecords{
    private SessionFactory sessionFactory;

    public EventRecordDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void add(final EventRecord eventRecord) {
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.doWork(new Work() {
            @Override
            public void execute(final Connection connection) throws SQLException {
                AllEventRecordsJdbcImpl eventRecords = new AllEventRecordsJdbcImpl(new JdbcConnectionProvider() {
                    @Override
                    public Connection getConnection() throws SQLException {
                        connection.setAutoCommit(true);
                        return connection;
                    }
                });
                eventRecords.setSchema("");
                eventRecords.add(eventRecord);
            }
        });
    }

    @Override
    public EventRecord get(String uuid) {
        throw new NotImplementedException("use the other AllEventRecords implementation in atomfeed server instead.");
    }

    @Override
    public int getTotalCount() {
        throw new NotImplementedException("use the other AllEventRecords implementation in atomfeed server instead.");
    }

    @Override
    public List<EventRecord> getEventsFromRange(Integer first, Integer last) {
        throw new NotImplementedException("use the other AllEventRecords implementation in atomfeed server instead.");
    }

    @Override
    public List<EventRecord> getEventsFromTimeRange(TimeRange timeRange) {
        throw new NotImplementedException("use the other AllEventRecords implementation in atomfeed server instead.");
    }
}
