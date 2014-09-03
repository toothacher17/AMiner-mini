package SchoolSearch.services.dao.user;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;

import daogenerator.utils.SqlConstructUtil;

import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.dao.user.model.User;
import SchoolSearch.services.services.userLog.UserLogService;
import SchoolSearch.services.utils.db.ConnectionPool;
import SchoolSearch.services.utils.db.DBConnection;

public class UserDao {
	
	String dbName = ConsistanceService.get("db.defaultDatabase");

	ConnectionPool pool = ConnectionPool.getInstance();
	
	public UserDao() {
		
	}
	
	public void updateInfoField(int id, String username, String mailAddress, String phone, String usertype, User u) {
		String sql = "UPDATE " + dbName + ".user SET username = ?, " //
				+ "mailAddress = ?, phone = ?, usertype=? Where id = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			ps.setString(2, mailAddress);
			ps.setString(3, phone);
			ps.setString(4, usertype);
			ps.setInt(5, id);
			SqlConstructUtil._executeUpdate(ps);
//			int affectedRows = ps.executeUpdate();
//			if (affectedRows > 0) {
//				//TODO: log
//			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}
	@Inject
	UserLogService userLogService;
	
	public void resetPass(String username, String newPassword, User u) {
		userLogService.log(u.id, u.username, "resetPass", newPassword, "old password: " + u.password, 1);
		String sql = "UPDATE " + dbName + ".user SET password = SHA(?) WHERE username = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, newPassword);
			ps.setString(2, username);
			SqlConstructUtil._executeUpdate(ps);
//			int affectedRows = ps.executeUpdate();
//			if (affectedRows > 0) {
//				//TODO: log
//			}
	
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}
	
	public boolean createUser(String username, String password, String email) {
		String sql = "INSERT INTO " + dbName + ".user (username, password, userType, mailAddress) " +//
						"VALUES (?, SHA(?), ?, ?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		boolean result = false;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			ps.setString(2, password);
			ps.setString(3, "");
			ps.setString(4, email);
			int executeUpdate = SqlConstructUtil._executeUpdate(ps);
//			int executeUpdate = ps.executeUpdate();
			if(executeUpdate > 0) {
				result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
		return result;
	}
	
	@Deprecated
	public void insert(User user) {
		String sql = "INSERT INTO " + dbName + ".user (username, password, userType, mailAddress, phone ) " +//
						"VALUES (?, SHA(?), ?, ?, ?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			_construct(ps, user);
			SqlConstructUtil._executeUpdate(ps);
//			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}
	
	@Deprecated
	public void insertBatch(List<User> userList) {
		String sql = "INSERT INTO " + dbName + ".user (username, password, userType, mailAddress, phone ) " + "VALUES (?, SHA(?), ?, ?, ?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for (User user : userList) {
				_construct(ps, user);
				ps.addBatch();
			}
			SqlConstructUtil._executeBatch(ps);
//			ps.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}
	
	public User getLoginUser(String username, String password, boolean isEncrypted)
	{	
		User result = null;
		String sql = "SELECT id,username,password,userType,mailAddress,phone FROM "//
				+ dbName + ".user WHERE username = ? AND password = SHA(?)";
		if(isEncrypted) {
			sql = "SELECT id,username,password,userType,mailAddress,phone FROM "//
					+ dbName + ".user WHERE username = ? AND password = ?";
		}
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, username);
			ps.setString(2, password);
//			rs = ps.executeQuery();
			rs = SqlConstructUtil._executeQuery(ps);
			if (rs.next()) {
				result = _construct(rs);			
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		return result;
	}
	
	
	public User getUserByUserName(String username)
	{	
		User result = null;
		String sql = "SELECT id,username,password,userType,mailAddress,phone FROM "//
				+ dbName + ".user WHERE username = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, username);
//			ps.setString(2, password);
//			rs = ps.executeQuery();
			rs = SqlConstructUtil._executeQuery(ps);
			if (rs.next()) {
				result = _construct(rs);			
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		return result;
	}
	
	
	
	private void _construct(PreparedStatement ps, User u) throws SQLException {
		int count = 1;
//		ps.setInt(count++, u.id);
		ps.setString(count++, u.username);
		ps.setString(count++, u.password);
		ps.setString(count++, u.userType);
		ps.setString(count++, u.mailAddress);
		__safeSetString(ps, count++, u.phone);
	}
	
	private User _construct(ResultSet rs) throws SQLException {
		User result = new User();
		result.id = rs.getInt("id");
		result.username = rs.getString("username");
		result.password = rs.getString("password");
		result.userType = rs.getString("userType");
		result.mailAddress = rs.getString("mailAddress");
		result.phone = rs.getString("phone");
		return result;
	}
	
	private void __safeSetString(PreparedStatement ps, int parameterIndex, String value) throws SQLException {
		if (null == value)
			ps.setNull(parameterIndex, Types.VARCHAR);
		else
			ps.setString(parameterIndex, value);
	}

}
