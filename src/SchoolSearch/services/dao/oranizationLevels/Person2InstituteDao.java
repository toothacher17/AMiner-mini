package SchoolSearch.services.dao.oranizationLevels;
/**
 * @author GCR
 */
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.dao.oranizationLevels.model.Person2Institute;
import SchoolSearch.services.utils.db.ConnectionPool;
import SchoolSearch.services.utils.db.DBConnection;

public class Person2InstituteDao {
	String dbName = ConsistanceService.get("db.defaultDatabase");
	static ConnectionPool pool = ConnectionPool.getInstance();
	
	private static Person2InstituteDao instance;
	
	public static Person2InstituteDao getInstance(){
		if(null == instance){
			instance = new Person2InstituteDao();
		}
		return instance;
	}
	
	public static Person2InstituteDao getIndependentInstance(String dbName) {
		return new Person2InstituteDao(dbName);
	}

	public Person2InstituteDao() {
	}

	private Person2InstituteDao(String dbName) {
		this.dbName = dbName;
	}
	
	public void insert(Person2Institute personInstitute) {
		String sql = "INSERT INTO " + dbName
				+ ".person2institute (person_id, institute_id) "
				+ "VALUES (?, ?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			_constructInsert(ps, personInstitute);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}

	public  void insertBatch(List<Person2Institute> personInstituteList) {
		String sql = "INSERT INTO " + dbName
				+ ".person2institute (person_id, institute_id) "
				+ "VALUES (?, ?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for (Person2Institute personInstitute : personInstituteList) {
				_constructInsert(ps, personInstitute);
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
	
	public void updateBatch(List<Person2Institute> personInstituteList) {
		String sql = "UPDATE " + dbName
				+ ".person2institute SET person_id = ?, institute_id = ? "
				+ "WHERE id = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for (Person2Institute personInstitute : personInstituteList) {
				ps.setInt(1, personInstitute.person_id);
				ps.setInt(2, personInstitute.institute_id);
				ps.setInt(3, personInstitute.id);
				System.out.println(">>[p2i]>>" + ps);
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
		String sql = "DELETE FROM " + dbName + ".person2institute WHERE id IN ";
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
			System.out.println(">>[p2i]>>" + ps);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}
	
	public List<Person2Institute> walk(int start, int limit) {
		List<Person2Institute> result = new ArrayList<Person2Institute>();

		String sql = "SELECT id, person_id, institute_id FROM "//
				+ dbName + ".person2institute WHERE id >= ? ORDER BY id LIMIT ?";

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
	
	
	
	public List<Person2Institute> walkAll() {
		List<Person2Institute> result = new ArrayList<Person2Institute>();
		int startid = 0;
		int limit = 500;
		while (true) {
			List<Person2Institute> walk = walk(startid, limit);
			if (null == walk || walk.size() == 0)
				break;
			result.addAll(walk);
			startid = walk.get(walk.size() - 1).id + 1;
		}
		return result;
	}
	
	public List<Integer> getPersonIdsByInstituteId(int institute_id){
		List<Integer> result = new ArrayList<Integer>();
		String sql = "SELECT DISTINCT person_id FROM "//
				+ dbName + ".person2institute WHERE institute_id = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, institute_id);
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
	
	public List<Integer> getPersonIDsByInstituteIds(List<Integer> instituteIDs) {
		if(instituteIDs.size() == 0)
			return new ArrayList<Integer>();
		List<Integer> result = new ArrayList<Integer>();
		String sql = "SELECT DISTINCT person_id FROM "//
				+ dbName + ".person2institute WHERE institute_id IN ";
		StringBuilder sb = new StringBuilder();
		sb.append(sql);
		sb.append("(");
		for(int i=0; i<instituteIDs.size(); i++) {
			sb.append("?");
			if(i < instituteIDs.size()-1)
				sb.append(",");
		}
		sb.append(")");
		
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(sb.toString());
			for(int i=0; i<instituteIDs.size(); i++) {
				ps.setInt(i+1, instituteIDs.get(i));
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
	
	public List<Person2Institute> getPerson2InstituteByInstituteIds(List<Integer> instituteIDs) {
		if(instituteIDs.size() == 0)
			return new ArrayList<Person2Institute>();
		List<Person2Institute> result = new ArrayList<Person2Institute>();
		String sql = "SELECT id, person_id, institute_id FROM "//
				+ dbName + ".person2institute WHERE institute_id IN ";
		StringBuilder sb = new StringBuilder();
		sb.append(sql);
		sb.append("(");
		for(int i=0; i<instituteIDs.size(); i++) {
			sb.append("?");
			if(i < instituteIDs.size()-1)
				sb.append(",");
		}
		sb.append(")");
		
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(sb.toString());
			for(int i=0; i<instituteIDs.size(); i++) {
				ps.setInt(i+1, instituteIDs.get(i));
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
	
	public List<Person2Institute> getPerson2InstituteByPersonIds(List<Integer> personIdList) {
		if(personIdList.size() == 0)
			return new ArrayList<Person2Institute>();
		List<Person2Institute> result = new ArrayList<Person2Institute>();
		String sql = "SELECT id, person_id, institute_id FROM "//
				+ dbName + ".person2institute WHERE person_id IN ";
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
	
	public List<Integer> getInstituteIdsByPersonId(int person_id){
		List<Integer> result = new ArrayList<Integer>();
		String sql = "SELECT institute_id FROM "//
				+ dbName + ".person2institute WHERE person_id = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, person_id);
			rs = ps.executeQuery();
			while (rs.next()) {
				result.add(rs.getInt("institute_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		return result;
	}
	
	@Deprecated
	public Set<Integer> getInstituteIdsByPersonIds(Set<Integer> person_ids){
		Set<Integer> result = new HashSet<Integer>();
		for(Integer p_id:person_ids){
			result.addAll(getInstituteIdsByPersonId(p_id.intValue()));
		}
		return result;
	}
	
	private void _constructInsert(PreparedStatement ps, Person2Institute pi)
			throws SQLException {
		int count = 1;
		ps.setInt(count++, pi.person_id);
		ps.setInt(count++, pi.institute_id);
	}

	private Person2Institute _construct(ResultSet rs) throws SQLException {
		Person2Institute result = new Person2Institute();
		result.id = rs.getInt("id");
		result.person_id = rs.getInt("person_id");
		result.institute_id = rs.getInt("institute_id");

		return result;
	}
	
//	public static void main(String args[]) {
//		Person2InstituteDao p2i = new Person2InstituteDao();
//		List<Integer> personIds = new ArrayList<Integer>();
//		personIds.add(2301);
//		personIds.add(2336);
//		List<Person2Institute> list = p2i.getPerson2InstituteByPersonIds(personIds);
//		for(Person2Institute p : list) {
//			System.out.println(p.id);
//		}
//	}
}
