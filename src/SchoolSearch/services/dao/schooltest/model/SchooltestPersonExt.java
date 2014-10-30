package SchoolSearch.services.dao.schooltest.model;


public class SchooltestPersonExt {
	Integer id;
	String title;

	public SchooltestPersonExt() {
	}

	public SchooltestPersonExt(Integer id,String title) {
		this.id = id;
		this.title = title;
	}

	public Integer getId() {
		return this.id;
	}
	public String getTitle() {
		return this.title;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public void setTitle(String title) {
		this.title = title;
	}
}
