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
import SchoolSearch.services.dao.person.model.PersonTitle;
import SchoolSearch.services.services.editLog.EditLogService;
import SchoolSearch.services.utils.db.ConnectionPool;
import SchoolSearch.services.utils.db.DBConnection;

public class PersonTitleDao {
	String dbName = ConsistanceService.get("db.defaultDatabase");
	static ConnectionPool pool = ConnectionPool.getInstance();
	
	private static PersonTitleDao instance;
	
	public static PersonTitleDao getInstance(){
		if(null == instance){
			instance = new PersonTitleDao();
		}
		return instance;
	}
	
	public static PersonTitleDao getIndependentInstance(String dbName) {
		return new PersonTitleDao(dbName);
	}

	public PersonTitleDao() {
	}

	private PersonTitleDao(String dbName) {
		this.dbName = dbName;
	}
	
	public void insert(PersonTitle personTitle) {
		String sql = "INSERT INTO " + dbName
				+ ".person_title (name, score) "
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

	public  void insertBatch(List<PersonTitle> personTitleList) {
		String sql = "INSERT INTO " + dbName
				+ ".person_title (name, score) "
				+ "VALUES (?,?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for (PersonTitle personTitle : personTitleList) {
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
	
	public List<PersonTitle> walk(int start, int limit) {
		List<PersonTitle> result = new ArrayList<PersonTitle>();

		String sql = "SELECT id, name, score FROM "//
				+ dbName + ".person_title WHERE id >= ? ORDER BY id LIMIT ?";

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

	public List<PersonTitle> walkAll() {
		List<PersonTitle> result = new ArrayList<PersonTitle>();
		int startid = 0;
		int limit = 500;
		while (true) {
			List<PersonTitle> walk = walk(startid, limit);
			if (null == walk || walk.size() == 0)
				break;
			result.addAll(walk);
			startid = walk.get(walk.size() - 1).id + 1;
		}
		return result;
	}
	
	@Inject
	EditLogService logService;
	
	public void updatePersonTitle(Integer id, String field_name, String new_value, PersonExt op) {
		String sql = "UPDATE " + dbName + ".person_title SET `" + field_name + "` = ? WHERE id = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, new_value);
			ps.setInt(2, id);
			int affectedRows = ps.executeUpdate();
			if (affectedRows > 0) {
				String old_value = null;
				Field field = PersonTitle.class.getField(field_name);
				old_value = (String) field.get(op);
				boolean doLog = true;
				if(null == old_value) {
					if(null != new_value)
						doLog = true;
				} else if(!old_value.equals(new_value)) {
					doLog = true;
				}
				if(doLog)
					logService.log("person_title", id, field_name, old_value,
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
	
	public PersonTitle getPersonTitleById(int id)
	{
		PersonTitle result = null;
		String sql = "SELECT id, name, score FROM "//
				+ dbName + ".person_title WHERE id = ?";
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
	
	public List<PersonTitle> getPersonTitlesByIdList(List<Integer> idList) {
		if(idList.size() == 0) {
			return new ArrayList<PersonTitle>();
		}
		String sql = "SELECT id, name, score FROM "//
				+ dbName + ".person_title WHERE id IN ";
		List<PersonTitle> result = new ArrayList<PersonTitle>();
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
	
	private void _construct(PreparedStatement ps, PersonTitle pt)
			throws SQLException {
		int count = 1;
		__safeSetString(ps, count++, pt.name);
		ps.setDouble(count++, pt.score);
	}

	private PersonTitle _construct(ResultSet rs) throws SQLException {
		PersonTitle result = new PersonTitle();
		result.id = rs.getInt("id");
		result.name = rs.getString("name");
		result.score = rs.getDouble("score");
		return result;
	}
	
	private static void __safeSetString(PreparedStatement ps, int parameterIndex, String value) throws SQLException {
		if (null == value)
			ps.setNull(parameterIndex, Types.VARCHAR);
		else
			ps.setString(parameterIndex, value);
	}
}
