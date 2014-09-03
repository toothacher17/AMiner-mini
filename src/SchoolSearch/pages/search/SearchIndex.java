package SchoolSearch.pages.search;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.ActivationRequestParameter;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.monitor.MonitorService;

@Import(stylesheet = {"context:/res/css/pages/person/person.css"})
public class SearchIndex {
	
	@Persist
	@Property
	@ActivationRequestParameter("query")
	String query;

	public Object activate(String query) {
		this.query = query;
		return this;
	}
	
	
	public Object onActivate(EventContext context) {
		if(context.getCount() > 0)
			query = context.get(String.class, 0);
		return true;
	}
	
	@SetupRender
	void setupRender() {
		if(this.query == null)
			this.query = "";
		monitor.visitPage("Search", query);
		
	}
	
	public String getTitle() {
		return "搜索";
	}
	
	@Inject
	private MonitorService monitor;
}
