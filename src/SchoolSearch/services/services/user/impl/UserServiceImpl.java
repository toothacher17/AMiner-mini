package SchoolSearch.services.services.user.impl;

import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.dao.user.UserDao;
import SchoolSearch.services.dao.user.model.User;
import SchoolSearch.services.services.auth.Authenticator;
import SchoolSearch.services.services.user.UserService;

public class UserServiceImpl implements UserService {

	@Override
	public User getLoginUser(String username, String password, boolean isEncrypted) {
		return userdao.getLoginUser(username, password, isEncrypted);
	}
	
	@Override
	public User getCurrentUser() {
		return authenticator.getCurrentUser();
	}
	
	@Inject
	UserDao userdao;
	
	@Inject
	Authenticator authenticator;
}
