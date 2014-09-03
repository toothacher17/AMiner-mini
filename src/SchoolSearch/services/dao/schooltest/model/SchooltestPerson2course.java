package SchoolSearch.services.dao.schooltest.model;


public class SchooltestPerson2course {
	Integer id;
	Integer person_id;

	public SchooltestPerson2course() {
	}

	public SchooltestPerson2course(Integer id,Integer person_id) {
		this.id = id;
		this.person_id = person_id;
	}

	public Integer getId() {
		return this.id;
	}
	public Integer getPersonId() {
		return this.person_id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public void setPersonId(Integer person_id) {
		this.person_id = person_id;
	}
}
