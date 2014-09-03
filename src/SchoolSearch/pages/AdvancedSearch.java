package SchoolSearch.pages;

import java.util.List;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.internal.services.StringValueEncoder;
import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.pages.search.SearchIndex;
import SchoolSearch.services.CollectionsService;
import SchoolSearch.services.monitor.MonitorService;
import SchoolSearch.services.utils.Strings;

/**
 * @author yxy
 *
 */
@Import(stylesheet = {"context:/res/css/pages/person/person.css"})
public class AdvancedSearch {
	
	 @Property
    private final StringValueEncoder stringValueEncoder = new StringValueEncoder();
	
	@Property
	String mainquery;
	
	@Property
	String position;
	
	@Property
	String personTitle;
	
	@Property
	String personPosition;
	
	@Property
	String _singleTitle;
	
	@Property
	String _singlePosition;
	
	@Property
	String selectedTitle;
	
	@Property
	String selectedPosition;
	
	@InjectPage
	SearchIndex searchPage;
	
	public Object onActivate(EventContext context) {
		return true;
	}
	
	@SetupRender
	void setupRender() {
		if(this.mainquery == null)
			this.mainquery = "";
		monitor.visitPage("AdvancedSearch", null);
	}
	
	Object onSuccess() {
		String avancedQuery = getAvancedQuery();
		if(null == avancedQuery)
			avancedQuery = "";
		return searchPage.activate(avancedQuery);
	}
	
	public String getTitle() {
		return "高级搜索";
	}
	
	public List<String> getPersonTitleList() {
		return CollectionsService.Title.getTitleList();
	}
	public List<String> getPersonPositionList(){
		return CollectionsService.Position.getPostionList();
	}
	
	private String getAvancedQuery() {
		StringBuilder sb = new StringBuilder();
		if(Strings.isNotEmpty(mainquery)) {
			sb.append(mainquery);
		} 
		if(Strings.isNotEmpty(selectedPosition)) {
			if(selectedPosition.equals("其他"))
				selectedPosition=null;
			else
				sb.append(" position:{").append(selectedPosition).append("}");
		}
		if(Strings.isEmpty(selectedPosition) && Strings.isNotEmpty(personPosition)) {
			sb.append(" authorPosition:{").append(personPosition).append("}");
		}

		if(Strings.isNotEmpty(selectedTitle)) {
			if(selectedTitle.equals("其他"))
				selectedTitle = null;
			else
				sb.append(" authorTitle:{").append(selectedTitle).append("}");
		} 
		if(Strings.isEmpty(selectedTitle) && Strings.isNotEmpty(personTitle)) {
			sb.append(" authorTitle:{").append(personTitle).append("}");
		}
		return sb.toString();
	}
	
	@Inject
	private MonitorService monitor;
}
