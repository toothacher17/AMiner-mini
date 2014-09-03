package SchoolSearch.services.dao.person;

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
import SchoolSearch.services.dao.person.model.PersonRelation;
import SchoolSearch.services.utils.db.ConnectionPool;
import SchoolSearch.services.utils.db.DBConnection;

public class PersonRelationDao {
	String dbName = ConsistanceService.get("db.defaultDatabase");
	static ConnectionPool pool = ConnectionPool.getInstance();
	
	private static PersonRelationDao instance;
	
	public static PersonRelationDao getInstance(){
		if(null == instance){
			instance = new PersonRelationDao();
		}
		return instance;
	}
	
	public static PersonRelationDao getIndependentInstance(String dbName) {
		return new PersonRelationDao(dbName);
	}

	public PersonRelationDao() {
	}

	private PersonRelationDao(String dbName) {
		this.dbName = dbName;
	}
	
	public void insert(PersonRelation personRelation) {
		String sql = "INSERT INTO " + dbName
				+ ".personRelation (person1_id, person2_id, person2_name, type, score) "
				+ "VALUES (?, ?, ?, ?, ?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			_construct(ps, personRelation);
//			System.out.println(ps);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}

	public  void insertBatch(List<PersonRelation> personRelationList) {
		String sql = "INSERT INTO " + dbName
				+ ".personRelation (person1_id, person2_id, person2_name, type, score) "
				+ "VALUES (?, ?, ?, ?, ?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for (PersonRelation personRelation : personRelationList) {
				_construct(ps, personRelation);
				ps.addBatch();
			}
			System.out.println(ps);
			ps.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}
	
	public List<PersonRelation> walk(int start, int limit) {
		List<PersonRelation> result = new ArrayList<PersonRelation>();

		String sql = "SELECT id, person1_id, person2_id, person2_name, type, score FROM "//
				+ dbName + ".personRelation WHERE id >= ? ORDER BY id LIMIT ?";

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
	
	public List<PersonRelation> walkAll() {
		List<PersonRelation> result = new ArrayList<PersonRelation>();
		int startid = 0;
		int limit = 500;
		while (true) {
			List<PersonRelation> walk = walk(startid, limit);
			if (null == walk || walk.size() == 0)
				break;
			result.addAll(walk);
			startid = walk.get(walk.size() - 1).id + 1;
		}
		return result;
	}
	
	public void truncate() {
		String sql = "TRUNCATE " + dbName + ".personRelation";
		pool.getConnection().execute(sql);
	}
	
	public List<PersonRelation> getExsitedPersonRelationByPersonId(Integer id) {
		List<PersonRelation> result = new ArrayList<PersonRelation>();

		String sql = "SELECT id, person1_id, person2_id, person2_name, type, score FROM "//
				+ dbName + ".personRelation WHERE person1_id = ? AND person2_id is not null";

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			rs = ps.executeQuery();
			System.out.println(ps);
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
	
	public List<PersonRelation> getPersonRelationByPersonId(Integer id) {
		List<PersonRelation> result = new ArrayList<PersonRelation>();

		String sql = "SELECT id, person1_id, person2_id, person2_name, type, score FROM "//
				+ dbName + ".personRelation WHERE person1_id = ?";

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
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
	
	
	private void _construct(PreparedStatement ps, PersonRelation pr)
			throws SQLException {
		int count = 1;
		__safeSetInt(ps, count++, pr.person1_id);
		__safeSetInt(ps, count++, pr.person2_id);
		__safeSetString(ps, count++, pr.person2_name);
		__safeSetString(ps, count++, pr.type);
		__safeSetInt(ps, count++, pr.score);
	}

	private PersonRelation _construct(ResultSet rs) throws SQLException {
		PersonRelation result = new PersonRelation();
		result.id = rs.getInt("id");
		result.person1_id = rs.getInt("person1_id");
		result.person2_id = rs.getInt("person2_id");
		result.person2_name = rs.getString("person2_name");
		result.type = rs.getString("type");
		result.score = rs.getInt("score");
		return result;
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
