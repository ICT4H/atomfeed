package org.ict4h.atomfeed.server.domain.criterion;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmptyCriterion implements Criterion{
    @Override
    public String asSqlString() {
        return "";
    }

    @Override
    public List<String> getValues() {
        return new ArrayList();
    }
}

