package org.ict4h.atomfeed.server.domain.criterion;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class CategoryCriteria implements Criterion{
    private final String category;

    public CategoryCriteria(String category) {
        this.category = category;
    }

    @Override
    public String asSqlString() {
        return "where category = ?";
    }

    @Override
    public List<String> getValues() {
        return Arrays.asList(category);
    }
}
