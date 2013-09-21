package org.ict4h.atomfeed.server.domain.criterion;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface Criterion {
    String asSqlString();
    //TODO - Move the below to a wrapper Query Builder class that understands criterion. Limit offset shouldn't be here.

    @Deprecated()
    PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException;
    @Deprecated()
    PreparedStatement prepareStatement(PreparedStatement statement, Integer limit, Integer offset) throws SQLException;
}
