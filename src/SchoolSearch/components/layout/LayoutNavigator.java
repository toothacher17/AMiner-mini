package SchoolSearch.components.layout;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.dao.user.model.User;
import SchoolSearch.services.services.auth.Authenticator;
import SchoolSearch.services.services.auth.UserConstants;

public class LayoutNavigator {

	@Property
	User user;
	
	@SetupRender
	Object setupRender() {
		user = authenticator.getCurrentUserAlsoFromCookie();
		return true;
	}
	
	void onActionFromSignOut() {
		authenticator.logout();
	}
	
	public boolean isAdmin() {
		if(null!=user) {
			return user.existGroup(UserConstants.ROLE_ADMIN);
		}
		return false;
	}
	
	@Inject
	Authenticator authenticator;
}
