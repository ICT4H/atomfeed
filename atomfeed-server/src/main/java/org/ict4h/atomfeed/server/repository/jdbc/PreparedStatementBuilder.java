package org.ict4h.atomfeed.server.repository.jdbc;

import org.ict4h.atomfeed.Configuration;
import org.ict4h.atomfeed.jdbc.JdbcUtils;
import org.ict4h.atomfeed.server.domain.criterion.Criterion;
import org.ict4h.atomfeed.server.domain.criterion.EmptyCriterion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

//usage - new PreparedStatementBuilder().count().withCriteria(criterion).build(connection);
public class PreparedStatementBuilder {
    private final String tableName;
    private Criterion criterion;
    private String rawSql;

    public PreparedStatementBuilder() {
        rawSql = "";
        tableName = JdbcUtils.getTableName(Configuration.getInstance().getSchema(), "event_records");
        criterion = new EmptyCriterion();
    }

    public PreparedStatementBuilder count() {
        this.rawSql = String.format("select count(*) from %s", tableName);
        return this;
    }

    public PreparedStatementBuilder withCriteria(Criterion criterion) {
        this.criterion = criterion;
        this.rawSql = new StringBuilder(rawSql).append(' ').append(criterion.asSqlString()).toString().trim();
        return this;
    }

    public PreparedStatement build(Connection connection) throws SQLException {
        return criterion.prepareStatement(connection.prepareStatement(this.rawSql));
    }

    public String getRawSql() {
        return rawSql;
    }
}
