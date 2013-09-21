package org.ict4h.atomfeed.server.repository.jdbc;

import org.ict4h.atomfeed.server.domain.criterion.CategoryTitleCriterion;
import org.ict4h.atomfeed.server.domain.criterion.Criterion;
import org.ict4h.atomfeed.server.domain.criterion.EmptyCriterion;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
}


