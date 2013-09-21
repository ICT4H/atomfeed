package org.ict4h.atomfeed.server.domain.criterion;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface Criterion {
    String asSqlString();
    PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException;
}
