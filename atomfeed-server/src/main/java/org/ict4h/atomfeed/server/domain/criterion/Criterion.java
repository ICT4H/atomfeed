package org.ict4h.atomfeed.server.domain.criterion;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public interface Criterion {
    String asSqlString();
    //All items to filter by a criteria are strings.The return type should change if this assumption changes.
    List<String> getValues();
}
