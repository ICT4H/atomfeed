package org.ict4h.atomfeed.server.domain.criterion;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CategoryTitleCriterion implements Criterion {
    private final String category;
    private final String title;

    public CategoryTitleCriterion(String category, String title) {
        this.category = category;
        this.title = title;
    }

    @Override
    public String asSqlString() {
        return "where category = ? and title = ?";
    }

    @Override
    public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
        statement.setString(1, category);
        statement.setString(2, title);
        return statement;
    }

    @Override
    public PreparedStatement prepareStatement(PreparedStatement statement, Integer limit, Integer offset) throws SQLException {
        statement.setInt(3, limit);
        statement.setInt(4, offset);
        return  statement;
    }
}
