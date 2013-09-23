package org.ict4h.atomfeed.server.domain.criterion;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

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
    public List<String> getValues() {
        return Arrays.asList(category,title);
    }
}
