package SchoolSearch.components.user;

import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.pages.Index;
import SchoolSearch.services.dao.user.model.User;
import SchoolSearch.services.services.auth.Authenticator;
import SchoolSearch.services.services.auth.annotation.RequireLogin;

public class UserResetPasswordComponent {
	@Property
	@Validate("required, minLength=3")
	String passwordOriginal;
	
	@Property
	@Validate("required, minLength=3") 
	String password;
	
	@Property
	@Validate("required, minLength=3") 
	String passwordVerify;
	
	@Property
	String errorMessage;
	
	@Property
	String successMessage;
	
	@InjectComponent
	Zone resetPasswordZone;
	
	@InjectPage
	Index indexPage;
	
	public Object onSuccessFromResetPasswordForm() {
		User u = authenticator.getCurrentUser();
		System.out.println("[ResetPassword]" + passwordOriginal + "\t" + password + "\t" + passwordVerify + "\t" );
		if(!password.equals(passwordVerify)){
			errorMessage = "两次输入的密码不一致";
			return resetPasswordZone.getBody();
		}else if(authenticator.verify(u.username, passwordOriginal, true)){
			authenticator.upadtePassword(u.username, password, u, true);
			authenticator.logout();
			errorMessage = null;
			successMessage = "修改成功";
			return resetPasswordZone.getBody();
		}else{
			errorMessage = "原始密码不正确";
			return resetPasswordZone.getBody();
		}
	}
	
	@Inject
	Authenticator authenticator;
}
