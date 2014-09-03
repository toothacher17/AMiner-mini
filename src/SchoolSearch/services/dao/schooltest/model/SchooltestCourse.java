package SchoolSearch.services.dao.schooltest.model;


public class SchooltestCourse {
	Integer id;
	String courseName;
	String teacherName;
	String majorName;
	Integer semesterId;
	String semesterName;
	String url;

	public SchooltestCourse() {
	}

	public SchooltestCourse(Integer id,String courseName,String teacherName,String majorName,Integer semesterId,String semesterName,String url) {
		this.id = id;
		this.courseName = courseName;
		this.teacherName = teacherName;
		this.majorName = majorName;
		this.semesterId = semesterId;
		this.semesterName = semesterName;
		this.url = url;
	}

	public Integer getId() {
		return this.id;
	}
	public String getCourseName() {
		return this.courseName;
	}
	public String getTeacherName() {
		return this.teacherName;
	}
	public String getMajorName() {
		return this.majorName;
	}
	public Integer getSemesterId() {
		return this.semesterId;
	}
	public String getSemesterName() {
		return this.semesterName;
	}
	public String getUrl() {
		return this.url;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}
	public void setMajorName(String majorName) {
		this.majorName = majorName;
	}
	public void setSemesterId(Integer semesterId) {
		this.semesterId = semesterId;
	}
	public void setSemesterName(String semesterName) {
		this.semesterName = semesterName;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
