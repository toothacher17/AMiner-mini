package SchoolSearch.services.services.auth.annotation.filter;

import java.io.IOException;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.runtime.Component;
import org.apache.tapestry5.services.ComponentEventRequestParameters;
import org.apache.tapestry5.services.ComponentRequestFilter;
import org.apache.tapestry5.services.ComponentRequestHandler;
import org.apache.tapestry5.services.ComponentSource;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.PageRenderRequestParameters;
import org.apache.tapestry5.services.Response;

import SchoolSearch.services.dao.user.model.User;
import SchoolSearch.services.services.auth.annotation.RequireLogin;
import SchoolSearch.services.services.user.UserService;

public class RequireLoginFilter implements ComponentRequestFilter {

	public RequireLoginFilter(PageRenderLinkSource renderLinkSource, ComponentSource componentSource,
			Response response, UserService userService) {
		this.renderLinkSource = renderLinkSource;
		this.componentSource = componentSource;
		this.response = response;
		this.userService = userService;
	}

	public void handleComponentEvent(ComponentEventRequestParameters parameters, ComponentRequestHandler handler)
			throws IOException {

		if (dispatchedToLoginPage(parameters.getActivePageName())) {
			return;
		}

		handler.handleComponentEvent(parameters);
	}

	public void handlePageRender(PageRenderRequestParameters parameters, ComponentRequestHandler handler)
			throws IOException {

		if (dispatchedToLoginPage(parameters.getLogicalPageName())) {
			return;
		}
		handler.handlePageRender(parameters);
	}

	/**
	 * FIXME heavey performance issue.
	 * 
	 * @param pageName
	 * @return false to go through the chain.
	 * @throws IOException
	 */
	private boolean dispatchedToLoginPage(String pageName) throws IOException {
		// System.err.println(">> + dispatch page + >>" + pageName);

		Component page = componentSource.getPage(pageName);
		if (!page.getClass().isAnnotationPresent(RequireLogin.class)) {
			// not annotated with RequireLoing, go through the chain.
			return false;
		}

		RequireLogin annotation = page.getClass().getAnnotation(RequireLogin.class);
		User user = userService.getCurrentUser();

		String indexPage, loginPage;
		if (annotation.admin()) {
			indexPage = this.defaultIndexPage;
			loginPage = this.defaultLoginPage;
		} else {
			indexPage = this.adminDefaultIndexPage;
			loginPage = this.adminDefaultLoginPage;
		}

		if (loginPage.equalsIgnoreCase(pageName)) {
			if (null != user) {
				// Logged user should not go back to Signin or Signup
				return redirect(indexPage);
			} else {
				return false;
			}
		}

		// here to check privileges
		String[] roles = annotation.roles();
		if (annotation.admin()) {
			roles = new String[] { "admin" };
		}
		boolean pass = false;
		if (null == user) {
			pass = false;
		} else {
			if (null == roles || roles.length == 0) {
				pass = true;
			} else {
				for (String role : roles) {
					if (user.existGroup(role)) {
						pass = true;
						break;
					}
				}
			}
		}
		if (!pass) {
			return redirectWithReturnpage(loginPage, pageName);
		}
		return false;
	}

	private boolean redirect(String redirectPage) throws IOException {
		Link link = renderLinkSource.createPageRenderLink(redirectPage);
		response.sendRedirect(link);
		return true;
	}

	private boolean redirectWithReturnpage(String redirectPage, String returnPage) throws IOException {
		Link link = renderLinkSource.createPageRenderLinkWithContext(redirectPage, returnPage);
		response.sendRedirect(link);
		return true;
	}

	/*
	 * Services
	 */
	private final PageRenderLinkSource renderLinkSource;

	private final ComponentSource componentSource;

	private final Response response;

	private final UserService userService;

//	@Inject
//	@Symbol("privileges.default_index_page")
	private String defaultIndexPage = "index";

//	@Inject
//	@Symbol("privileges.default_login_page")
	private String defaultLoginPage = "user/login";

//	@Inject
//	@Symbol("privileges.admin_index_page")
	private String adminDefaultIndexPage = "index";

//	@Inject
//	@Symbol("privileges.admin_login_page")
	private String adminDefaultLoginPage = "user/login";

}
