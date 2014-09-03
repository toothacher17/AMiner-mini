package SchoolSearch.services.dao.oranizationLevels;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.dao.oranizationLevels.model.Department;
import SchoolSearch.services.dao.oranizationLevels.model.Institute;
import SchoolSearch.services.utils.db.ConnectionPool;
import SchoolSearch.services.utils.db.DBConnection;

public class InstituteDao {
	private static InstituteDao instance;
	String dbName = ConsistanceService.get("db.defaultDatabase");
	static ConnectionPool pool = ConnectionPool.getInstance();
	
	public static InstituteDao getInstance(){
		if(null == instance){
			instance = new InstituteDao();
		}
		return instance;
	}
	
	public static InstituteDao getIndependentInstance(String dbName) {
		return new InstituteDao(dbName);
	}

	public InstituteDao() {
	}

	private InstituteDao(String dbName) {
		this.dbName = dbName;
	}
	
	
	public void insert(Institute institute) {
		String sql = "INSERT INTO " + dbName
				+ ".institute (institute_name) "
				+ "VALUES (?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			_construct(ps, institute);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}

	public  void insertBatch(List<Institute> instituteList) {
		String sql = "INSERT INTO " + dbName
				+ ".institute (institute_name) "
				+ "VALUES (?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for (Institute institute : instituteList) {
				_construct(ps, institute);
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}
	
	public List<Institute> walk(int start, int limit) {
		List<Institute> result = new ArrayList<Institute>();

		String sql = "SELECT id, institute_name FROM "//
				+ dbName + ".institute WHERE id >= ? ORDER BY id LIMIT ?";

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, start);
			ps.setInt(2, limit);
			rs = ps.executeQuery();
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

	public List<Institute> walkAll() {
		List<Institute> result = new ArrayList<Institute>();
		int startid = 0;
		int limit = 500;
		while (true) {
			List<Institute> walk = walk(startid, limit);
			if (null == walk || walk.size() == 0)
				break;
			result.addAll(walk);
			startid = walk.get(walk.size() - 1).id + 1;
		}
		return result;
	}
	
	public Institute getInstituteById(int id)
	{
		Institute result = null;
		String sql = "SELECT id, institute_name FROM "//
				+ dbName + ".institute WHERE id = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, id); 
			rs = ps.executeQuery();
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
	
	public List<Institute> getInstitutesByIds(List<Integer> ids){
		
		String sql = "SELECT id, institute_name FROM "//
				+ dbName + ".institute WHERE id IN ";
		List<Institute> result = new ArrayList<Institute>();
		StringBuilder sb = new StringBuilder();
		sb.append(sql);
		sb.append("(");
		for(int i=0; i<ids.size(); i++) {
			sb.append("?");
			if(i < ids.size() -1) {
				sb.append(",");
			}
		}
		sb.append(")");
		
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sb.toString());
			for(int i=0; i<ids.size(); i++) {
				ps.setInt(i+1, ids.get(i));
			}
			rs = ps.executeQuery();
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
	
	public Institute getInstituteByInstituteName(String InstituteNameStr)
	{
		Institute result = null;
		String sql = "SELECT id, institute_name FROM "//
				+ dbName + ".institute WHERE institute_name = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, InstituteNameStr);
			rs = ps.executeQuery();
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
	
	private void _construct(PreparedStatement ps, Institute c)
			throws SQLException {
		int count = 1;
		__safeSetString(ps, count++, c.institute_name);
	}

	private Institute _construct(ResultSet rs) throws SQLException {
		Institute result = new Institute();
		result.id = rs.getInt("id");
		result.institute_name = rs.getString("institute_name");
		return result;
	}
	
	private static void __safeSetString(PreparedStatement ps, int parameterIndex, String value) throws SQLException {
		if (null == value)
			ps.setNull(parameterIndex, Types.VARCHAR);
		else
			ps.setString(parameterIndex, value);
	}
}
