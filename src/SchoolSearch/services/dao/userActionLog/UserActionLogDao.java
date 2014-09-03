package SchoolSearch.services.dao.userActionLog;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import daogenerator.utils.SqlConstructUtil;

import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.dao.userActionLog.model.UserActionLog;
import SchoolSearch.services.utils.db.ConnectionPool;
import SchoolSearch.services.utils.db.DBConnection;

/**
 * 
 * @author guanchengran
 *
 */
public class UserActionLogDao {
	String dbName = ConsistanceService.get("db.defaultDatabase");
	static ConnectionPool pool = ConnectionPool.getInstance();
	
	private static UserActionLogDao instance;
	
	public static UserActionLogDao getInstance(){
		if(null == instance){
			instance = new UserActionLogDao();
		}
		return instance;
	}
	
	public static UserActionLogDao getIndependentInstance(String dbName) {
		return new UserActionLogDao(dbName);
	}

	public UserActionLogDao() {
	}

	private UserActionLogDao(String dbName) {
		this.dbName = dbName;
	}
	
	public void insert(UserActionLog log) {
		String sql = "INSERT INTO " + dbName
				+ ".useractionlog (uid, userName, pageName, info, ip, type) "//
				+ "VALUES (?, ?, ?, ?, ?, ?)";
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

	public void insertBatch(List<UserActionLog> logList) {
		String sql = "INSERT INTO " + dbName
				+ ".userLog (uid, userName, pageName, info, ip, type) "//
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for (UserActionLog log : logList) {
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
	
	public List<UserActionLog> walk(int start, int limit) {
		List<UserActionLog> result = new ArrayList<UserActionLog>();

		String sql = "SELECT `id`, uid, userName, pageName, info, ip, type, `time` FROM "
				+ dbName + ".userActionLog WHERE id >= ? ORDER BY id LIMIT ?";
				
		
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
	
	public List<UserActionLog> walkAll() {
		List<UserActionLog> result = new ArrayList<UserActionLog>();
		int startid = 0;
		int limit = 500;
		while (true) {
			List<UserActionLog> walk = walk(startid, limit);
			if (null == walk || walk.size() == 0)
				break;
			result.addAll(walk);
			startid = walk.get(walk.size() - 1).id + 1;
		}
		return result;
	}
	
	private void _construct(PreparedStatement ps, UserActionLog log) throws SQLException {
		int count = 1;
		__safeSetInt(ps, count++, log.uid);
		__safeSetString(ps, count++, log.userName);
		__safeSetString(ps, count++, log.pageName);
		__safeSetString(ps, count++, log.info);
		__safeSetString(ps, count++, log.ip);
		__safeSetInt(ps, count++, log.type);
	}
	
	private UserActionLog _construct(ResultSet rs) throws SQLException {
		UserActionLog result = new UserActionLog();
		result.id = rs.getInt("id");
		result.uid = rs.getInt("uid");
		result.userName = rs.getString("userName");
		result.pageName = rs.getString("pageName");
		result.info = rs.getString("info");
		result.ip = rs.getString("ip");
		result.type = rs.getInt("type");
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
}
