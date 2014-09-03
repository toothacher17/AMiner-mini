package SchoolSearch.services.dao.oranizationLevels;

/**
 * @author CX
 */

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.dao.oranizationLevels.model.Department;
import SchoolSearch.services.utils.db.ConnectionPool;
import SchoolSearch.services.utils.db.DBConnection;

public class DepartmentDao {
	private static DepartmentDao instance;
	String dbName = ConsistanceService.get("db.defaultDatabase");
	static ConnectionPool pool = ConnectionPool.getInstance();

	public static DepartmentDao getInstance() {
		if (null == instance) {
			instance = new DepartmentDao();
		}
		return instance;
	}

	public static DepartmentDao getIndependentInstance(String dbName) {
		return new DepartmentDao(dbName);
	}

	public DepartmentDao() {
	}

	private DepartmentDao(String dbName) {
		this.dbName = dbName;
	}

	
	public void insert(Department department) {
		String sql = "INSERT INTO " + dbName + ".department (school_id, department_key, department_name) " + "VALUES (?, ?, ?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			_constract(ps, department);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}

	public void insertBatch(List<Department> departmentList) {
		String sql = "INSERT INTO " + dbName + ".department (school_id, department_key, department_name) " + "VALUES (?, ?, ?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for (Department department : departmentList) {
				_constract(ps, department);
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


	public List<Department> walk(int start, int limit) {
		List<Department> result = new ArrayList<Department>();

		String sql = "SELECT id, school_id, department_key, department_name FROM "//
				+ dbName + ".department WHERE id >= ? ORDER BY id LIMIT ?";

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


	public List<Department> walkAll() {
		List<Department> result = new ArrayList<Department>();
		int startid = 0;
		int limit = 500;
		while (true) {
			List<Department> walk = walk(startid, limit);
			if (null == walk || walk.size() == 0)
				break;
			result.addAll(walk);
			startid = walk.get(walk.size() - 1).id + 1;
		}
		return result;
	}

	
	public Department getDepartmentById(int id) {
		Department result = null;
		String sql = "SELECT id, school_id, department_key,department_name FROM "//
				+ dbName + ".department WHERE id = ?";
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

	public List<Department> getDepartmentsByIds(List<Integer> ids) {
		
		String sql = "SELECT id, school_id, department_key,department_name FROM "//
				+ dbName + ".department WHERE id IN ";
		List<Department> result = new ArrayList<Department>();
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

	
	public List<Department> getDepartmentsBySchoolId(int schoolId) {
		List<Department> result = new ArrayList<Department>();
		String sql = "SELECT id,school_id,department_key,department_name FROM "//
				+ dbName + ".department WHERE school_id = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, schoolId);
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
	
	public List<Integer> getDepartmentIdsBySchoolId(int schoolId) {
		List<Integer> result = new ArrayList<Integer>();
		String sql = "SELECT id FROM "//
				+ dbName + ".department WHERE school_id = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, schoolId);
			rs = ps.executeQuery();
			while (rs.next()) {
				result.add(rs.getInt("id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		return result;
	}


	public Department getDepartmentByDepartmentkey(String DepartmentKeyStr) {
		Department result = null;
		String sql = "SELECT id, school_id, department_key,department_name FROM "//
				+ dbName + ".department WHERE department_key = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, DepartmentKeyStr);
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


	public Department getDepartmentByDepartmentName(String DepartmentNameStr) {
		Department result = null;
		String sql = "SELECT id,school_id,department_key,department_name FROM "//
				+ dbName + ".department WHERE department_name = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, DepartmentNameStr);
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

	
	private void _constract(PreparedStatement ps, Department c) throws SQLException {
		int count = 1;
		ps.setInt(count++, c.school_id);
		__safeSetString(ps, count++, c.department_key);
		__safeSetString(ps, count++, c.department_name);
	}

	
	private Department _construct(ResultSet rs) throws SQLException {
		Department result = new Department();
		result.id = rs.getInt("id");
		result.school_id = rs.getInt("school_id");
		result.department_key = rs.getString("department_key");
		result.department_name = rs.getString("department_name");
		return result;
	}

	private static void __safeSetString(PreparedStatement ps, int parameterIndex, String value) throws SQLException {
		if (null == value)
			ps.setNull(parameterIndex, Types.VARCHAR);
		else
			ps.setString(parameterIndex, value);
	}
	
	public static void main(String args[]) {
		DepartmentDao d = new DepartmentDao();
		d.walkAll();
		List<Department> departList = d.getDepartmentsBySchoolId(7);
		for(Department depart : departList) {
			System.out.println(depart.department_name);
		}
	}
}
