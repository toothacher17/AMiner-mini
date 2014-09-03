package SchoolSearch.services.dao.schooltest.model;


public class SchooltestPerson2publication {
	Integer id;
	Integer publication_id;
	Integer person_id;
	Integer position;

	public SchooltestPerson2publication() {
	}

	public SchooltestPerson2publication(Integer id,Integer publication_id,Integer person_id,Integer position) {
		this.id = id;
		this.publication_id = publication_id;
		this.person_id = person_id;
		this.position = position;
	}

	public Integer getId() {
		return this.id;
	}
	public Integer getPublicationId() {
		return this.publication_id;
	}
	public Integer getPersonId() {
		return this.person_id;
	}
	public Integer getPosition() {
		return this.position;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public void setPublicationId(Integer publication_id) {
		this.publication_id = publication_id;
	}
	public void setPersonId(Integer person_id) {
		this.person_id = person_id;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
}
