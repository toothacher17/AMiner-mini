package SchoolSearch.services.dao.oranizationLevels;
/**
 * @author GCR
 */
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.dao.oranizationLevels.model.School;
import SchoolSearch.services.dao.oranizationLevels.model.School;
import SchoolSearch.services.utils.db.ConnectionPool;
import SchoolSearch.services.utils.db.DBConnection;

public class SchoolDao {
	
	String dbName = ConsistanceService.get("db.defaultDatabase");
	static ConnectionPool pool = ConnectionPool.getInstance();
	
	private static SchoolDao instance;
	
	public static SchoolDao getInstance(){
		if(null == instance){
			instance = new SchoolDao();
		}
		return instance;
	}
	
	public static SchoolDao getIndependentInstance(String dbName) {
		return new SchoolDao(dbName);
	}

	public SchoolDao() {
	}

	private SchoolDao(String dbName) {
		this.dbName = dbName;
	}
	
	public  void insert(School school) {
		String sql = "INSERT INTO " + dbName
				+ ".school (schoolname) "
				+ "VALUES (?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			_construct(ps, school);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}

	public  void insertBatch(List<School> schoolList) {
		String sql = "INSERT INTO " + dbName
				+ ".school (schoolname) "
				+ "VALUES (?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for (School school : schoolList) {
				_construct(ps, school);
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
	
	public List<School> walk(int start, int limit) {
		List<School> result = new ArrayList<School>();

		String sql = "SELECT id,schoolname FROM "//
				+ dbName + ".school WHERE id >= ? ORDER BY id LIMIT ?";

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

	public List<School> walkAll() {
		List<School> result = new ArrayList<School>();
		int startid = 0;
		int limit = 500;
		while (true) {
			List<School> walk = walk(startid, limit);
			if (null == walk || walk.size() == 0)
				break;
			result.addAll(walk);
			startid = walk.get(walk.size() - 1).id + 1;
		}
		return result;
	}
	
	public School getSchoolById(int id)
	{
		School result = null;
		String sql = "SELECT id,schoolname FROM "//
				+ dbName + ".school WHERE id = ?";
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
	
	public List<School> getSchoolsByIds(List<Integer> ids){
		List<School> result = new ArrayList<School>();
		StringBuilder sb = new StringBuilder();
		
		String sql = "SELECT id,schoolname FROM "//
				+ dbName + ".school WHERE id IN ";
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
	
	public School getSchoolByName(String schoolname)
	{
		School result = null;
		String sql = "SELECT id,schoolname FROM "//
				+ dbName + ".school WHERE schoolname = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, schoolname);
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
	
	public List<School> getSchoolsByNames(List<String> schoolnames){
		List<School> results = new ArrayList<School>();
		for(String schoolname:schoolnames){
			results.add(getSchoolByName(schoolname));
		}
		return results;
	}
	
	private void _construct(PreparedStatement ps, School s) throws SQLException {
		int count = 1;
		__safeSetString(ps, count++, s.schoolname);
	}
	
	private School _construct(ResultSet rs) throws SQLException {
		School result = new School();
		result.id = rs.getInt("id");
		result.schoolname = rs.getString("schoolname");
		return result;
	}
	
	private static void __safeSetString(PreparedStatement ps, int parameterIndex, String value) throws SQLException {
		if (null == value)
			ps.setNull(parameterIndex, Types.VARCHAR);
		else
			ps.setString(parameterIndex, value);
	}

}
