package SchoolSearch.services.dao.oranizationLevels;
/**
 * @author GCR
 */
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.dao.oranizationLevels.model.Institute2Department;
import SchoolSearch.services.utils.db.ConnectionPool;
import SchoolSearch.services.utils.db.DBConnection;

public class Institute2DepartmentDao {
	private static Institute2DepartmentDao instance;
	String dbName = ConsistanceService.get("db.defaultDatabase");
	static ConnectionPool pool = ConnectionPool.getInstance();
	
	public static Institute2DepartmentDao getInstance(){
		if(null == instance){
			instance = new Institute2DepartmentDao();
		}
		return instance;
	}
	
	public static Institute2DepartmentDao getIndependentInstance(String dbName) {
		return new Institute2DepartmentDao(dbName);
	}

	public Institute2DepartmentDao() {
	}

	private Institute2DepartmentDao(String dbName) {
		this.dbName = dbName;
	}
	
	
	public void insert(Institute2Department departmentInstitute) {
		String sql = "INSERT INTO " + dbName
				+ ".department2institute (department_id, institute_id) "
				+ "VALUES (?, ?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			_construct(ps, departmentInstitute);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}

	public  void insertBatch(List<Institute2Department> departmentInstituteList) {
		String sql = "INSERT INTO " + dbName
				+ ".department2institute (department_id, institute_id) "
				+ "VALUES (?, ?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for (Institute2Department departmentInstitute : departmentInstituteList) {
				_construct(ps, departmentInstitute);
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
	
	public List<Institute2Department> walk(int start, int limit) {
		List<Institute2Department> result = new ArrayList<Institute2Department>();

		String sql = "SELECT id, department_id, institute_id FROM "//
				+ dbName + ".department2institute WHERE id >= ? ORDER BY id LIMIT ?";

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
	
	public List<Institute2Department> walkAll() {
		List<Institute2Department> result = new ArrayList<Institute2Department>();
		int startid = 0;
		int limit = 500;
		while (true) {
			List<Institute2Department> walk = walk(startid, limit);
			if (null == walk || walk.size() == 0)
				break;
			result.addAll(walk);
			startid = walk.get(walk.size() - 1).id + 1;
		}
		return result;
	}
	
	public List<Integer> getExistedDepartmentIds() {
		List<Integer> result = new ArrayList<Integer>();
		String sql = "SELECT DISTINCT department_id FROM " //
					+dbName + ".department2institute";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()) {
				result.add(rs.getInt("department_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		return result;
		
		
	}
	
	public List<Integer> getInstituteIdsByDepartmentId(int department_id){
		List<Integer> result = new ArrayList<Integer>();
		String sql = "SELECT DISTINCT institute_id FROM "//
				+ dbName + ".department2institute WHERE department_id = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, department_id);
			rs = ps.executeQuery();
			while (rs.next()) {
				result.add(rs.getInt("institute_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		return result;
	}
	

	public List<Integer> getDepartmentIdsByInstituteId(Integer institute_id){
		List<Integer> result = new ArrayList<Integer>();
		String sql = "SELECT id, department_id, institute_id FROM "//
				+ dbName + ".department2institute WHERE institute_id = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, institute_id);
			rs = ps.executeQuery();
			while (rs.next()) {
				result.add(rs.getInt("department_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		return result;
	}
	
	@Deprecated
	public Set<Integer> getInstituteIdsByDepartmentIds(Set<Integer> departments){
		Set<Integer> result = new HashSet<Integer>();
		for(Integer dep:departments){
			result.addAll(getInstituteIdsByDepartmentId(dep.intValue()));
		}
		return result;
	}
	
	
	@Deprecated
	public Set<Integer> getDepartmentIdsByInstituteIds(Set<Integer> institutes){
		Set<Integer> result = new HashSet<Integer>();
		for(Integer ins:institutes){
			result.addAll(getDepartmentIdsByInstituteId(ins.intValue()));
		}
		return result;
	}
	
	private void _construct(PreparedStatement ps, Institute2Department c)
			throws SQLException {
		int count = 1;
		ps.setInt(count++, c.department_id);
		ps.setInt(count++, c.institute_id);
	}

	private Institute2Department _construct(ResultSet rs) throws SQLException {
		Institute2Department result = new Institute2Department();
		result.id = rs.getInt("id");
		result.department_id = rs.getInt("department_id");
		result.institute_id = rs.getInt("institute_id");

		return result;
	}
		
}
