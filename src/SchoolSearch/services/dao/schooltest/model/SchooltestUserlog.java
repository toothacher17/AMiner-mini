package SchoolSearch.services.dao.schooltest.model;

import java.sql.Timestamp;

public class SchooltestUserlog {
	Integer id;
	Integer uid;
	String username;
	String ip;
	String state;
	String password;
	String userInfo;
	Integer type;
	Timestamp time;

	public SchooltestUserlog() {
	}

	public SchooltestUserlog(Integer id,Integer uid,String username,String ip,String state,String password,String userInfo,Integer type,Timestamp time) {
		this.id = id;
		this.uid = uid;
		this.username = username;
		this.ip = ip;
		this.state = state;
		this.password = password;
		this.userInfo = userInfo;
		this.type = type;
		this.time = time;
	}

	public Integer getId() {
		return this.id;
	}
	public Integer getUid() {
		return this.uid;
	}
	public String getUsername() {
		return this.username;
	}
	public String getIp() {
		return this.ip;
	}
	public String getState() {
		return this.state;
	}
	public String getPassword() {
		return this.password;
	}
	public String getUserInfo() {
		return this.userInfo;
	}
	public Integer getType() {
		return this.type;
	}
	public Timestamp getTime() {
		return this.time;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public void setState(String state) {
		this.state = state;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public void setTime(Timestamp time) {
		this.time = time;
	}
}
