package SchoolSearch.services.dao.schooltest.model;


public class SchooltestPerson2department {
	Integer id;
	Integer person_id;
	Integer department_id;

	public SchooltestPerson2department() {
	}

	public SchooltestPerson2department(Integer id,Integer person_id,Integer department_id) {
		this.id = id;
		this.person_id = person_id;
		this.department_id = department_id;
	}

	public Integer getId() {
		return this.id;
	}
	public Integer getPersonId() {
		return this.person_id;
	}
	public Integer getDepartmentId() {
		return this.department_id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public void setPersonId(Integer person_id) {
		this.person_id = person_id;
	}
	public void setDepartmentId(Integer department_id) {
		this.department_id = department_id;
	}
}
