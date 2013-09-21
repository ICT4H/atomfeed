package org.ict4h.atomfeed.server.domain.criterion;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EmptyCriterion implements Criterion{
    @Override
    public String asSqlString() {
        return "";
    }

    @Override
    public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException{
        return statement;
    }

    @Override
    public PreparedStatement prepareStatement(PreparedStatement statement, Integer limit, Integer offset) throws SQLException {
        statement.setInt(1, limit);
        statement.setInt(2, offset);
        return statement;
    }
}

