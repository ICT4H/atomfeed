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
}

