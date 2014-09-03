package SchoolSearch.services.dao.schooltest.model;


public class SchooltestPerson {
	Integer id;
	String name;
	String name_alias;

	public SchooltestPerson() {
	}

	public SchooltestPerson(Integer id,String name,String name_alias) {
		this.id = id;
		this.name = name;
		this.name_alias = name_alias;
	}

	public Integer getId() {
		return this.id;
	}
	public String getName() {
		return this.name;
	}
	public String getNameAlias() {
		return this.name_alias;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setNameAlias(String name_alias) {
		this.name_alias = name_alias;
	}
}
