package org.ict4h.atomfeed.server.repository.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.ict4h.atomfeed.server.domain.chunking.ChunkingHistoryEntry;
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
	public List<ChunkingHistoryEntry> all() {
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			connection = getDbConnection();
			String sql = String.format("select id, interval, start from %s order by id", getTableName("chunking_history"));
			stmt = connection.prepareStatement(sql);
			rs = stmt.executeQuery();
			return mapHistories(rs);
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

	private List<ChunkingHistoryEntry> mapHistories(ResultSet results) {
		JdbcResultSetMapper<ChunkingHistoryEntry> resultSetMapper = new JdbcResultSetMapper<ChunkingHistoryEntry>();
        return resultSetMapper.mapResultSetToObject(results, ChunkingHistoryEntry.class);
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
