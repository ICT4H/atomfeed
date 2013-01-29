package org.ict4htw.atomfeed.server.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;

public class AllChunkingEntriesJdbcImpl implements ChunkingHistories {

	private DataSource dataSource;
	
	private String schema = "atomfeed";	
	
	public AllChunkingEntriesJdbcImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void setSchema(String dbSchema) {
		this.schema = dbSchema;
	}
	
	@Override
	public <T> List<T> all(Class<T> clazz) {
		Connection connection = null;
		try {
			connection = getDbConnection();
			String sql = String.format("select id, chunk_size, start_pos, end_pos from %s order by id", getTableName("chunking_history"));
			PreparedStatement stmt = connection.prepareStatement(sql);
			ResultSet results = stmt.executeQuery();
			return mapHistories(results, clazz);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			closeAll(connection, null, null);
		}
	}

	private void closeAll(Connection con, PreparedStatement stmt, ResultSet rs) {
		if (stmt != null) {
			try {
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private <T> List<T> mapHistories(ResultSet results, Class<T> clazz) {
		JdbcResultSetMapper<T> resultSetMapper = new JdbcResultSetMapper<T>();
		List<T> events = resultSetMapper.mapResultSetToObject(results, clazz);
		return events;
	}

	private Object getTableName(String table) {
		if ((schema != null) && (!"".equals(schema))) {
			return schema + "." + table;
		} else {
			return table;
		}
	}

	private Connection getDbConnection() throws SQLException {
		return DataSourceUtils.doGetConnection(dataSource);
	}

}
