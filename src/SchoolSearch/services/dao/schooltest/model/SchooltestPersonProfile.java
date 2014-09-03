package SchoolSearch.services.dao.schooltest.model;


public class SchooltestPersonProfile {
	Integer id;
	String No;
	String position;
	Integer department_id;
	String department_key;
	String location;
	String phone;
	String email;
	String homepage;
	String imagelink;
	Integer author_id;

	public SchooltestPersonProfile() {
	}

	public SchooltestPersonProfile(Integer id,String No,String position,Integer department_id,String department_key,String location,String phone,String email,String homepage,String imagelink,Integer author_id) {
		this.id = id;
		this.No = No;
		this.position = position;
		this.department_id = department_id;
		this.department_key = department_key;
		this.location = location;
		this.phone = phone;
		this.email = email;
		this.homepage = homepage;
		this.imagelink = imagelink;
		this.author_id = author_id;
	}

	public Integer getId() {
		return this.id;
	}
	public String getNo() {
		return this.No;
	}
	public String getPosition() {
		return this.position;
	}
	public Integer getDepartmentId() {
		return this.department_id;
	}
	public String getDepartmentKey() {
		return this.department_key;
	}
	public String getLocation() {
		return this.location;
	}
	public String getPhone() {
		return this.phone;
	}
	public String getEmail() {
		return this.email;
	}
	public String getHomepage() {
		return this.homepage;
	}
	public String getImagelink() {
		return this.imagelink;
	}
	public Integer getAuthorId() {
		return this.author_id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public void setNo(String No) {
		this.No = No;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public void setDepartmentId(Integer department_id) {
		this.department_id = department_id;
	}
	public void setDepartmentKey(String department_key) {
		this.department_key = department_key;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}
	public void setImagelink(String imagelink) {
		this.imagelink = imagelink;
	}
	public void setAuthorId(Integer author_id) {
		this.author_id = author_id;
	}
}
