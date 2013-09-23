package org.ict4h.atomfeed.server.repository.jdbc;

import org.ict4h.atomfeed.Configuration;
import org.ict4h.atomfeed.jdbc.JdbcUtils;
import org.ict4h.atomfeed.server.domain.criterion.Criterion;
import org.ict4h.atomfeed.server.domain.criterion.EmptyCriterion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

//usage - PreparedStatementBuilder().count.withCriteria(criterion).build(connection);
//usage - PreparedStatementBuilder().select.withCriteria(criterion).orderById().withLimitAndOffset(l,o).build(connection);
public class PreparedStatementBuilder {
    private enum QueryType{
        SELECT,COUNT
    }

    private static String tableName = JdbcUtils.getTableName(Configuration.getInstance().getSchema(), "event_records");
    private Criterion criterion;
    private String rawSql;
    private Integer limit;
    private Integer offset;
    private QueryType queryType;

    private PreparedStatementBuilder(QueryType queryType, String rawSql) {
        this.rawSql = rawSql;
        this.queryType = queryType;
        criterion = new EmptyCriterion();
    }

    public static PreparedStatementBuilder count() {
        return new PreparedStatementBuilder(QueryType.COUNT,String.format("select count(*) from %s", tableName));
    }

    public static PreparedStatementBuilder select() {
        return new PreparedStatementBuilder(QueryType.SELECT,String.format("select * from %s", tableName));
    }

    public PreparedStatementBuilder withLimitAndOffset(Integer limit, Integer offset){
        this.limit = limit;
        this.offset = offset;
        this.rawSql = new StringBuilder(this.rawSql).append(' ').append("limit ? offset ?").toString();
        return this;
    }

    public PreparedStatementBuilder orderById(){
        this.rawSql = new StringBuilder(this.rawSql).append(' ').append("order by id asc").toString();
        return this;
    }

    public PreparedStatementBuilder withCriteria(Criterion criterion) {
        this.criterion = criterion;
        this.rawSql = new StringBuilder(rawSql).append(' ').append(criterion.asSqlString()).toString().trim();
        return this;
    }

    public PreparedStatement build(Connection connection) throws SQLException {
        int index = 1;
        PreparedStatement statement = connection.prepareStatement(this.rawSql);
        for(String value : criterion.getValues()){
            statement.setString(index, value);
            index++;
        }
        if(this.queryType.equals(QueryType.SELECT)){
            statement.setInt(index, this.limit);
            statement.setInt(index + 1, this.offset);
        }
        return statement;
    }

    public String getRawSql() {
        return rawSql;
    }
}
