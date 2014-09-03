	package SchoolSearch.services.dao.person;

/**
 * @author GCR
 */
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.dao.person.model.PersonProfile;
import SchoolSearch.services.services.editLog.EditLogService;
import SchoolSearch.services.utils.db.ConnectionPool;
import SchoolSearch.services.utils.db.DBConnection;

public class PersonProfileDao {

	String dbName = ConsistanceService.get("db.defaultDatabase");
	static ConnectionPool pool = ConnectionPool.getInstance();

	private static PersonProfileDao instance;

	public static PersonProfileDao getInstance() {
		if (null == instance) {
			instance = new PersonProfileDao();
		}
		return instance;
	}

	public static PersonProfileDao getIndependentInstance(String dbName) {
		return new PersonProfileDao(dbName);
	}

	public PersonProfileDao() {
	}

	private PersonProfileDao(String dbName) {
		this.dbName = dbName;
	}

	public void insert(PersonProfile personProfile) {
		String sql = "INSERT INTO " + dbName + ".person_profile (id, NO, position, department_id, department_key, location, phone, email, homepage, imagelink, author_id) " + "VALUES (?,?,?,?,?,?,?,?,?,?,?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			_construct(ps, personProfile);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}

	public void insertBatch(List<PersonProfile> personProfileList) {
		String sql = "INSERT INTO " + dbName + ".person_profile (id, NO, position, department_id, department_key, location, phone, email, homepage, imagelink, author_id) " + "VALUES (?,?,?,?,?,?,?,?,?,?,?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for (PersonProfile personProfile : personProfileList) {
				_construct(ps, personProfile);
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

	public List<PersonProfile> walk(int start, int limit) {
		List<PersonProfile> result = new ArrayList<PersonProfile>();

		String sql = "SELECT id, NO, position, department_id, department_key, location, phone, email, homepage, imagelink, author_id FROM "//
				+ dbName + ".person_profile WHERE id >= ? ORDER BY id LIMIT ?";

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

	public List<PersonProfile> walkAll() {
		List<PersonProfile> result = new ArrayList<PersonProfile>();
		int startid = 0;
		int limit = 500;
		while (true) {
			List<PersonProfile> walk = walk(startid, limit);
			if (null == walk || walk.size() == 0)
				break;
			result.addAll(walk);
			startid = walk.get(walk.size() - 1).id + 1;
		}
		return result;
	}

	@Inject
	EditLogService logService;

	public void updatePersonProfile(Integer id, String position, String location, String phone, String email, String homepage, Long author_id, PersonProfile op) {
		String sql = "UPDATE " + dbName + ".person_profile SET position = ?, location = ?, phone = ?, " //
				+ "email = ?, homepage = ?, author_id = ? WHERE id = ?";

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, position);
			ps.setString(2, location);
			ps.setString(3, phone);
			ps.setString(4, email);
			ps.setString(5, homepage);
			ps.setLong(6, author_id);
			ps.setInt(7, id);
			System.out.println(ps.toString());
			int affectedRows = ps.executeUpdate();
			String[] affectedStringColumns = { "position", "location", "phone", "email", "homepage" };
			if (affectedRows > 0) {
				PersonProfile np = new PersonProfile(position, location, phone, email, homepage, op.imagelink, author_id);
				for (String fieldName : affectedStringColumns) {
					Field field = PersonProfile.class.getField(fieldName);
					String old_value = (String) field.get(op);
					String new_value = (String) field.get(np);
					boolean doLog = false;
					if(null == old_value) {
						if(null != new_value)
							doLog = true;
					} else if(!old_value.equals(new_value)) {
						doLog = true;
					}
					if(doLog)
						logService.log("person_profile", id, fieldName, old_value, new_value);
				}
				if (!author_id.equals(op.author_id)) {
					logService.log("person_profile", id, "author_id", op.author_id + "", author_id + "");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}

	}
	
	public void deleteBatch(List<Integer> idList) {
		if(idList.size() == 0)
			return;
		String sql = "DELETE FROM " + dbName + ".person_profile WHERE id IN ";
		StringBuilder sb = new StringBuilder();
		sb.append(sql);
		sb.append("(");
		for(int i=0; i<idList.size(); i++) {
			sb.append("?");
			if(i < idList.size()-1)
				sb.append(",");
		}
		sb.append(")");
		
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		
		try {
			ps = conn.prepareStatement(sb.toString());
			for(int i=0; i<idList.size(); i++) {
				ps.setInt(i+1, idList.get(i));
			}
			System.out.println(">>[pp]>>" + ps);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}

	public void updateImageInPersonProfile(Integer id, String imagelink, PersonProfile op) {
		String sql = "UPDATE " + dbName + ".person_profile SET imagelink = ? WHERE id = ?";

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, imagelink);
			ps.setInt(2, id);
			System.out.println(ps.toString());
			int affectedRows = ps.executeUpdate();
			String fieldName = "imagelink";
			if (affectedRows > 0) {
				PersonProfile np = new PersonProfile();
				np.imagelink = imagelink;
				Field field = PersonProfile.class.getField(fieldName);
				String old_value = (String) field.get(op);
				String new_value = (String) field.get(np);
				boolean doLog = false;
				if (null == old_value) {
					if (null != new_value)
						doLog = true;
				} else if (!old_value.equals(new_value)) {
					doLog = true;
				}
				if (doLog)
					logService.log("person_profile", id, fieldName, old_value,
							new_value);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}
		

	public PersonProfile getPersonProfileById(int id) {
		PersonProfile result = null;
		String sql = "SELECT id,  NO, position, department_id, department_key, location, phone, email, homepage, imagelink, author_id  FROM "//
				+ dbName + ".person_profile WHERE id = ?";
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
	
	public List<PersonProfile> getPersonProfileByPersonIds(List<Integer> personIdList) {
		if(personIdList.size() == 0)
			return new ArrayList<PersonProfile>();
		List<PersonProfile> result = new ArrayList<PersonProfile>();
		String sql = "SELECT id,  NO, position, department_id, department_key, location, phone, email, homepage, imagelink, author_id  FROM "
				+ dbName + ".person_profile WHERE id IN ";
		StringBuilder sb = new StringBuilder();
		sb.append(sql);
		sb.append("(");
		for(int i=0; i<personIdList.size(); i++) {
			sb.append("?");
			if(i < personIdList.size()-1)
				sb.append(",");
		}
		sb.append(")");
		
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(sb.toString());
			for(int i=0; i<personIdList.size(); i++) {
				ps.setInt(i+1, personIdList.get(i));
			}
			rs = ps.executeQuery();
			while(rs.next()) {
				result.add(_construct(rs));	
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		return result;
	}

	public List<Integer> getPersonIdsByDepartmentId(Integer department_id) {
		List<Integer> result = new ArrayList<Integer>();
		

		String sql = "SELECT id FROM "
				+ dbName + ".person_profile WHERE department_id = ?";

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, department_id);
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

	public List<Integer> getPersonIdsByDepartmentIds(List<Integer> departmentIds) {
		
		if(departmentIds.size() == 0) {
			return new ArrayList<Integer>();
		}

		String sql = "SELECT id FROM "//
				+ dbName + ".person_profile WHERE department_id IN ";
		List<Integer> result = new ArrayList<Integer>();
		StringBuilder sb = new StringBuilder();
		sb.append(sql);
		sb.append("(");
		for (int i = 0; i < departmentIds.size(); i++) {
			sb.append("?");
			if (i < departmentIds.size() - 1) {
				sb.append(",");
			}
		}
		sb.append(")");

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sb.toString());
			for (int i = 0; i < departmentIds.size(); i++) {
				ps.setInt(i + 1, departmentIds.get(i));
			}
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

	private PersonProfile _construct(ResultSet rs) throws SQLException {
		PersonProfile result = new PersonProfile();
		result.id = rs.getInt("id");
		result.No = rs.getString("No");
		result.position = rs.getString("position");
		result.department_id = rs.getInt("department_id");
		result.department_key = rs.getString("department_key");
		result.location = rs.getString("location");
		result.phone = rs.getString("phone");
		result.email = rs.getString("email");
		result.homepage = rs.getString("homepage");
		result.imagelink = rs.getString("imagelink");
		result.author_id = rs.getLong("author_id");
		return result;
	}

	private void _construct(PreparedStatement ps, PersonProfile pp) throws SQLException {
		int count = 0;
		ps.setInt(++count, pp.id);
		__safeSetString(ps, ++count, pp.No);
		__safeSetString(ps, ++count, pp.position);
		__safeSetString(ps, ++count, pp.department_key);
		__safeSetInt(ps, ++count, pp.department_id);
		__safeSetString(ps, ++count, pp.location);
		__safeSetString(ps, ++count, pp.phone);
		__safeSetString(ps, ++count, pp.email);
		__safeSetString(ps, ++count, pp.homepage);
		__safeSetString(ps, ++count, pp.imagelink);
		ps.setLong(count++, pp.author_id);
	}

	private void __safeSetString(PreparedStatement ps, int parameterIndex, String value) throws SQLException {
		if (null == value)
			ps.setNull(parameterIndex, Types.VARCHAR);
		else
			ps.setString(parameterIndex, value);
	}

	private void __safeSetInt(PreparedStatement ps, int parameterIndex, Integer value) throws SQLException {
		if (null == value)
			ps.setNull(parameterIndex, Types.INTEGER);
		else
			ps.setInt(parameterIndex, value);
	}

}
