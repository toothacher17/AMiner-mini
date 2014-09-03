package SchoolSearch.services.dao.schooltest.model;


public class SchooltestSchool {
	Integer id;
	String school_name;

	public SchooltestSchool() {
	}

	public SchooltestSchool(Integer id,String school_name) {
		this.id = id;
		this.school_name = school_name;
	}

	public Integer getId() {
		return this.id;
	}
	public String getSchoolName() {
		return this.school_name;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public void setSchoolName(String school_name) {
		this.school_name = school_name;
	}
}
