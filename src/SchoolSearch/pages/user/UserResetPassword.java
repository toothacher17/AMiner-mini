package SchoolSearch.pages.user;

import org.apache.tapestry5.annotations.Import;

import SchoolSearch.services.services.auth.annotation.RequireLogin;

@Import (
	stylesheet = {"context:/res/css/home.css"	}
)

@RequireLogin
public class UserResetPassword {
	
	public String getTitle() {
		return "修改密码";
	}
}
