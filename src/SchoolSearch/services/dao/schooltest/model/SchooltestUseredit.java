package SchoolSearch.services.dao.schooltest.model;


public class SchooltestUseredit {
	Integer id;
	Integer uid;
	String userName;
	String componentName;
	Integer personId;
	String field;
	String original;
	String _new;
	String ip;
	String time;
	String userType;

	public SchooltestUseredit() {
	}

	public SchooltestUseredit(Integer id,Integer uid,String userName,String componentName,Integer personId,String field,String original,String _new,String ip,String time,String userType) {
		this.id = id;
		this.uid = uid;
		this.userName = userName;
		this.componentName = componentName;
		this.personId = personId;
		this.field = field;
		this.original = original;
		this._new = _new;
		this.ip = ip;
		this.time = time;
		this.userType = userType;
	}

	public Integer getId() {
		return this.id;
	}
	public Integer getUid() {
		return this.uid;
	}
	public String getUserName() {
		return this.userName;
	}
	public String getComponentName() {
		return this.componentName;
	}
	public Integer getPersonId() {
		return this.personId;
	}
	public String getField() {
		return this.field;
	}
	public String getOriginal() {
		return this.original;
	}
	public String getNew() {
		return this._new;
	}
	public String getIp() {
		return this.ip;
	}
	public String getTime() {
		return this.time;
	}
	public String getUserType() {
		return this.userType;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	public void setPersonId(Integer personId) {
		this.personId = personId;
	}
	public void setField(String field) {
		this.field = field;
	}
	public void setOriginal(String original) {
		this.original = original;
	}
	public void setNew(String _new) {
		this._new = _new;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
}
