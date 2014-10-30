package SchoolSearch.services.dao.schooltest.model;


public class SchooltestPublication {
	Integer id;
	String title;
	String jconf;
	String year;
	String authors;
	String type;
	String institute_key;
	String url;
	String keywords;
	String source;

	public SchooltestPublication() {
	}

	public SchooltestPublication(Integer id,String title,String jconf,String year,String authors,String type,String institute_key,String url,String keywords,String source) {
		this.id = id;
		this.title = title;
		this.jconf = jconf;
		this.year = year;
		this.authors = authors;
		this.type = type;
		this.institute_key = institute_key;
		this.url = url;
		this.keywords = keywords;
		this.source = source;
	}

	public Integer getId() {
		return this.id;
	}
	public String getTitle() {
		return this.title;
	}
	public String getJconf() {
		return this.jconf;
	}
	public String getYear() {
		return this.year;
	}
	public String getAuthors() {
		return this.authors;
	}
	public String getType() {
		return this.type;
	}
	public String getInstituteKey() {
		return this.institute_key;
	}
	public String getUrl() {
		return this.url;
	}
	public String getKeywords() {
		return this.keywords;
	}
	public String getSource() {
		return this.source;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setJconf(String jconf) {
		this.jconf = jconf;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public void setAuthors(String authors) {
		this.authors = authors;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setInstituteKey(String institute_key) {
		this.institute_key = institute_key;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public void setSource(String source) {
		this.source = source;
	}
}
