package SchoolSearch.services.dao.publication;
/**
 * @author GCR
 */
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import SchoolSearch.services.dao.publication.model.Person2Publication;
import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.utils.db.ConnectionPool;
import SchoolSearch.services.utils.db.DBConnection;

public class Person2PublicationDao {
	
	String dbName = ConsistanceService.get("db.defaultDatabase");
	static ConnectionPool pool = ConnectionPool.getInstance();
	
	private static Person2PublicationDao instance;
	
	public static Person2PublicationDao getInstance(){
		if(null == instance){
			instance = new Person2PublicationDao();
		}
		return instance;
	}
	
	public static Person2PublicationDao getIndependentInstance(String dbName) {
		return new Person2PublicationDao(dbName);
	}

	public Person2PublicationDao() {
	}

	private Person2PublicationDao(String dbName) {
		this.dbName = dbName;
	}
	
	public void insert(Person2Publication personPublication) {
		String sql = "INSERT INTO " + dbName
				+ ".person2publication (person_id, publication_id, position) "
				+ "VALUES (?, ?, ?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			_construct(ps, personPublication);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}

	public  void insertBatch(List<Person2Publication> personPublicationList) {
		String sql = "INSERT INTO " + dbName
				+ ".person2publication (person_id, publication_id, position) "
				+ "VALUES (?, ?, ?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for (Person2Publication personPublication : personPublicationList) {
				_construct(ps, personPublication);
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
	
	public void updateBatch(List<Person2Publication> person2publicationList) {
		String sql = "UPDATE " + dbName
				+ ".person2publication SET person_id = ?, publication_id = ? "
				+ "WHERE id = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for (Person2Publication person2publication : person2publicationList) {
				ps.setInt(1, person2publication.person_id);
				ps.setInt(2, person2publication.publication_id);
				ps.setInt(3, person2publication.id);
				System.out.println(">>[p2p]>>" + ps);
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
		String sql = "DELETE FROM " + dbName + ".person2publication WHERE id IN ";
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
			System.out.println(">>[p2p]>>" + ps);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}

	
	public List<Person2Publication> walk(int start, int limit) {
		List<Person2Publication> result = new ArrayList<Person2Publication>();

		String sql = "SELECT id, person_id, publication_id, position FROM "//
				+ dbName + ".person2publication WHERE id >= ? ORDER BY id LIMIT ?";

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
	
	public List<Person2Publication> walkAll() {
		List<Person2Publication> result = new ArrayList<Person2Publication>();
		int startid = 0;
		int limit = 500;
		while (true) {
			List<Person2Publication> walk = walk(startid, limit);
			if (null == walk || walk.size() == 0)
				break;
			result.addAll(walk);
			startid = walk.get(walk.size() - 1).id + 1;
		}
		return result;
	}
	
	public List<Integer> getPersonIdsByPublicationId(int publication_id){
		List<Integer> result = new ArrayList<Integer>();
		String sql = "SELECT DISTINCT person_id FROM "//
				+ dbName + ".person2publication WHERE publication_id = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, publication_id);
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
	
	public List<Person2Publication> getPerson2PublicationByPublicationId(Integer pubid) {
		List<Person2Publication> result = new ArrayList<Person2Publication>();

		String sql = "SELECT id, person_id, publication_id, position FROM "//
				+ dbName + ".person2publication WHERE publication_id = ?";

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, pubid);
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
	
	public List<Person2Publication> getPerson2PublicationByPersonId(Integer person_id) {
		
		if(null == person_id) {
			return new ArrayList<Person2Publication>();
		}
		
		List<Person2Publication> result = new ArrayList<Person2Publication>();

		String sql = "SELECT id, person_id, publication_id, position FROM "//
				+ dbName + ".person2publication WHERE person_id = ?";

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
	
	public List<Person2Publication> getPerson2PublicationByPersonIds(List<Integer> personIdList) {
		if(personIdList.size() == 0)
			return new ArrayList<Person2Publication>();
		List<Person2Publication> result = new ArrayList<Person2Publication>();
		String sql = "SELECT id, person_id, publication_id, position FROM "//
				+ dbName + ".person2publication WHERE person_id IN ";
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
	
	public List<Integer> getPersonIdsByPublicationIds(List<Integer> publication_ids){
		List<Integer> result = new ArrayList<Integer>();
		String sql = "SELECT DISTINCT person_id FROM "//
				+ dbName + ".person2publication WHERE publication_id IN ";
		
		StringBuilder sb = new StringBuilder();
		sb.append(sql);
		sb.append("(");
		for(int i=0; i<publication_ids.size(); i++) {
			sb.append("?");
			if(i < publication_ids.size()-1)
				sb.append(",");
		}
		sb.append(")");
		
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(sb.toString());
			for(int i=0; i<publication_ids.size(); i++) {
				ps.setInt(i+1, publication_ids.get(i));
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
	
	public List<Integer> getPublicationIdsByPersonId(Integer person_id){
		if(null == person_id) {
			return new ArrayList<Integer>();
		}
		List<Integer> result = new ArrayList<Integer>();
		String sql = "SELECT DISTINCT publication_id FROM "//
				+ dbName + ".person2publication WHERE person_id = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, person_id);
			rs = ps.executeQuery();
			while (rs.next()) {
				result.add(rs.getInt("publication_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		return result;
	}
	
	public List<Integer> getPublicationIdsByPersonIds(List<Integer> person_ids){
		List<Integer> result = new ArrayList<Integer>();
		String sql = "SELECT DISTINCT publication_id FROM "//
				+ dbName + ".person2publication WHERE person_id IN ";
		
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
				result.add(rs.getInt("publication_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		return result;
	}
	
	private void _construct(PreparedStatement ps, Person2Publication pp)
			throws SQLException {
		int count = 1;
		ps.setInt(count++, pp.person_id);
		ps.setInt(count++, pp.publication_id);
		ps.setInt(count++, pp.position);
	}

	private Person2Publication _construct(ResultSet rs) throws SQLException {
		Person2Publication result = new Person2Publication();
		result.id = rs.getInt("id");
		result.person_id = rs.getInt("person_id");
		result.publication_id = rs.getInt("publication_id");
		result.position = rs.getInt("position");
		return result;
	}
}
