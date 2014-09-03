package SchoolSearch.services.dao.schooltest.model;


public class SchooltestPersonInterest {
	Integer id;
	Integer person_id;
	Integer aid;
	String interest;
	Integer year;

	public SchooltestPersonInterest() {
	}

	public SchooltestPersonInterest(Integer id,Integer person_id,Integer aid,String interest,Integer year) {
		this.id = id;
		this.person_id = person_id;
		this.aid = aid;
		this.interest = interest;
		this.year = year;
	}

	public Integer getId() {
		return this.id;
	}
	public Integer getPersonId() {
		return this.person_id;
	}
	public Integer getAid() {
		return this.aid;
	}
	public String getInterest() {
		return this.interest;
	}
	public Integer getYear() {
		return this.year;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public void setPersonId(Integer person_id) {
		this.person_id = person_id;
	}
	public void setAid(Integer aid) {
		this.aid = aid;
	}
	public void setInterest(String interest) {
		this.interest = interest;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
}
