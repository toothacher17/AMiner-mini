package SchoolSearch.services.dao.course;
/**
 * @author CX
 */
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import SchoolSearch.services.dao.course.model.Course;
import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.services.person.PersonService;
import SchoolSearch.services.utils.db.ConnectionPool;
import SchoolSearch.services.utils.db.DBConnection;

public class CourseDao {
	private static CourseDao instance;
	String dbName = ConsistanceService.get("db.defaultDatabase");
	static ConnectionPool pool = ConnectionPool.getInstance();
	@Inject
	PersonService personService;
	
	public static CourseDao getInstance(){
		if(null == instance){
			instance = new CourseDao();
		}
		return instance;
	}
	
	public static CourseDao getIndependentInstance(String dbName) {
		return new CourseDao(dbName);
	}

	public CourseDao() {
	}

	private CourseDao(String dbName) {
		this.dbName = dbName;
	}
	
	public void insert(Course course) {
		String sql = "INSERT INTO "
				+ dbName
				+ ".course (courseId, courseName, courseNo, "
				+ "teacherId, teacherName, majorId, majorName, browseTimes, semesterId, "
				+ "semesterName, openType, studentNum, sex, position, duty, telephone, email, "
				+ "address, postcode, personabstract, courseDescription, teachingMaterial, "
				+ "credit, class_hour, checking, teachingMaterialNum, paperNum, homework, url) "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			_construct(ps, course);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}

