package SchoolSearch.services.dao.schooltest.model;


public class SchooltestCourseDetail {
	Integer id;
	Integer courseId;
	String courseName;
	String courseNo;
	String teacherId;
	String teacherName;
	String majorId;
	String majorName;
	Integer browseTimes;
	Integer semesterId;
	String semesterName;
	String openType;
	Integer studentNum;
	String sex;
	String position;
	String duty;
	String telephone;
	String email;
	String address;
	String postcode;
	String personabstract;
	String courseDescription;
	String teachingMaterial;
	Integer credit;
	Integer class_hour;
	String checking;
	Integer teachingMaterialNum;
	Integer paperNum;
	String homework;
	String url;

	public SchooltestCourseDetail() {
	}

	public SchooltestCourseDetail(Integer id,Integer courseId,String courseName,String courseNo,String teacherId,String teacherName,String majorId,String majorName,Integer browseTimes,Integer semesterId,String semesterName,String openType,Integer studentNum,String sex,String position,String duty,String telephone,String email,String address,String postcode,String personabstract,String courseDescription,String teachingMaterial,Integer credit,Integer class_hour,String checking,Integer teachingMaterialNum,Integer paperNum,String homework,String url) {
		this.id = id;
		this.courseId = courseId;
		this.courseName = courseName;
		this.courseNo = courseNo;
		this.teacherId = teacherId;
		this.teacherName = teacherName;
		this.majorId = majorId;
		this.majorName = majorName;
		this.browseTimes = browseTimes;
		this.semesterId = semesterId;
		this.semesterName = semesterName;
		this.openType = openType;
		this.studentNum = studentNum;
		this.sex = sex;
		this.position = position;
		this.duty = duty;
		this.telephone = telephone;
		this.email = email;
		this.address = address;
		this.postcode = postcode;
		this.personabstract = personabstract;
		this.courseDescription = courseDescription;
		this.teachingMaterial = teachingMaterial;
		this.credit = credit;
		this.class_hour = class_hour;
		this.checking = checking;
		this.teachingMaterialNum = teachingMaterialNum;
		this.paperNum = paperNum;
		this.homework = homework;
		this.url = url;
	}

	public Integer getId() {
		return this.id;
	}
	public Integer getCourseId() {
		return this.courseId;
	}
	public String getCourseName() {
		return this.courseName;
	}
	public String getCourseNo() {
		return this.courseNo;
	}
	public String getTeacherId() {
		return this.teacherId;
	}
	public String getTeacherName() {
		return this.teacherName;
	}
	public String getMajorId() {
		return this.majorId;
	}
	public String getMajorName() {
		return this.majorName;
	}
	public Integer getBrowseTimes() {
		return this.browseTimes;
	}
	public Integer getSemesterId() {
		return this.semesterId;
	}
	public String getSemesterName() {
		return this.semesterName;
	}
	public String getOpenType() {
		return this.openType;
	}
	public Integer getStudentNum() {
		return this.studentNum;
	}
	public String getSex() {
		return this.sex;
	}
	public String getPosition() {
		return this.position;
	}
	public String getDuty() {
		return this.duty;
	}
	public String getTelephone() {
		return this.telephone;
	}
	public String getEmail() {
		return this.email;
	}
	public String getAddress() {
		return this.address;
	}
	public String getPostcode() {
		return this.postcode;
	}
	public String getPersonabstract() {
		return this.personabstract;
	}
	public String getCourseDescription() {
		return this.courseDescription;
	}
	public String getTeachingMaterial() {
		return this.teachingMaterial;
	}
	public Integer getCredit() {
		return this.credit;
	}
	public Integer getClassHour() {
		return this.class_hour;
	}
	public String getChecking() {
		return this.checking;
	}
	public Integer getTeachingMaterialNum() {
		return this.teachingMaterialNum;
	}
	public Integer getPaperNum() {
		return this.paperNum;
	}
	public String getHomework() {
		return this.homework;
	}
	public String getUrl() {
		return this.url;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public void setCourseId(Integer courseId) {
		this.courseId = courseId;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public void setCourseNo(String courseNo) {
		this.courseNo = courseNo;
	}
	public void setTeacherId(String teacherId) {
		this.teacherId = teacherId;
	}
	public void setTeacherName(String teacherName) {
		this.teacherName = teacherName;
	}
	public void setMajorId(String majorId) {
		this.majorId = majorId;
	}
	public void setMajorName(String majorName) {
		this.majorName = majorName;
	}
	public void setBrowseTimes(Integer browseTimes) {
		this.browseTimes = browseTimes;
	}
	public void setSemesterId(Integer semesterId) {
		this.semesterId = semesterId;
	}
	public void setSemesterName(String semesterName) {
		this.semesterName = semesterName;
	}
	public void setOpenType(String openType) {
		this.openType = openType;
	}
	public void setStudentNum(Integer studentNum) {
		this.studentNum = studentNum;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public void setDuty(String duty) {
		this.duty = duty;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	public void setPersonabstract(String personabstract) {
		this.personabstract = personabstract;
	}
	public void setCourseDescription(String courseDescription) {
		this.courseDescription = courseDescription;
	}
	public void setTeachingMaterial(String teachingMaterial) {
		this.teachingMaterial = teachingMaterial;
	}
	public void setCredit(Integer credit) {
		this.credit = credit;
	}
	public void setClassHour(Integer class_hour) {
		this.class_hour = class_hour;
	}
	public void setChecking(String checking) {
		this.checking = checking;
	}
	public void setTeachingMaterialNum(Integer teachingMaterialNum) {
		this.teachingMaterialNum = teachingMaterialNum;
	}
	public void setPaperNum(Integer paperNum) {
		this.paperNum = paperNum;
	}
	public void setHomework(String homework) {
		this.homework = homework;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
