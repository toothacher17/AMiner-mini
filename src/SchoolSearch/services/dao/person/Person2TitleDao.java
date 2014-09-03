package SchoolSearch.services.dao.person;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.dao.person.model.Person2Title;
import SchoolSearch.services.utils.db.ConnectionPool;
import SchoolSearch.services.utils.db.DBConnection;

public class Person2TitleDao {
	String dbName = ConsistanceService.get("db.defaultDatabase");
	static ConnectionPool pool = ConnectionPool.getInstance();
	
	private static Person2TitleDao instance;
	
	public static Person2TitleDao getInstance(){
		if(null == instance){
			instance = new Person2TitleDao();
		}
		return instance;
	}
	
	public static Person2TitleDao getIndependentInstance(String dbName) {
		return new Person2TitleDao(dbName);
	}

	public Person2TitleDao() {
	}

	private Person2TitleDao(String dbName) {
		this.dbName = dbName;
	}
	
	public void insert(Person2Title person2Title) {
		String sql = "INSERT INTO " + dbName
				+ ".person2title (person_id, title_id, year) "
				+ "VALUES (?, ?, ?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			_construct(ps, person2Title);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}

	public  void insertBatch(List<Person2Title> person2TitleList) {
		String sql = "INSERT INTO " + dbName
				+ ".person2title (person_id, title_id, year) "
				+ "VALUES (?, ?, ?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for (Person2Title person2Title : person2TitleList) {
				_construct(ps, person2Title);
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
	
	public List<Person2Title> walk(int start, int limit) {
		List<Person2Title> result = new ArrayList<Person2Title>();

		String sql = "SELECT id, person_id, title_id, year FROM "//
				+ dbName + ".person2title WHERE id >= ? ORDER BY id LIMIT ?";

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
	
	public List<Person2Title> walkAll() {
		List<Person2Title> result = new ArrayList<Person2Title>();
		int startid = 0;
		int limit = 500;
		while (true) {
			List<Person2Title> walk = walk(startid, limit);
			if (null == walk || walk.size() == 0)
				break;
			result.addAll(walk);
			startid = walk.get(walk.size() - 1).id + 1;
		}
		return result;
	}
	
	public List<Integer> getPersonIdsByTitleId(int title_id){
		List<Integer> result = new ArrayList<Integer>();
		String sql = "SELECT DISTINCT person_id FROM "//
				+ dbName + ".person2title WHERE title_id = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, title_id);
			rs = ps.executeQuery();
			while (rs.next()) {
				result.add(rs.getInt("person_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		return result;
	}
	
	public List<Person2Title> getPerson2TitlesByTitleId(Integer title_id) {
		List<Person2Title> result = new ArrayList<Person2Title>();

		String sql = "SELECT id, person_id, title_id, year FROM "//
				+ dbName + ".person2title WHERE title_id = ?";

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, title_id);
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

	public List<Person2Title> getPerson2TitlesByPersonId(Integer person_id) {

		if (null == person_id) {
			return new ArrayList<Person2Title>();
		}

		List<Person2Title> result = new ArrayList<Person2Title>();

		String sql = "SELECT id, person_id, title_id, year FROM "//
				+ dbName + ".person2title WHERE person_id = ?";

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, person_id);
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
	
	public List<Integer> getPersonIdsByTitleIds(List<Integer> title_ids){
		List<Integer> result = new ArrayList<Integer>();
		String sql = "SELECT DISTINCT person_id FROM "//
				+ dbName + ".person2title WHERE title_id IN ";
		
		StringBuilder sb = new StringBuilder();
		sb.append(sql);
		sb.append("(");
		for(int i=0; i<title_ids.size(); i++) {
			sb.append("?");
			if(i < title_ids.size()-1)
				sb.append(",");
		}
		sb.append(")");
		
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(sb.toString());
			for(int i=0; i<title_ids.size(); i++) {
				ps.setInt(i+1, title_ids.get(i));
			}
			rs = ps.executeQuery();
			while(rs.next()) {
				result.add(rs.getInt("person_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		return result;
	}
	
	public List<Integer> getTitleIdsByPersonId(Integer person_id){
		if(null == person_id) {
			return new ArrayList<Integer>();
		}
		List<Integer> result = new ArrayList<Integer>();
		String sql = "SELECT DISTINCT title_id FROM "//
				+ dbName + ".person2title WHERE person_id = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, person_id);
			rs = ps.executeQuery();
			while (rs.next()) {
				result.add(rs.getInt("title_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		return result;
	}
	
	public List<Integer> getTitleIdsByPersonIds(List<Integer> person_ids){
		List<Integer> result = new ArrayList<Integer>();
		String sql = "SELECT DISTINCT title_id FROM "//
				+ dbName + ".person2title WHERE person_id IN ";
		
		StringBuilder sb = new StringBuilder();
		sb.append(sql);
		sb.append("(");
		for(int i=0; i<person_ids.size(); i++) {
			sb.append("?");
			if(i < person_ids.size()-1)
				sb.append(",");
		}
		sb.append(")");
		
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(sb.toString());
			for(int i=0; i<person_ids.size(); i++) {
				ps.setInt(i+1, person_ids.get(i));
			}
			rs = ps.executeQuery();
			while(rs.next()) {
				result.add(rs.getInt("title_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		return result;
	}
	
	private void _construct(PreparedStatement ps, Person2Title pp)
			throws SQLException {
		int count = 1;
		ps.setInt(count++, pp.person_id);
		ps.setInt(count++, pp.title_id);
		__safeSetString(ps, count++, pp.year);
	}

	private Person2Title _construct(ResultSet rs) throws SQLException {
		Person2Title result = new Person2Title();
		result.id = rs.getInt("id");
		result.person_id = rs.getInt("person_id");
		result.title_id = rs.getInt("title_id");
		result.year = rs.getString("year");
		return result;
	}
	
	private static void __safeSetString(PreparedStatement ps, int parameterIndex, String value) throws SQLException {
		if (null == value)
			ps.setNull(parameterIndex, Types.VARCHAR);
		else
			ps.setString(parameterIndex, value);
	}
}
