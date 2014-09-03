package SchoolSearch.services.services.auth;

import SchoolSearch.services.dao.user.model.User;

public interface Authenticator {
	String getCurrentIp();
	
	User getCurrentUser();
	User getCurrentUserAlsoFromCookie();
	
	boolean login(String username, String password, boolean remember);
	
	boolean register(String username, String password, String email);
	
	boolean verify(String username, String password, boolean remember);
	
	void upadtePassword(String username, String newPassword, User u, boolean remember);
	void logout();

}
