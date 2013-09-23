package org.ict4h.atomfeed.server.domain.criterion;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

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
    public List<String> getValues() {
        return Arrays.asList(title);
    }
}
