package org.ict4htw.atomfeed.server.repository.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public interface JdbcConnectionProvider {

	Connection getConnection() throws SQLException;

}
