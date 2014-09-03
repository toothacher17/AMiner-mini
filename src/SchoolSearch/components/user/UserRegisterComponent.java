package SchoolSearch.components.user;

import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.pages.Index;
import SchoolSearch.services.services.auth.Authenticator;
import SchoolSearch.services.utils.Strings;
import SchoolSearch.tapestry.model.ExternalLink;

public class UserRegisterComponent {

	@Parameter
	String referer;
	
	@Property
	@Validate("required, minLength=3") 
	String username;
	
	@Property
	@Validate("required, minLength=3") 
	String email;
	
	@Property
	@Validate("required, minLength=3") 
	String password;
	
	@Property
	@Validate("required, minLength=3") 
	String passwordVerify;
	
	@Property
	String errorMessage;
	
	@InjectComponent
	Zone registerZone;
	
	@InjectPage
	Index indexPage;
	
	public Object onSuccessFromRegisterForm() {
		System.out.println("[Register]" + username + "\t" + password + "\t" + passwordVerify + "\t" + email);
		
		if(!email.contains("@")) {
			errorMessage = "邮箱地址格式不正确。";
			return registerZone.getBody();
		} else if(!password.equals(passwordVerify)) {
			errorMessage = "两次输入的密码不一致。";
			return registerZone.getBody();
		} else if(authenticator.register(username, password, email)) {
			if(Strings.isEmpty(referer))
				return indexPage;
			else
				return new ExternalLink(referer);
		} else {
			errorMessage = "用户名已存在。";
			return registerZone.getBody();
		}
	}
	
	
	@Inject
	Authenticator authenticator;
}
