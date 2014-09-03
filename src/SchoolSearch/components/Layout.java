package SchoolSearch.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;

/**
 * Extend a layout to choose basic layout of the entire application.
 */
@Import( //
stack = {}, //
stylesheet = {	//
		"context:/res/css/bootstrap.css", //
		"context:/res/css/bootstrap.customize.css",	//
		"context:/res/css/footer.css"}, //
library = { "context:/res/js/jquery.js",	//
		"context:/res/js/jquery.sprintf.js",	//
		"context:/res/js/jquery.noconflict.js", //
		"context:/res/js/bootstrap.js"} //
)
@SuppressWarnings("unused")
public class Layout {

	/** The page title, for the <title> element and the <h1>element. */
	@Property
	@Parameter(required = true, defaultPrefix = BindingConstants.LITERAL, value = "message:title")
	private Block title;

	@Property
	@Parameter(defaultPrefix = BindingConstants.LITERAL, value = "false")
	private boolean fullwidth;

	/**
	 * current page string to highlight in navibar
	 */
	@Property
	@Parameter(defaultPrefix = BindingConstants.LITERAL)
	private String currentPage;

	@Property
	@Parameter
	private String query;

	@Property
	@Parameter(defaultPrefix = BindingConstants.LITERAL, value = "message:meta_keywords")
	private String meta_keywords;

	@Property
	@Parameter(defaultPrefix = BindingConstants.LITERAL, value = "message:meta_description")
	private String meta_description;

	@Property
	@Parameter(defaultPrefix = BindingConstants.LITERAL, value = "")
	private String containerCss;

	@Property
	@Parameter(value = "true")
	private boolean withSubheader;

	@Property
	boolean demoUser = false;
	
	void setupRender() {
//		User currentUserSafe = userService.getCurrentUserSafe();
//		if(null != currentUserSafe) {
//			Set<String> allRoles = currentUserSafe.getAllRoles();
//			for(String roles : allRoles) {
//				if(roles.equalsIgnoreCase("demo")) {
//					demoUser = true;
//				}
//			}
//			
//		}
	}
	
	void afterRender() {
	}
	
//	@Inject
//	UserService userService;
}
