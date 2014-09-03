package SchoolSearch.services.dao.schooltest.model;


public class SchooltestUser {
	Integer id;
	String username;
	String phone;
	String mailAddress;
	String userType;
	String password;
	public Integer getId() {
		return this.id;
	}
	public String getUsername() {
		return this.username;
	}
	public String getPhone() {
		return this.phone;
	}
	public String getMailAddress() {
		return this.mailAddress;
	}
	public String getUserType() {
		return this.userType;
	}
	public String getPassword() {
		return this.password;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
