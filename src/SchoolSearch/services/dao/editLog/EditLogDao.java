package SchoolSearch.services.dao.editLog;

/**
 * @author GCR
 */
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import daogenerator.utils.SqlConstructUtil;

import SchoolSearch.services.dao.editLog.model.EditLog;
import SchoolSearch.services.utils.db.DBConnection;
import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.utils.db.ConnectionPool;

public class EditLogDao {
	String dbName = ConsistanceService.get("db.defaultDatabase");
	static ConnectionPool pool = ConnectionPool.getInstance();
	
	private static EditLogDao instance;
	
	public static EditLogDao getInstance(){
		if(null == instance){
			instance = new EditLogDao();
		}
		return instance;
	}
	
	public static EditLogDao getIndependentInstance(String dbName) {
		return new EditLogDao(dbName);
	}

	public EditLogDao() {
	}

	private EditLogDao(String dbName) {
		this.dbName = dbName;
	}
	
	public void insert(EditLog log) {
		String sql = "INSERT INTO " + dbName + ".editLog (uid, `table`, column_id,  `field`, old_value, new_value, ip, state) "//
							+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			_construct(ps, log);
//			ps.executeUpdate();
			SqlConstructUtil._executeUpdate(ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}

	public void insertBatch(List<EditLog> logList) {
		String sql = "INSERT INTO " + dbName + ".editLog (uid, `table`, column_id,  `field`, old_value, new_value, ip, state) "//
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for (EditLog log : logList) {
				_construct(ps, log);
				ps.addBatch();
			}
//			ps.executeBatch();
			SqlConstructUtil._executeBatch(ps);
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}
	
	public List<EditLog> walk(int start, int limit) {
		List<EditLog> result = new ArrayList<EditLog>();

		String sql = "SELECT id, uid, `table`, column_id, field, old_value, new_value, ip, state, `time` FROM "
				+ dbName + ".editLog WHERE id >= ? ORDER BY id LIMIT ?";
				
		
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, start);
			ps.setInt(2, limit);
			System.out.println(ps.toString());
//			rs = ps.executeQuery();
			rs = SqlConstructUtil._executeQuery(ps);
			while (rs.next()) {
				result.add(_construct(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		return result;
	}
	
	public List<EditLog> walkAll() {
		List<EditLog> result = new ArrayList<EditLog>();
		int startid = 0;
		int limit = 500;
		while (true) {
			List<EditLog> walk = walk(startid, limit);
			if (null == walk || walk.size() == 0)
				break;
			result.addAll(walk);
			startid = walk.get(walk.size() - 1).id + 1;
		}
		return result;
	}
	
	public void rollback(Integer id) {
		String sql = "UPDATE " + dbName + ".editLog SET state = 1 WHERE id = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
//			ps.executeUpdate();
			SqlConstructUtil._executeUpdate(ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}
	
	public List<EditLog> getLogsByTableName(String tableName) {
		
		List<EditLog> result = new ArrayList<EditLog>();

		String sql = "SELECT id, uid, `table`, column_id, field, old_value, new_value, ip, state, `time` FROM "
				+ dbName + ".editLog WHERE `table` = ?";
				
		
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, tableName);
			System.out.println(ps.toString());
//			rs = ps.executeQuery();
			rs = SqlConstructUtil._executeQuery(ps);
			while (rs.next()) {
				result.add(_construct(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		return result;
	}
	
	private void _construct(PreparedStatement ps, EditLog log) throws SQLException {
		int count = 1;
		__safeSetInt(ps, count++, log.uid);
		__safeSetString(ps, count++, log.table);
		__safeSetInt(ps, count++, log.column_id);
		__safeSetString(ps, count++, log.field);
		__safeSetString(ps, count++, log.old_value);
		__safeSetString(ps, count++, log.new_value);
		__safeSetString(ps, count++, log.ip);
		__safeSetInt(ps, count++, log.state);
	}
	
	private EditLog _construct(ResultSet rs) throws SQLException {
		EditLog result = new EditLog();
		result.id = rs.getInt("id");
		result.uid = rs.getInt("uid");
		result.table = rs.getString("table");
		result.column_id = rs.getInt("column_id");
		result.field = rs.getString("field");
		result.old_value = rs.getString("old_value");
		result.new_value = rs.getString("new_value");
		result.ip = rs.getString("ip");
		result.state = rs.getInt("state");
		result.time = rs.getTimestamp("time");
		return result;
	}
	
	private void __safeSetString(PreparedStatement ps, int parameterIndex, String value) throws SQLException {
		if (null == value)
			ps.setNull(parameterIndex, Types.VARCHAR);
		else
			ps.setString(parameterIndex, value);
	}
	
	private void __safeSetInt(PreparedStatement ps, int parameterIndex, Integer value) throws SQLException {
		if (value == null)
			ps.setNull(parameterIndex, Types.INTEGER);
		else
			ps.setInt(parameterIndex, value);
	}
	
	public static void main(String args[]) {
		EditLogDao e = new EditLogDao();
		System.out.println(e.getLogsByTableName("person_ext").size());
	}
}
