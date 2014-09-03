package SchoolSearch.services.dao.course;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import SchoolSearch.services.dao.course.model.Person2Course;
import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.utils.db.ConnectionPool;
import SchoolSearch.services.utils.db.DBConnection;

/**
 * 
 * @author guanchengran
 *
 */
public class Person2CourseDao {
	String dbName = ConsistanceService.get("db.defaultDatabase");
	static ConnectionPool pool = ConnectionPool.getInstance();
	
	private static Person2CourseDao instance;
	
	public static Person2CourseDao getInstance(){
		if(null == instance){
			instance = new Person2CourseDao();
		}
		return instance;
	}
	
	public static Person2CourseDao getIndependentInstance(String dbName) {
		return new Person2CourseDao(dbName);
	}

	public Person2CourseDao() {
	}

	private Person2CourseDao(String dbName) {
		this.dbName = dbName;
	}
	
	public void insert(Person2Course personCourse) {
		String sql = "INSERT INTO " + dbName
				+ ".person2course (person_id, course_id, semesterId, rank) "
				+ "VALUES (?, ?, ?, ?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			_construct(ps, personCourse);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}

	public  void insertBatch(List<Person2Course> personCourseList) {
		String sql = "INSERT INTO " + dbName
				+ ".person2course (person_id, course_id, semesterId, rank) "
				+ "VALUES (?, ?, ?, ?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			int count = 1;
			for (Person2Course personCourse : personCourseList) {
				_construct(ps, personCourse);
				ps.addBatch();
				System.out.println("数据库添加插入进度为" + count/personCourseList.size());
				count ++;
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
	
	public void updateBatch(List<Person2Course> person2courseList) {
		String sql = "UPDATE " + dbName
				+ ".person2course SET person_id = ?, course_id = ? "
				+ "WHERE id = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for (Person2Course person2course : person2courseList) {
				ps.setInt(1, person2course.person_id);
				ps.setInt(2, person2course.course_id);
				ps.setInt(3, person2course.id);
				System.out.println(">>[p2c]>>" + ps);
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
		String sql = "DELETE FROM " + dbName + ".person2course WHERE id IN ";
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
			System.out.println(">>[p2c]>>" + ps);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}

	public List<Person2Course> walk(int start, int limit) {
		List<Person2Course> result = new ArrayList<Person2Course>();

		String sql = "SELECT id, person_id, course_id, semesterId, rank FROM "//
				+ dbName + ".person2course WHERE id >= ? ORDER BY id LIMIT ?";

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
	
	public List<Person2Course> walkAll() {
		List<Person2Course> result = new ArrayList<Person2Course>();
		int startid = 0;
		int limit = 500;
		while (true) {
			List<Person2Course> walk = walk(startid, limit);
			if (null == walk || walk.size() == 0)
				break;
			result.addAll(walk);
			startid = walk.get(walk.size() - 1).id + 1;
		}
		return result;
	}
	
	public List<Integer> getPersonIdsByCourseId(Integer course_id){
		List<Integer> result = new ArrayList<Integer>();
		String sql = "SELECT DISTINCT person_id FROM "//
				+ dbName + ".person2course WHERE course_id = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, course_id);
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
	
	public List<Integer> getPersonIdsByCourseIds(List<Integer> course_ids){
		List<Integer> result = new ArrayList<Integer>();
		String sql = "SELECT DISTINCT person_id FROM "//
				+ dbName + ".person2course WHERE course_id IN ";
		
		StringBuilder sb = new StringBuilder();
		sb.append(sql);
		sb.append("(");
		for(int i=0; i<course_ids.size(); i++) {
			sb.append("?");
			if(i < course_ids.size()-1)
				sb.append(",");
		}
		sb.append(")");
		
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(sb.toString());
			for(int i=0; i<course_ids.size(); i++) {
				ps.setInt(i+1, course_ids.get(i));
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
	
	public List<Integer> getCourseIdsByPersonId(Integer person_id){
		List<Integer> result = new ArrayList<Integer>();
		String sql = "SELECT DISTINCT course_id FROM "//
				+ dbName + ".person2course WHERE person_id = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, person_id);
			rs = ps.executeQuery();
			while (rs.next()) {
				result.add(rs.getInt("course_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		return result;
	}
	
	public List<Integer> getCourseIdsByPersonIds(List<Integer> person_ids){
		List<Integer> result = new ArrayList<Integer>();
		String sql = "SELECT DISTINCT course_id FROM "//
				+ dbName + ".person2course WHERE person_id IN ";
		
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
				result.add(rs.getInt("course_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		return result;
	}
	
	public List<Person2Course> getPerson2CourseByPersonIds(List<Integer> personIdList) {
		if(personIdList.size() == 0)
			return new ArrayList<Person2Course>();
		List<Person2Course> result = new ArrayList<Person2Course>();
		String sql = "SELECT id, person_id, course_id, semesterId, rank FROM "//
				+ dbName + ".person2course WHERE person_id IN ";
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

	
	private void _construct(PreparedStatement ps, Person2Course pc)
			throws SQLException {
		int count = 1;
		ps.setInt(count++, pc.person_id);
		ps.setInt(count++, pc.course_id);
		ps.setInt(count++, pc.semesterId);
		ps.setString(count++, pc.rank);
	}

	private Person2Course _construct(ResultSet rs) throws SQLException {
		Person2Course result = new Person2Course();
		result.id = rs.getInt("id");
		result.person_id = rs.getInt("person_id");
		result.course_id = rs.getInt("course_id");
		result.semesterId = rs.getInt("semesterId");
		result.rank = rs.getString("rank");
		return result;
	}
}
