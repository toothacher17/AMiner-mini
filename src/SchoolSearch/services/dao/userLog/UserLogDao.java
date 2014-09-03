package SchoolSearch.services.dao.userLog;
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

import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.dao.userLog.model.UserLog;
import SchoolSearch.services.utils.db.ConnectionPool;
import SchoolSearch.services.utils.db.DBConnection;

public class UserLogDao {
	String dbName = ConsistanceService.get("db.defaultDatabase");
	static ConnectionPool pool = ConnectionPool.getInstance();
	
	private static UserLogDao instance;
	
	public static UserLogDao getInstance(){
		if(null == instance){
			instance = new UserLogDao();
		}
		return instance;
	}
	
	public static UserLogDao getIndependentInstance(String dbName) {
		return new UserLogDao(dbName);
	}

	public UserLogDao() {
	}

	private UserLogDao(String dbName) {
		this.dbName = dbName;
	}
	
	public void insert(UserLog log) {
		String sql = "INSERT INTO " + dbName
				+ ".userLog (uid, username, ip, state, password, userInfo, type) "//
				+ "VALUES (?, ?, ?, ?, SHA(?), ?, ?)";
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

	public void insertBatch(List<UserLog> logList) {
		String sql = "INSERT INTO " + dbName
				+ ".userLog (uid, ip, state, password, userInfo, type) "//
				+ "VALUES (?, ?, ?, ?, SHA(?), ?, ?)";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for (UserLog log : logList) {
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
	
	public List<UserLog> walk(int start, int limit) {
		List<UserLog> result = new ArrayList<UserLog>();

		String sql = "SELECT `id`, uid, username, ip, state, `password`, userInfo, type, `time` FROM "
				+ dbName + ".userLog WHERE id >= ? ORDER BY id LIMIT ?";
				
		
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
	
	public List<UserLog> walkAll() {
		List<UserLog> result = new ArrayList<UserLog>();
		int startid = 0;
		int limit = 500;
		while (true) {
			List<UserLog> walk = walk(startid, limit);
			if (null == walk || walk.size() == 0)
				break;
			result.addAll(walk);
			startid = walk.get(walk.size() - 1).id + 1;
		}
		return result;
	}
	
	private void _construct(PreparedStatement ps, UserLog log) throws SQLException {
		int count = 1;
		__safeSetInt(ps, count++, log.uid);
		__safeSetString(ps, count++, log.username);
		__safeSetString(ps, count++, log.ip);
		__safeSetString(ps, count++, log.state);
		__safeSetString(ps, count++, log.password);
		__safeSetString(ps, count++, log.userInfo);
		__safeSetInt(ps, count++, log.type);
	}
	
	private UserLog _construct(ResultSet rs) throws SQLException {
		UserLog result = new UserLog();
		result.id = rs.getInt("id");
		result.uid = rs.getInt("uid");
		result.username = rs.getString("username");
		result.ip = rs.getString("ip");
		result.state = rs.getString("state");
		result.password = rs.getString("password");
		result.userInfo = rs.getString("userInfo");
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
	
	public static void main(String[] args) {
		UserLogDao u = new UserLogDao();
		UserLog log = new UserLog();
		log.uid = 1;
		log.ip = "2";
		log.state = "3";
		log.password = "4";
		log.userInfo = "5";
		log.type = 1;
		u.insert(log);
	}
}
