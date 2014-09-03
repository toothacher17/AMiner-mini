package SchoolSearch.services.dao.schooltest.model;


public class SchooltestPersonTitle {
	Integer id;
	String name;
	Double score;

	public SchooltestPersonTitle() {
	}

	public SchooltestPersonTitle(Integer id,String name,Double score) {
		this.id = id;
		this.name = name;
		this.score = score;
	}

	public Integer getId() {
		return this.id;
	}
	public String getName() {
		return this.name;
	}
	public Double getScore() {
		return this.score;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setScore(Double score) {
		this.score = score;
	}
}
