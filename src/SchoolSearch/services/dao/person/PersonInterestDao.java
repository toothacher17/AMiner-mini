package SchoolSearch.services.dao.person;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.dao.person.model.PersonInterest;
import SchoolSearch.services.utils.db.ConnectionPool;
import SchoolSearch.services.utils.db.DBConnection;

public class PersonInterestDao {
	
	String dbName = ConsistanceService.get("db.defaultDatabase");
	static ConnectionPool pool = ConnectionPool.getInstance();
	
	private static PersonInterestDao instance;
	
	public static PersonInterestDao getInstance(){
		if(null == instance){
			instance = new PersonInterestDao();
		}
		return instance;
	}
	
	public static PersonInterestDao getIndependentInstance(String dbName) {
		return new PersonInterestDao(dbName);
	}

	public PersonInterestDao() {
	}

	private PersonInterestDao(String dbName) {
		this.dbName = dbName;
	}
	
	public  void insert(PersonInterest personInterest) {
		String sql = "INSERT INTO " + dbName
				+ ".person_interest (person_id, aid, interest, `year`) "
				+ "VALUES (?,?,?,?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			_construct(ps, personInterest);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}

	public  void insertBatch(List<PersonInterest> personInterestList) {
		String sql = "INSERT INTO " + dbName
				+ ".person_interest (person_id, aid, interest, `year`) "
				+ "VALUES (?,?,?,?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for (PersonInterest personInterest : personInterestList) {
				_construct(ps, personInterest);
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

	public List<PersonInterest> walk(int start, int limit) {

		List<PersonInterest> result = new ArrayList<PersonInterest>();

		String sql = "SELECT id, person_id, aid, interest, `year` FROM " + dbName //
				+ ".person_interest WHERE id >= ? ORDER BY id LIMIT ?";

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

	public List<PersonInterest> walkAll() {
		List<PersonInterest> result = new ArrayList<PersonInterest>();
		int startid = 0;
		int limit = 500;
		while (true) {
			List<PersonInterest> walk = walk(startid, limit);
			if (null == walk || walk.size() == 0)
				break;
			result.addAll(walk);
			startid = walk.get(walk.size() - 1).id + 1;
		}
		return result;
	}
	
	public List<PersonInterest> getPersonInterestByPersonIds(List<Integer> person_ids) {

		List<PersonInterest> result = new ArrayList<PersonInterest>();

		String sql = "SELECT id, person_id, aid, interest, `year` FROM " + dbName //
				+ ".person_interest WHERE person_id IN ";
		
		StringBuilder sb = new StringBuilder();
		sb.append(sql);
		sb.append("(");
		for (int i = 0; i < person_ids.size(); i++) {
			sb.append("?");
			if (i < person_ids.size() - 1) {
				sb.append(",");
			}
		}
		sb.append(")");

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sb.toString());
			for (int i = 0; i < person_ids.size(); i++) {
				ps.setInt(i + 1, person_ids.get(i));
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
	
	public List<PersonInterest> getPersonInterestByAids(List<Long> aids)  {

		List<PersonInterest> result = new ArrayList<PersonInterest>();

		String sql = "SELECT id, person_id, aid, interest, `year` FROM " + dbName //
				+ ".person_interest WHERE aid IN ";
		
		StringBuilder sb = new StringBuilder();
		sb.append(sql);
		sb.append("(");
		for (int i = 0; i < aids.size(); i++) {
			sb.append("?");
			if (i < aids.size() - 1) {
				sb.append(",");
			}
		}
		sb.append(")");

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sb.toString());
			for (int i = 0; i < aids.size(); i++) {
				ps.setLong(i + 1, aids.get(i));
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
	
	public PersonInterest getPersonInterestByPersonId(Integer person_id)  {

		PersonInterest result = new PersonInterest();

		String sql = "SELECT id, person_id, aid, interest, `year` FROM " + dbName //
				+ ".person_interest WHERE person_id = ?";

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, person_id);
			rs = ps.executeQuery();
			if (rs.next()) {
				 return _construct(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		return result;
	}
	
	public PersonInterest getPersonInterestByAid(Long aid)  {

		PersonInterest result = new PersonInterest();

		String sql = "SELECT id, person_id, aid, interest, `year` FROM " + dbName //
				+ ".person_interest WHERE aid = ?";

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sql);
			ps.setLong(1, aid);
			rs = ps.executeQuery();
			if (rs.next()) {
				 return _construct(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		return result;
	}
	
	
	
	

	private PersonInterest _construct(ResultSet rs) throws SQLException {
		PersonInterest result = new PersonInterest();
		result.id = rs.getInt("id");
		result.person_id = rs.getInt("person_id");
		result.aid = rs.getLong("aid");
		result.interest = rs.getString("interest");
		result.year =  rs.getInt("year");
		return result;
	}
	
	private void _construct(PreparedStatement ps, PersonInterest pi) throws SQLException {
		int count = 0;
		__safeSetInt(ps, ++count, pi.person_id);
		ps.setLong(++count, pi.aid);
		__safeSetString(ps, ++count, pi.interest);
		__safeSetInt(ps, ++count, pi.year);
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
