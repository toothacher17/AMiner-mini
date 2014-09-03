package SchoolSearch.services.dao.schooltest.model;


public class SchooltestDepartment2school {
	Integer id;
	Integer school_id;

	public SchooltestDepartment2school() {
	}

	public SchooltestDepartment2school(Integer id,Integer school_id) {
		this.id = id;
		this.school_id = school_id;
	}

	public Integer getId() {
		return this.id;
	}
	public Integer getSchoolId() {
		return this.school_id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public void setSchoolId(Integer school_id) {
		this.school_id = school_id;
	}
}
