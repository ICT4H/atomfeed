package org.ict4h.atomfeed.server.repository.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.ict4h.atomfeed.server.repository.ChunkingEntries;

public class ChunkingEntriesJdbcImpl implements ChunkingEntries {

	
	private String schema = "atomfeed";

	private JdbcConnectionProvider provider;	
	
	public ChunkingEntriesJdbcImpl(JdbcConnectionProvider provider) {
		this.provider = provider;
	}
	
	public void setSchema(String dbSchema) {
		this.schema = dbSchema;
	}
	
	@Override
	public <T> List<T> all(Class<T> clazz) {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			connection = getDbConnection();
			String sql = String.format("select id, chunk_size, start_pos from %s order by id", getTableName("number_based_chunking_histories"));
			stmt = connection.prepareStatement(sql);
			rs = stmt.executeQuery();
			return mapHistories(rs, clazz);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			closeAll(stmt, rs);
		}
	}

	private void closeAll(PreparedStatement stmt, ResultSet rs) {
		try {
			if (rs != null) {
					rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		return provider.getConnection();
	}

}
