package SchoolSearch.services.dao.graduatePublication;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.utils.db.ConnectionPool;
import SchoolSearch.services.utils.db.DBConnection;
import SchoolSearch.services.dao.graduatePublication.model.Person2GraduatePublication;

/**
 * 
 * @author guanchengran
 *
 */
public class Person2GraduatePublicationDao {
	
	String dbName = ConsistanceService.get("db.defaultDatabase");
	static ConnectionPool pool = ConnectionPool.getInstance();
	
	public static Person2GraduatePublicationDao instance;
	
	public static Person2GraduatePublicationDao getInstance(){
		if(null == instance){
			instance = new Person2GraduatePublicationDao();
		}
		return instance;
	}
	
	public static Person2GraduatePublicationDao getIndependentInstance(String dbName) {
		return new Person2GraduatePublicationDao(dbName);
	}

	public Person2GraduatePublicationDao() {
	}

	private Person2GraduatePublicationDao(String dbName) {
		this.dbName = dbName;
	}
	
	public void insert(Person2GraduatePublication person2GraduatePublication) {
		String sql = "INSERT INTO " + dbName
				+ ".person2graduatepublication (person_id, graduatepublication_id, type, position) "
				+ "VALUES (?, ?, ?, ?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			_construct(ps, person2GraduatePublication);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}

	public  void insertBatch(List<Person2GraduatePublication> personGraduatePublicationList) {
		String sql = "INSERT INTO " + dbName
				+ ".person2graduatepublication (person_id, graduatepublication_id, type, position) "
				+ "VALUES (?, ?, ?, ?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for (Person2GraduatePublication personGraduatePublication : personGraduatePublicationList) {
				_construct(ps, personGraduatePublication);
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
	
	public void updateBatch(List<Person2GraduatePublication> person2graduatepublicationList) {
		String sql = "UPDATE " + dbName
				+ ".person2graduatepublication SET person_id = ?, graduatepublication_id = ? "
				+ "WHERE id = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for (Person2GraduatePublication person2graduatepublication : person2graduatepublicationList) {
				ps.setInt(1, person2graduatepublication.person_id);
				ps.setInt(2, person2graduatepublication.graduatePublication_id);
				ps.setInt(3, person2graduatepublication.id);
				System.out.println(">>[p2g]>>" + ps);
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
		String sql = "DELETE FROM " + dbName + ".person2graduatepublication WHERE id IN ";
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
			System.out.println(">>[p2g]>>" + ps);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}

	public List<Person2GraduatePublication> walk(int start, int limit) {
		List<Person2GraduatePublication> result = new ArrayList<Person2GraduatePublication>();

		String sql = "SELECT id, person_id, graduatepublication_id, type, position FROM "//
				+ dbName + ".person2graduatepublication WHERE id >= ? ORDER BY id LIMIT ?";

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
	
	public List<Person2GraduatePublication> walkAll() {
		List<Person2GraduatePublication> result = new ArrayList<Person2GraduatePublication>();
		int startid = 0;
		int limit = 500;
		while (true) {
			List<Person2GraduatePublication> walk = walk(startid, limit);
			if (null == walk || walk.size() == 0)
				break;
			result.addAll(walk);
			startid = walk.get(walk.size() - 1).id + 1;
		}
		return result;
	}
	
	public List<Integer> getPersonIdsByGraduatePublicationIdAndType(int graduatepublication_id, String type){
		List<Integer> result = new ArrayList<Integer>();
		String sql = "SELECT DISTINCT person_id FROM "//
				+ dbName + ".person2graduatepublication WHERE graduatepublication_id = ? AND type = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, graduatepublication_id);
			ps.setString(2, type);
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
	
	public List<Integer> getPersonIdsByGraduatePublicationIdsAndType(List<Integer> graduatepublication_ids, String type){
		List<Integer> result = new ArrayList<Integer>();
		if(graduatepublication_ids.size() == 0)
			return result;
		String sql = "SELECT DISTINCT person_id FROM "//
				+ dbName + ".person2graduatepublication WHERE graduatepublication_id IN ";
		
		StringBuilder sb = new StringBuilder();
		sb.append(sql);
		sb.append("(");
		for(int i=0; i<graduatepublication_ids.size(); i++) {
			sb.append("?");
			if(i < graduatepublication_ids.size()-1)
				sb.append(",");
		}
		sb.append(") AND type = ?");
		
		
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(sb.toString());
			for(int i=0; i<graduatepublication_ids.size(); i++) {
				ps.setInt(i+1, graduatepublication_ids.get(i));
			}
			ps.setString(graduatepublication_ids.size()+1, type);
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
	
	public List<Integer> getGraduatePublicationIdsByPersonIdAndType(int person_id, String type){
		List<Integer> result = new ArrayList<Integer>();
		String sql = "SELECT DISTINCT graduatepublication_id FROM "//
				+ dbName + ".person2graduatepublication WHERE person_id = ? AND type = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, person_id);
			ps.setString(2, type);
			rs = ps.executeQuery();
			while (rs.next()) {
				result.add(rs.getInt("graduatepublication_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		return result;
	}
	
	public List<Person2GraduatePublication> getPersonGraduatePublicationsByGrapubId(Integer grapubid){
		List<Person2GraduatePublication> result = new ArrayList<Person2GraduatePublication>();
		String sql = "SELECT id, person_id, graduatepublication_id, type, position FROM "//
				+ dbName + ".person2graduatepublication WHERE graduatepublication_id = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, grapubid);
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
	
	public List<Person2GraduatePublication> getPersonGraduatePublicationsByPersonId(Integer person_id){
		if(null == person_id) {
			return new ArrayList<Person2GraduatePublication>();
		}
		List<Person2GraduatePublication> result = new ArrayList<Person2GraduatePublication>();
		String sql = "SELECT id, person_id, graduatepublication_id, type, position FROM "//
				+ dbName + ".person2graduatepublication WHERE person_id = ?";
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
	
	public List<Person2GraduatePublication> getPerson2GraduatePublicationByPersonIds(List<Integer> personIdList) {
		if(personIdList.size() == 0)
			return new ArrayList<Person2GraduatePublication>();
		List<Person2GraduatePublication> result = new ArrayList<Person2GraduatePublication>();
		String sql = "SELECT id, person_id, graduatepublication_id, type, position FROM "//
				+ dbName + ".person2graduatepublication WHERE person_id IN ";
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

	
	
	private void _construct(PreparedStatement ps, Person2GraduatePublication pgp)
			throws SQLException {
		int count = 1;
		__safeSetInt(ps, count++, pgp.person_id);
		__safeSetInt(ps, count++, pgp.graduatePublication_id);
		__safeSetString(ps, count++, pgp.type);
		__safeSetInt(ps, count++, pgp.position);
	}

	private Person2GraduatePublication _construct(ResultSet rs) throws SQLException {
		Person2GraduatePublication result = new Person2GraduatePublication();
		result.id = rs.getInt("id");
		result.person_id = rs.getInt("person_id");
		result.graduatePublication_id = rs.getInt("graduatePublication_id");
		result.type = rs.getString("type");
		result.position = rs.getInt("position");
		return result;
	}
	
	private void __safeSetString(PreparedStatement ps, int parameterIndex, String value) throws SQLException {
		if (null == value)
			ps.setNull(parameterIndex, Types.VARCHAR);
		else
			ps.setString(parameterIndex, value);
	}
	
	private void __safeSetInt(PreparedStatement ps, int parameterIndex, Integer value) throws SQLException {
		if (value == null)
			ps.setNull(parameterIndex, Types.INTEGER);
		else
			ps.setInt(parameterIndex, value);
	}

}
