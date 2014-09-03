package SchoolSearch.services.services.user;

import SchoolSearch.services.dao.user.model.User;

public interface UserService {
	User getLoginUser(String username, String password, boolean isEncrypted);
	
	User getCurrentUser();
}
