package SchoolSearch.services.dao.person;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.dao.person.model.Person;
import SchoolSearch.services.services.editLog.EditLogService;
import SchoolSearch.services.utils.db.ConnectionPool;
import SchoolSearch.services.utils.db.DBConnection;

/**
 * 
 * @author guanchengran
 * 
 */
public class PersonDao {

	String dbName = ConsistanceService.get("db.defaultDatabase");
	static ConnectionPool pool = ConnectionPool.getInstance();

	private static PersonDao instance;

	public static PersonDao getInstance() {
		if (null == instance) {
			instance = new PersonDao();
		}
		return instance;
	}

	public static PersonDao getIndependentInstance(String dbName) {
		return new PersonDao(dbName);
	}

	public PersonDao() {
	}

	private PersonDao(String dbName) {
		this.dbName = dbName;
	}

	public void insert(Person person) {
		String sql = "INSERT INTO " + dbName + ".person (id, name, name_alias) " + "VALUES (?,?,?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			_construct(ps, person);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}

	public void insertBatch(List<Person> personList) {
		String sql = "INSERT INTO " + dbName + ".person (id, name, name_alias) " + "VALUES (?,?,?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for (Person person : personList) {
				_construct(ps, person);
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

	public List<Person> walk(int start, int limit) {
		List<Person> result = new ArrayList<Person>();

		String sql = "SELECT id, name, name_alias FROM "//
				+ dbName + ".person WHERE id >= ? ORDER BY id LIMIT ?";

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
	
	public void deleteBatch(List<Integer> idList) {
		if(idList.size() == 0)
			return;
		String sql = "DELETE FROM " + dbName + ".person WHERE id IN ";
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
			System.out.println(">>[person]>>" + ps);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}


	public List<Person> walkAll() {
		List<Person> result = new ArrayList<Person>();
		int startid = 0;
		int limit = 500;
		while (true) {
			List<Person> walk = walk(startid, limit);
			if (null == walk || walk.size() == 0)
				break;
			result.addAll(walk);
			startid = walk.get(walk.size() - 1).id + 1;
		}
		return result;
	}

	@Inject
	EditLogService logService;

	public void updatePerson(Integer id, String name, String name_alias, Person op) {
		String sql = "UPDATE " + dbName + ".person SET name = ?, name_alias = ? WHERE id = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, name);
			ps.setString(2, name_alias);
			ps.setInt(3, id);
			int affectedRows = ps.executeUpdate();
			String[] affectStringColumns = { "name", "name_alias" };
			if (affectedRows > 0) {
				Person newPerson = new Person(name, name_alias);
				for (String fieldName : affectStringColumns) {
					Field field = Person.class.getField(fieldName);
					String old_value = (String) field.get(op);
					String new_value = (String) field.get(newPerson);
					boolean doLog = true;
					if (null == old_value) {
						if (null != new_value)
							doLog = true;
					} else if (!old_value.equals(new_value)) {
						doLog = true;
					}
					if (doLog) {
						logService.log("person", id, fieldName, old_value, new_value);
					}
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}

	public Person getPersonById(int id) {
		Person result = null;
		String sql = "SELECT id, name, name_alias FROM "//
				+ dbName + ".person WHERE id = ?";
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

	public List<Person> getPersonByName(String name) {
		List<Person> result = new ArrayList<Person>();
		String sql = "SELECT id, name, name_alias FROM "//
				+ dbName + ".person WHERE name LIKE ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, name);
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

	public List<Person> getPersonByNameList(List<String> nameList) {
		if (nameList.size() == 0)
			return new ArrayList<Person>();
		List<Person> result = new ArrayList<Person>();
		StringBuilder sb = new StringBuilder("SELECT id, name, name_alias FROM "//
				+ dbName + ".person WHERE");
		for (int i = 0; i < nameList.size(); i++) {
			sb.append(" name LIKE ?");
			if (i < nameList.size() - 1) {
				sb.append(" OR");
			}
		}
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sb.toString());
			int index = 0;
			for(String name : nameList) {
				ps.setString(++index, name);
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

	public List<Person> getPersonsByIdList(List<Integer> ids) {
		if (ids.size() == 0) {
			return new ArrayList<Person>();
		}
		String sql = "SELECT id, name, name_alias FROM "//
				+ dbName + ".person WHERE id IN ";
		List<Person> result = new ArrayList<Person>();
		StringBuilder sb = new StringBuilder();
		sb.append(sql);
		sb.append("(");
		for (int i = 0; i < ids.size(); i++) {
			sb.append("?");
			if (i < ids.size() - 1) {
				sb.append(",");
			}
		}
		sb.append(")");

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sb.toString());
			for (int i = 0; i < ids.size(); i++) {
				ps.setInt(i + 1, ids.get(i));
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

	public Person getPerson_TitleByPerson_TitleName(String name) {
		Person result = null;
		String sql = "SELECT id, name, name_alias FROM "//
				+ dbName + ".person WHERE name = ? OR name_alias = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, name);
			ps.setString(2, name);
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

	private void _construct(PreparedStatement ps, Person p) throws SQLException {
		int count = 1;
		ps.setInt(count++, p.id);
		__safeSetString(ps, count++, p.name);
		__safeSetString(ps, count++, p.name_alias);
	}

	private Person _construct(ResultSet rs) throws SQLException {
		Person result = new Person();
		result.id = rs.getInt("id");
		result.name = rs.getString("name");
		result.name_alias = rs.getString("name_alias");
		return result;
	}

	private static void __safeSetString(PreparedStatement ps, int parameterIndex, String value) throws SQLException {
		if (null == value)
			ps.setNull(parameterIndex, Types.VARCHAR);
		else
			ps.setString(parameterIndex, value);
	}

}
