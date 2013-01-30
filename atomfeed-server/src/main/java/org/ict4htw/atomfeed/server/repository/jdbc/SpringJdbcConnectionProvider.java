package org.ict4htw.atomfeed.server.repository.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

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
