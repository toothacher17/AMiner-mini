package SchoolSearch.services.dao.schooltest.model;


public class SchooltestPerson2graduatepublication {
	Integer id;
	Integer person_id;
	Integer graduatepublication_id;
	String type;
	Integer position;

	public SchooltestPerson2graduatepublication() {
	}

	public SchooltestPerson2graduatepublication(Integer id,Integer person_id,Integer graduatepublication_id,String type,Integer position) {
		this.id = id;
		this.person_id = person_id;
		this.graduatepublication_id = graduatepublication_id;
		this.type = type;
		this.position = position;
	}

	public Integer getId() {
		return this.id;
	}
	public Integer getPersonId() {
		return this.person_id;
	}
	public Integer getGraduatepublicationId() {
		return this.graduatepublication_id;
	}
	public String getType() {
		return this.type;
	}
	public Integer getPosition() {
		return this.position;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public void setPersonId(Integer person_id) {
		this.person_id = person_id;
	}
	public void setGraduatepublicationId(Integer graduatepublication_id) {
		this.graduatepublication_id = graduatepublication_id;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
}
