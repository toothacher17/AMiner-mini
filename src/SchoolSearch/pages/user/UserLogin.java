package SchoolSearch.pages.user;

import org.apache.tapestry5.annotations.ActivationRequestParameter;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import SchoolSearch.services.monitor.MonitorService;

@Import (
	stylesheet = {"context:/res/css/home.css"	}
)

public class UserLogin {

	@Persist
	@Property
	@ActivationRequestParameter("referer")
	String referer;
	
	@Inject
	Request request;
	
	
	@SetupRender
	void setupRender() {
		referer = request.getHeader("Referer");
		monitor.visitPage("UserLogin", null);
	}
	
	public String getTitle() {
		return "用户登录";
	}
	
	@Inject
	private MonitorService monitor;
}
