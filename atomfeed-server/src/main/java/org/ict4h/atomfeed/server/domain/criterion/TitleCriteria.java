package org.ict4h.atomfeed.server.domain.criterion;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TitleCriteria implements Criterion{
    private final String title;

    public TitleCriteria(String title) {
        this.title = title;
    }

    @Override
    public String asSqlString() {
        return "where title = ?";
    }

    @Override
    public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
        statement.setString(1,title);
        return statement;
    }
}