	public  void insertBatch(List<Course> courseList) {
		String sql = "INSERT INTO "
				+ dbName
				+ ".course (courseId, courseName, courseNo, "
				+ "teacherId, teacherName, majorId, majorName, browseTimes, semesterId, "
				+ "semesterName, openType, studentNum, sex, position, duty, telephone, email, "
				+ "address, postcode, personabstract, courseDescription, teachingMaterial, "
				+ "credit, class_hour, checking, teachingMaterialNum, paperNum, homework, url) "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for (Course course : courseList) {
				_construct(ps, course);
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

	public  List<Course> walk(Integer start, int limit) {

		List<Course> result = new ArrayList<Course>();
		
		String sql = "SELECT id, courseId, courseName, courseNo, "
				+ "teacherId, teacherName, majorId, majorName, browseTimes, semesterId, "
				+ "semesterName, openType, studentNum, sex, position, duty, telephone, email, "
				+ "address, postcode, personabstract, courseDescription, teachingMaterial, "
				+ "credit, class_hour, checking, teachingMaterialNum, paperNum, homework, url FROM "
				+ dbName + ".course WHERE id >= ? ORDER BY id LIMIT ?";

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sql);
			ps.setLong(1, start);
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

	public  List<Course> walkAll() {
		List<Course> result = new ArrayList<Course>();
		int startid = 0;
		int limit = 500;
		while (true) {
			List<Course> walk = walk(startid, limit);
			if (null == walk || walk.size() == 0)
				break;
			result.addAll(walk);
			startid = walk.get(walk.size() - 1).id + 1;
		}
		return result;
	}

	public Course getCourseById(Integer id){
		String sql = "SELECT id, courseId, courseName, courseNo, "
				+ "teacherId, teacherName, majorId, majorName, browseTimes, semesterId, "
				+ "semesterName, openType, studentNum, sex, position, duty, telephone, email, "
				+ "address, postcode, personabstract, courseDescription, teachingMaterial, "
				+ "credit, class_hour, checking, teachingMaterialNum, paperNum, homework, url FROM "
				+ dbName + ".course WHERE id = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		 Course result = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				result  = _construct(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		return result;
	}
		
	public List<Course> getCoursesByIdList(List<Integer> ids){
		
		if(ids.size() == 0) {
			return new ArrayList<Course>();
		}
		
		List<Course> result = new ArrayList<Course>();
		
		String sql = "SELECT id, courseId, courseName, courseNo, "
				+ "teacherId, teacherName, majorId, majorName, browseTimes, semesterId, "
				+ "semesterName, openType, studentNum, sex, position, duty, telephone, email, "
				+ "address, postcode, personabstract, courseDescription, teachingMaterial, "
				+ "credit, class_hour, checking, teachingMaterialNum, paperNum, homework, url FROM "
				+ dbName + ".course WHERE id IN";
		
		StringBuilder sb = new StringBuilder();
		sb.append(sql);
		sb.append("(");
		for(int i=0; i<ids.size(); i++) {
			sb.append("?");
			if(i < ids.size() -1) {
				sb.append(",");
			}
		}
		sb.append(")");
		
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(sb.toString());
			for(int i=0; i<ids.size(); i++) {
				ps.setInt(i+1, ids.get(i));
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
	
	
	private  void _construct(PreparedStatement ps, Course c)
			throws SQLException {
		int count = 1;
		__safeSetInt(ps, count++, c.courseId);
		__safeSetString(ps,count++, c.courseName);
		__safeSetString(ps,count++, c.courseNo);
		__safeSetString(ps,count++, c.teacherId);
		__safeSetString(ps,count++, c.teacherName);
		__safeSetString(ps,count++, c.majorId);
		__safeSetString(ps,count++, c.majorName);
		__safeSetInt(ps, count++, c.browseTimes);
		__safeSetInt(ps, count++, c.semesterId);
		__safeSetString(ps,count++, c.semesterName);
		__safeSetString(ps,count++, c.openType);
		__safeSetInt(ps, count++, c.studentNum);
		
		__safeSetString(ps,count++, c.sex);
		__safeSetString(ps,count++, c.position);
		__safeSetString(ps,count++, c.duty);
		__safeSetString(ps,count++, c.telephone);
		__safeSetString(ps,count++, c.email);
		__safeSetString(ps,count++, c.address);
		__safeSetInt(ps, count++, c.postcode);
		__safeSetString(ps,count++, c.personProfile);
		__safeSetString(ps,count++, c.courseDescription);
		__safeSetString(ps,count++, c.teachingMaterial);
		__safeSetInt(ps, count++, c.credit);
		__safeSetInt(ps, count++, c.class_hour);
		__safeSetString(ps,count++, c.checking);
		__safeSetInt(ps, count++, c.teachingMaterialNum);
		__safeSetInt(ps, count++, c.paperNum);
		__safeSetString(ps,count++, c.homework);
		__safeSetString(ps,count++, c.url);
	}

	private  Course _construct(ResultSet rs) throws SQLException {
		Course result = new Course();
		result.id = rs.getInt("id");
		result.courseId = rs.getInt("courseId");
		result.courseName = rs.getString("courseName");
		result.courseNo = rs.getString("courseNo");
		result.teacherId = rs.getString("teacherId");
		result.teacherName = rs.getString("teacherName");
		result.majorId = rs.getString("majorId");
		result.majorName = rs.getString("majorName");
		result.browseTimes = rs.getInt("browseTimes");
		result.semesterId = rs.getInt("semesterId");
		result.semesterName = rs.getString("semesterName");
		result.openType = rs.getString("openType");
		result.studentNum = rs.getInt("studentNum");
		
		result.sex = rs.getString("sex");
		result.position = rs.getString("position");
		result.duty = rs.getString("duty");
		result.telephone = rs.getString("telephone");
		result.email = rs.getString("email");
		result.address = rs.getString("address");
		result.postcode = rs.getInt("postcode");
		result.personProfile = rs.getString("personabstract");
		result.courseDescription = rs.getString("courseDescription");
		result.teachingMaterial = rs.getString("teachingMaterial");
		result.credit = rs.getInt("credit");
		result.class_hour = rs.getInt("class_hour");
		result.checking = rs.getString("checking");
		result.teachingMaterialNum = rs.getInt("teachingMaterialNum");
		result.paperNum = rs.getInt("paperNum");
		result.homework = rs.getString("homework");
		result.url = rs.getString("url");
		
		return result;
	}
	
	private static void __safeSetString(PreparedStatement ps,
			int parameterIndex, String value) throws SQLException {
		if (null == value)
			ps.setNull(parameterIndex, Types.VARCHAR);
		else
			ps.setString(parameterIndex, value);
	}

	private static void __safeSetInt(PreparedStatement ps, int parameterIndex,
			Integer value) throws SQLException {
		if (value == null)
			ps.setNull(parameterIndex, Types.INTEGER);
		else
			ps.setInt(parameterIndex, value);
	}
	
}
