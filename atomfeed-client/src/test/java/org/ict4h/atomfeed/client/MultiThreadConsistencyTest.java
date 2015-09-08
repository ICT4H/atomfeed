package org.ict4h.atomfeed.client;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.sun.syndication.feed.atom.Feed;
import org.ict4h.atomfeed.client.repository.AllFeeds;
import org.ict4h.atomfeed.client.repository.jdbc.AllFailedEventsJdbcImpl;
import org.ict4h.atomfeed.client.repository.jdbc.AllMarkersJdbcImpl;
import org.ict4h.atomfeed.client.service.AtomFeedClient;
import org.ict4h.atomfeed.client.service.EventWorker;
import org.ict4h.atomfeed.jdbc.AtomFeedJdbcTransactionManager;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.ict4h.atomfeed.server.repository.AllEventRecordsStub;
import org.ict4h.atomfeed.server.repository.InMemoryEventRecordCreator;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsJdbcImpl;
import org.ict4h.atomfeed.server.repository.jdbc.AllEventRecordsOffsetMarkersJdbcImpl;
import org.ict4h.atomfeed.server.repository.jdbc.ChunkingEntriesJdbcImpl;
import org.ict4h.atomfeed.server.repository.jdbc.ThreadLocalJdbcConnectionProvider;
import org.ict4h.atomfeed.server.service.Event;
import org.ict4h.atomfeed.server.service.EventFeedService;
import org.ict4h.atomfeed.server.service.EventFeedServiceImpl;
import org.ict4h.atomfeed.server.service.EventServiceImpl;
import org.ict4h.atomfeed.server.service.feedgenerator.NumberFeedGenerator;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class MultiThreadConsistencyTest {
    private EventFeedService eventFeedService;
    private AllEventRecordsStub allEventRecords;
    private InMemoryEventRecordCreator recordCreator;
    private String category;

    private final int NUMBER_OF_EVENT_CREATORS = 50;
    private static int totalEventsGenerated = 0;
    private static int totalEventsConsumed = 0;

    private static synchronized void addEventsConsumed(int number) {
        totalEventsConsumed += number;
    }

    private static synchronized void addEventsGenerated(int number) {
        totalEventsGenerated += number;
    }

    @Before
    public void setUp() throws SQLException {
        JdbcConnectionProvider connectionProvider = getConnectionProvider();
        Connection connection = connectionProvider.getConnection();
        PreparedStatement deleteEventRecords = connection.prepareStatement("DELETE FROM event_records;");
        deleteEventRecords.execute();
        deleteEventRecords.close();


        PreparedStatement deleteMarkers = connection.prepareStatement("DELETE FROM markers;");
        deleteMarkers.execute();
        deleteMarkers.close();

        connection.commit();
        connection.close();
    }

    @Test
    public void setupEventRecords() throws URISyntaxException, SQLException, InterruptedException {
        EventConsumer eventConsumer = new EventConsumer();
        Thread consumerThread = new Thread(eventConsumer, "EVENT_CONSUMER");
        consumerThread.setPriority(Thread.MAX_PRIORITY);
        consumerThread.start();

        ArrayList<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < NUMBER_OF_EVENT_CREATORS; i++) {
            Thread thread = new Thread(new EventCreator(), "EVENT_CREATOR_" + i);
            thread.start();
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.join();
        }

        eventConsumer.stopRunning();
        consumerThread.join();
        System.out.println("Total events consumed initially = " + totalEventsConsumed);


        EventConsumer lastConsumer = new EventConsumer();
        Thread lastConsumerThread = new Thread(lastConsumer, "LAST_EVENT_CONSUMER");
        lastConsumerThread.start();
        lastConsumer.stopRunning();
        lastConsumerThread.join();

        System.out.println("Total events generated = " + totalEventsGenerated);
        System.out.println("Total events consumed = " + totalEventsConsumed);
        assertEquals(totalEventsGenerated, totalEventsConsumed);
    }

    private static class EventConsumer implements Runnable {
        private boolean stopRunning;

        public void stopRunning() {
            stopRunning = true;
        }

        @Override
        public void run() {
            JdbcConnectionProvider connectionProvider = getConnectionProvider();

            do {
                try {
                    Thread.sleep(10);
                    AtomFeedClient atomFeedClient = getAtomFeedClient(connectionProvider);
                    atomFeedClient.processEvents();
                } catch (InterruptedException doNothing) {
                }
            } while (!stopRunning);
        }

        private AtomFeedClient getAtomFeedClient(JdbcConnectionProvider connectionProvider) {
            return new AtomFeedClient(new AllFeedsStub(), new AllMarkersJdbcImpl(connectionProvider),
                    new AllFailedEventsJdbcImpl(connectionProvider)
                    , new AtomFeedProperties(), new AtomFeedJdbcTransactionManager(connectionProvider)
                    , URI.create("http://uri/feed/"), new EventWorker() {
                @Override
                public void process(org.ict4h.atomfeed.client.domain.Event event) {
                    System.out.println("Processed " + event.getContent());
                    addEventsConsumed(1);
                }

                @Override
                public void cleanUp(org.ict4h.atomfeed.client.domain.Event event) {

                }
            });
        }
    }

    public static class AllFeedsStub extends AllFeeds {

        private final EventFeedServiceImpl eventService;

        public AllFeedsStub() {
            JdbcConnectionProvider connectionProvider = getConnectionProvider();
            eventService = new EventFeedServiceImpl(new NumberFeedGenerator(new AllEventRecordsJdbcImpl(connectionProvider),
                    new AllEventRecordsOffsetMarkersJdbcImpl(connectionProvider), new ChunkingEntriesJdbcImpl(connectionProvider)));
        }

        @Override
        public Feed getFor(URI uri) {
            int pageNumber = 0;
            try {
                pageNumber = Integer.valueOf(uri.getPath().substring(uri.getPath().lastIndexOf("/") + 1));
            } catch (NumberFormatException e) {
                pageNumber = 1;
            }
            Feed feed = eventService.getEventFeed(uri, "category", Integer.valueOf(pageNumber));
            return feed;
        }
    }

    private static class EventCreator implements Runnable {
        @Override
        public void run() {
            int totalInserted = 0;
            for (int i = 0; i < 100; i++) {
                try {
                    createEvent();
                    Thread.sleep(new Random().nextInt(5));
                    totalInserted++;
                } catch (URISyntaxException | SQLException e) {
                    addEventsGenerated(totalInserted);
                    throw new RuntimeException(e);
                } catch (InterruptedException ignore) {
                    System.out.println("interrupted");
                }
            }
            addEventsGenerated(totalInserted);
        }

        private void createEvent() throws URISyntaxException, SQLException {
            JdbcConnectionProvider provider = getConnectionProvider();
            EventServiceImpl eventService = new EventServiceImpl(new AllEventRecordsJdbcImpl(provider));
            String eventContent = Thread.currentThread().getName() + "_" + System.nanoTime();
            eventService.notify(new Event(UUID.randomUUID().toString(), "title",
                    DateTime.now(), "http://uri/feed/", eventContent, "category"));
            System.out.println("Created " + eventContent);

            provider.getConnection().commit();
            provider.getConnection().close();
        }
    }

    private static JdbcConnectionProvider getConnectionProvider() {
        MysqlConnectionPoolDataSource dataSource = new MysqlConnectionPoolDataSource();
        dataSource.setURL("jdbc:mysql://localhost:3306/atomfeed");
        dataSource.setUser("root");
        return new ThreadLocalJdbcConnectionProvider(dataSource);
    }
}
