package SchoolSearch.services.services.auth.impl;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Cookies;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Session;

import SchoolSearch.services.dao.user.UserDao;
import SchoolSearch.services.dao.user.model.User;
import SchoolSearch.services.services.auth.Authenticator;
import SchoolSearch.services.services.auth.UserConstants;
import SchoolSearch.services.services.userLog.UserLogService;
import SchoolSearch.services.utils.Strings;

public class AuthenticatorImpl implements Authenticator {

	@Inject
	Request request;
	
	@Inject
	RequestGlobals requestGloble;
	
	@Inject
	Cookies cookies;
	
	@Override
	public String getCurrentIp() {
		if(null == requestGloble || null == requestGloble.getHTTPServletRequest())
			return "program";
		return Strings.getIpAddr(requestGloble.getHTTPServletRequest());
	}
	
	@Override
	public User getCurrentUser() {
		Session session = request.getSession(true);
		User user = (User) session.getAttribute(UserConstants.SESSION_USER_TOKEN);
		return user;
	}

	@Override
	public User getCurrentUserAlsoFromCookie() {
		Session session = request.getSession(true);
		User user = (User) session.getAttribute(UserConstants.SESSION_USER_TOKEN);
		if (null == user) {
			String username = cookies.readCookieValue(UserConstants.COOKIE_USERNAME_TOKEN);
			if (Strings.isNotEmpty(username)) {
				String password = cookies.readCookieValue(UserConstants.COOKIE_PASSWORD_TOKEN);
				if (Strings.isNotEmpty(password)) {
					System.out.println("[cookie]" + username + "\t" + password + "\t" + System.currentTimeMillis() % 100000);
					user = userdao.getLoginUser(username, password, true);
					if (null == user) {
						cookies.removeCookieValue(UserConstants.COOKIE_USERNAME_TOKEN);
						cookies.removeCookieValue(UserConstants.COOKIE_PASSWORD_TOKEN);
					} else {
						session.setAttribute(UserConstants.SESSION_USER_TOKEN, user);
					}
				}
			}
		}
		return user;
	}
	
	@Override
	public boolean login(String username, String password, boolean remember) {
		System.out.println("[login]" + username + "\t" + password + "\t" + remember + "\t" + System.currentTimeMillis() % 100000);
		User user = userdao.getLoginUser(username, password, false);
		if (null != user) {
			Session session = request.getSession(true);
			session.setAttribute(UserConstants.SESSION_USER_TOKEN, user);
			if (remember) {
				int time = 60 * 24 * 7 * 2;
				cookies.writeCookieValue(UserConstants.COOKIE_USERNAME_TOKEN, username, time);
				cookies.writeCookieValue(UserConstants.COOKIE_PASSWORD_TOKEN, user.password, time);
			}
			userLogService.log(user.id, user.username, "login", null, null, 1);
			return true;
		}
		userLogService.log(null, null, "login", password, null, 0);
		return false;
	}
	
	@Override
	public boolean register(String username, String password, String email) {
		
		User user = userdao.getUserByUserName(username);
		if(null != user) {
			userLogService.log(null, null, "register", password, email, 0);
			return false;
		} 
			
		boolean createUser = userdao.createUser(username, password, email);
		if (createUser) {
			user = userdao.getUserByUserName(username);
			Session session = request.getSession(true);
			session.setAttribute(UserConstants.SESSION_USER_TOKEN, user);
			
			userLogService.log(user.id, user.username, "register", password, email, 1);
		}
		return createUser;
	}

	@Override
	public void logout() {
		
		Session session = request.getSession(true);
		User user = (User) session.getAttribute(UserConstants.SESSION_USER_TOKEN);
		if(null != user) {
			session.setAttribute(UserConstants.SESSION_USER_TOKEN, null);
			userLogService.log(user.id, user.username, "logout", null, null, 1);
		} 
		

		cookies.removeCookieValue(UserConstants.COOKIE_USERNAME_TOKEN);
		cookies.removeCookieValue(UserConstants.COOKIE_PASSWORD_TOKEN);
	}

	@Inject
	UserDao userdao;
	
	@Inject
	UserLogService userLogService;

	@Override
	public boolean verify(String username, String password, boolean remember) {
		System.out.println("[verify]" + username + "\t" + password + "\t" + remember + "\t" + System.currentTimeMillis() % 100000);
		User user = userdao.getLoginUser(username, password, false);
		if (null != user) {
			if (remember) {
				int time = 60 * 24 * 7 * 2;
				cookies.writeCookieValue(UserConstants.COOKIE_USERNAME_TOKEN, username, time);
				cookies.writeCookieValue(UserConstants.COOKIE_PASSWORD_TOKEN, user.password, time);
			}
			userLogService.log(user.id, user.username, "reset", null, null, 1);
			return true;
		}
		userLogService.log(null, null, "reset", password, null, 0);
		return false;
	}

	@Override
	public void upadtePassword(String username, String newPassword,
			User u, boolean remember) {
		System.out.println("[updatePassword]" + username + "\t" + u.password + "\t" + newPassword + "\t" +remember + "\t" + System.currentTimeMillis() % 100000);
		userdao.resetPass(username, newPassword, u);
	}


}
