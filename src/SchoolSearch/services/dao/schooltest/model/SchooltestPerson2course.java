package SchoolSearch.services.dao.schooltest.model;


public class SchooltestPerson2course {
	Integer id;
	Integer person_id;
	Integer label;
	Integer confirmed;

	public SchooltestPerson2course() {
	}

	public SchooltestPerson2course(Integer id,Integer person_id,Integer label,Integer confirmed) {
		this.id = id;
		this.person_id = person_id;
		this.label = label;
		this.confirmed = confirmed;
	}

	public Integer getId() {
		return this.id;
	}
	public Integer getPersonId() {
		return this.person_id;
	}
	public Integer getLabel() {
		return this.label;
	}
	public Integer getConfirmed() {
		return this.confirmed;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public void setPersonId(Integer person_id) {
		this.person_id = person_id;
	}
	public void setLabel(Integer label) {
		this.label = label;
	}
	public void setConfirmed(Integer confirmed) {
		this.confirmed = confirmed;
	}
}
