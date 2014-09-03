package SchoolSearch.tapestry.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.internal.services.LinkImpl;
import org.apache.tapestry5.internal.services.LinkSecurity;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BaseURLSource;
import org.apache.tapestry5.services.ContextPathEncoder;
import org.apache.tapestry5.services.Response;

public class ExternalLink implements Link {
	String url;
	
	public ExternalLink(String url) {
		this.url = url;
	}
	

	@Override
	public List<String> getParameterNames() {
		return new ArrayList<String>();
	}

	@Override
	public String getParameterValue(String name) {
		return null;
	}

	@Override
	public void addParameter(String parameterName, String value) {
		
	}

	@Override
	public Link addParameterValue(String parameterName, Object value) {
		return this;
	}

	@Override
	public void removeParameter(String parameterName) {
	}

	@Override
	public String getBasePath() {
		return null;
	}

	@Override
	public Link copyWithBasePath(String basePath) {
		return null;
	}

	@Override
	public String toURI() {
		return url;
	}

	@Override
	public String toRedirectURI() {
		return url;
	}

	@Override
	public String getAnchor() {
		return null;
	}

	@Override
	public void setAnchor(String anchor) {
	}

	@Override
	public String toAbsoluteURI() {
		return url;
	}

	@Override
	public String toAbsoluteURI(boolean secure) {
		return url;
	}

	@Override
	public void setSecurity(LinkSecurity newSecurity) {
	}

	@Override
	public LinkSecurity getSecurity() {
		return null;
	}

	@Override
	public String[] getParameterValues(String parameterName) {
		return new String[0];
	}
}
