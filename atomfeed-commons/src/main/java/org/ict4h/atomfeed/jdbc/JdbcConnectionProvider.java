package org.ict4h.atomfeed.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public interface JdbcConnectionProvider {

	Connection getConnection() throws SQLException;

}
