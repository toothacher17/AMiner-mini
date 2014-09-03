package SchoolSearch.components.user;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.httpclient.RedirectException;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.internal.services.LinkSecurity;
import org.apache.tapestry5.internal.services.PageRenderLinkSourceImpl;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;

import SchoolSearch.pages.Index;
import SchoolSearch.services.services.auth.Authenticator;
import SchoolSearch.services.utils.Strings;
import SchoolSearch.tapestry.model.ExternalLink;

public class UserLoginComponent {

	@Parameter
	String referer;
	
	@Property
	@Validate("required, minLength=3") 
	String username;
	
	@Property
	@Validate("required, minLength=3") 
	String password;
	
	@Property
	Boolean rememberLogin;
	
	@Property
	String errorMessage;
	
	@InjectComponent
	Zone loginZone;
	
	@InjectPage
	Index indexPage;

	
	
//	@Inject
//	Response response;
	
	@Inject
	RequestGlobals rg;
	
	public Object onSuccessFromLoginForm() throws RedirectException {
		System.out.println("~[Login]" + username + "\t" + password + "\t" + rememberLogin + "\t" + referer);
//		String redirect = referer;
//		if(Strings.isNotEmpty(redirect) && redirect.startsWith("http")) {
//			StringBuilder sb = new StringBuilder();
//			String[] split1 = redirect.split("//");
//			String[] split2 = split1[1].split("/");
//			for(int i=1; i<split2.length; i++) {
////				sb.append(split2[i]).append("/");
//				sb.append("/").append(split2[i]);
//			}
////			sb.deleteCharAt(sb.length()-1);
//			redirect = sb.toString();
//			System.out.println(">>>>>>" + redirect );
//		}
			
		if(authenticator.login(username, password, rememberLogin)) {
			if(Strings.isEmpty(referer)) 
				return indexPage;
			else {
				return new ExternalLink(referer);
			}
		} else {
			errorMessage = "用户名或密码错误。";
			return loginZone.getBody();
		}
	}
	
	
	@Inject
	Authenticator authenticator;
}
