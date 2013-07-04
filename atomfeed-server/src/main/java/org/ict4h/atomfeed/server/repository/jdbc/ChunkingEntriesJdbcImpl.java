package org.ict4h.atomfeed.server.repository.jdbc;

import org.ict4h.atomfeed.Configuration;
import org.ict4h.atomfeed.jdbc.JdbcConnectionProvider;
import org.ict4h.atomfeed.jdbc.JdbcResultSetMapper;
import org.ict4h.atomfeed.jdbc.JdbcUtils;
import org.ict4h.atomfeed.server.domain.chunking.ChunkingHistoryEntry;
import org.ict4h.atomfeed.server.exceptions.AtomFeedRuntimeException;
import org.ict4h.atomfeed.server.repository.ChunkingEntries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ChunkingEntriesJdbcImpl implements ChunkingEntries {

	private JdbcConnectionProvider provider;
	
	public ChunkingEntriesJdbcImpl(JdbcConnectionProvider provider) {
		this.provider = provider;
	}
	
	@Override
	public List<ChunkingHistoryEntry> all() {
		Connection connection;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			connection = getDbConnection();
			String sql = String.format("select id, chunk_length, start from %s order by id",
                    JdbcUtils.getTableName(Configuration.getInstance().getSchema(), "chunking_history"));
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
			throw new AtomFeedRuntimeException(e);
		}
	}

	private List<ChunkingHistoryEntry> mapHistories(ResultSet results) {
		JdbcResultSetMapper<ChunkingHistoryEntry> resultSetMapper = new JdbcResultSetMapper<ChunkingHistoryEntry>();
        return resultSetMapper.mapResultSetToObject(results, ChunkingHistoryEntry.class);
	}

	private Connection getDbConnection() throws SQLException {
		return provider.getConnection();
	}

}
