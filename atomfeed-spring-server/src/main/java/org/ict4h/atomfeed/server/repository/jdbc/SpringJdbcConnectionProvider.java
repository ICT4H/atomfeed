package org.ict4h.atomfeed.server.repository.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.springframework.jdbc.datasource.DataSourceUtils;

public class SpringJdbcConnectionProvider implements JdbcConnectionProvider {
	
	private DataSource dataSource;

	public SpringJdbcConnectionProvider(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return DataSourceUtils.doGetConnection(dataSource);
	}

}
