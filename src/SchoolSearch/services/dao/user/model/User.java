package SchoolSearch.services.dao.user.model;

import SchoolSearch.services.services.auth.UserConstants;
import SchoolSearch.services.utils.Strings;

public class User {

	
	
	public User() {
		super();
	}

	public User(String username, String password, String userType, String mailAddress) {
		super();
		this.username = username;
		this.password = password;
		this.userType = userType;
		this.mailAddress = mailAddress;
	}


	public int id;
	public String username;
	public String password;
	public String userType;
	public String mailAddress;
	public String phone;

	
	public boolean existGroup(String groupName) {
		groupName = groupName.trim().toLowerCase();
		if(Strings.isEmpty(userType))
			return false;
		String[] split = userType.split("[,; ]");
		boolean exist = false;
		for(String s : split) {
			s = s.trim().toLowerCase();
			if(s.equals(groupName)) {
				exist = true;
				break;
			}
		}
		return exist;
	}
	
	public boolean isAdmin() {
		return existGroup(UserConstants.ROLE_ADMIN);
	}
}
