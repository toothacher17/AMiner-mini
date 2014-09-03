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
import SchoolSearch.services.dao.person.model.PersonExt;
import SchoolSearch.services.services.editLog.EditLogService;
import SchoolSearch.services.utils.db.ConnectionPool;
import SchoolSearch.services.utils.db.DBConnection;

public class PersonExtDao {
	String dbName = ConsistanceService.get("db.defaultDatabase");
	static ConnectionPool pool = ConnectionPool.getInstance();
	
	private static PersonExtDao instance;
	
	public static PersonExtDao getInstance(){
		if(null == instance){
			instance = new PersonExtDao();
		}
		return instance;
	}
	
	public static PersonExtDao getIndependentInstance(String dbName) {
		return new PersonExtDao(dbName);
	}

	public PersonExtDao() {
	}

	private PersonExtDao(String dbName) {
		this.dbName = dbName;
	}
	
	public void insert(PersonExt personTitle) {
		String sql = "INSERT INTO " + dbName
				+ ".person_ext (id, title) "
				+ "VALUES (?,?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			_construct(ps, personTitle);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}

	public  void insertBatch(List<PersonExt> personTitleList) {
		String sql = "INSERT INTO " + dbName
				+ ".person_ext (id, title) "
				+ "VALUES (?,?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for (PersonExt personTitle : personTitleList) {
				_construct(ps, personTitle);
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
	
	public void deleteBatch(List<Integer> idList) {
		if(idList.size() == 0)
			return;
		String sql = "DELETE FROM " + dbName + ".person_ext WHERE id IN ";
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
			System.out.println(">>[pe]>>" + ps);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}

	public List<PersonExt> walk(int start, int limit) {
		List<PersonExt> result = new ArrayList<PersonExt>();

		String sql = "SELECT id, title FROM "//
				+ dbName + ".person_ext WHERE id >= ? ORDER BY id LIMIT ?";

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

	public List<PersonExt> walkAll() {
		List<PersonExt> result = new ArrayList<PersonExt>();
		int startid = 0;
		int limit = 500;
		while (true) {
			List<PersonExt> walk = walk(startid, limit);
			if (null == walk || walk.size() == 0)
				break;
			result.addAll(walk);
			startid = walk.get(walk.size() - 1).id + 1;
		}
		return result;
	}
	
	@Inject
	EditLogService logService;
	
	public void updatePersonExt(Integer id, String field_name, String new_value, PersonExt op) {
		String sql = "UPDATE " + dbName + ".person_ext SET `" + field_name + "` = ? WHERE id = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, new_value);
			ps.setInt(2, id);
			int affectedRows = ps.executeUpdate();
			if (affectedRows > 0) {
				String old_value = null;
				Field field = PersonExt.class.getField(field_name);
				old_value = (String) field.get(op);
				boolean doLog = true;
				if(null == old_value) {
					if(null != new_value)
						doLog = true;
				} else if(!old_value.equals(new_value)) {
					doLog = true;
				}
				if(doLog)
					logService.log("person_ext", id, field_name, old_value,
							new_value);
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}
	
	public PersonExt getPersonExtById(int id)
	{
		PersonExt result = null;
		String sql = "SELECT id, title FROM "//
				+ dbName + ".person_ext WHERE id = ?";
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
	
	public List<PersonExt> getPersonExtsByIdList(List<Integer> idList) {
		if(idList.size() == 0) {
			return new ArrayList<PersonExt>();
		}
		String sql = "SELECT id, title FROM "//
				+ dbName + ".person_ext WHERE id IN ";
		List<PersonExt> result = new ArrayList<PersonExt>();
		StringBuilder sb = new StringBuilder();
		sb.append(sql);
		sb.append("(");
		for(int i=0; i<idList.size(); i++) {
			sb.append("?");
			if(i < idList.size() -1) {
				sb.append(",");
			}
		}
		sb.append(")");
		
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sb.toString());
			for(int i=0; i<idList.size(); i++) {
				ps.setInt(i+1, idList.get(i));
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

	
	@Deprecated
	public PersonExt getPersonExtByPerson_TitleName(String title)
	{
		PersonExt result = null;
		String sql = "SELECT id, title FROM "//
				+ dbName + ".person_ext WHERE title = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, title);
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
	
	private void _construct(PreparedStatement ps, PersonExt pt)
			throws SQLException {
		int count = 1;
		ps.setInt(count++, pt.id);
		__safeSetString(ps, count++, pt.title);
	}

	private PersonExt _construct(ResultSet rs) throws SQLException {
		PersonExt result = new PersonExt();
		result.id = rs.getInt("id");
		result.title = rs.getString("title");
		return result;
	}
	
	private static void __safeSetString(PreparedStatement ps, int parameterIndex, String value) throws SQLException {
		if (null == value)
			ps.setNull(parameterIndex, Types.VARCHAR);
		else
			ps.setString(parameterIndex, value);
	}
}
