package org.ict4h.atomfeed.server.repository.jdbc;

import org.ict4h.atomfeed.server.domain.criterion.CategoryTitleCriterion;
import org.ict4h.atomfeed.server.domain.criterion.Criterion;
import org.ict4h.atomfeed.server.domain.criterion.EmptyCriterion;
import org.ict4h.atomfeed.server.domain.criterion.TitleCriteria;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class PreparedStatementBuilderTest {
    private static String fQTN =  "atomfeed.event_records";
    private PreparedStatementBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = new PreparedStatementBuilder();
    }

    @Test
    public void shouldBuildCountQueryWithNoCriterion() throws Exception {
        final String rawSql = builder.count().withCriteria(new EmptyCriterion()).getRawSql();
        assertEquals(String.format("select count(*) from %s", fQTN), rawSql);
    }

    @Test
    public void shouldBuildCountQueryWithCategoryTitleCriterion() throws Exception {
        final Criterion criterion = new CategoryTitleCriterion("category", "title");
        final String rawSql = builder.count().withCriteria(criterion).getRawSql();
        assertEquals(String.format("select count(*) from %s where category = ? and title = ?", fQTN), rawSql);
    }

    @Test
    public void shouldBuildCountPreparedStatementWithNoCriterion() throws Exception {
        String expectedRawSql = String.format("select count(*) from %s", fQTN);
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(expectedRawSql)).thenReturn(statement);
        builder.count().withCriteria(new EmptyCriterion()).build(connection);
        verify(statement,never()).setString(anyInt(),anyString());
    }

    @Test
    public void shouldBuildCountPreparedStatementWithCriterion() throws Exception {
        String expected = String.format("select count(*) from %s where title = ?", fQTN);
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(expected)).thenReturn(statement);
        builder.count().withCriteria(new TitleCriteria("title")).build(connection);
        verify(statement).setString(1,"title");
    }

    @Test
    public void shouldBuildSelectQueryWithNoCriterion() throws Exception {
        final String rawSql = builder.select().withCriteria(new EmptyCriterion()).getRawSql();
        assertEquals(String.format("select * from %s", fQTN), rawSql);
    }

    @Test
    public void shouldBuildSelectQueryWithOrderAndNoCriterion() throws Exception {
        final String rawSql = builder.select().withCriteria(new EmptyCriterion()).orderById().getRawSql();
        assertEquals(String.format("select * from %s order by id asc", fQTN), rawSql);
    }

    @Test
    public void shouldBuildSelectQueryWithLimitAndOffsetAndNoCriterion() throws Exception {
        final String rawSql = builder.select().withCriteria(new EmptyCriterion()).withLimitAndOffset(1,1).getRawSql();
        assertEquals(String.format("select * from %s limit ? offset ?", fQTN), rawSql);
    }

}


