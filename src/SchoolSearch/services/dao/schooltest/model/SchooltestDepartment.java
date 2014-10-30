package SchoolSearch.services.dao.schooltest.model;


public class SchooltestDepartment {
	Integer id;
	String name;

	public SchooltestDepartment() {
	}

	public SchooltestDepartment(Integer id,String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return this.id;
	}
	public String getName() {
		return this.name;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
}
