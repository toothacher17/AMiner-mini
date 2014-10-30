package SchoolSearch.services.dao.schooltest.model;


public class SchooltestPersonrelation {
	Integer id;
	Integer personid1;
	Integer personid2;
	Integer count;

	public SchooltestPersonrelation() {
	}

	public SchooltestPersonrelation(Integer id,Integer personid1,Integer personid2,Integer count) {
		this.id = id;
		this.personid1 = personid1;
		this.personid2 = personid2;
		this.count = count;
	}

	public Integer getId() {
		return this.id;
	}
	public Integer getPersonid1() {
		return this.personid1;
	}
	public Integer getPersonid2() {
		return this.personid2;
	}
	public Integer getCount() {
		return this.count;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public void setPersonid1(Integer personid1) {
		this.personid1 = personid1;
	}
	public void setPersonid2(Integer personid2) {
		this.personid2 = personid2;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
}
