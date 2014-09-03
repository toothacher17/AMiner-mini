package SchoolSearch.components.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;

import SchoolSearch.services.page.Interface.PagerContainer;
import SchoolSearch.services.search.SearchService;
import SchoolSearch.services.search.model.SearchResult;
import SchoolSearch.services.utils.model.PagerParameter;

public class UtilPager {

	final int defaultRadius = 3;
	
	@Parameter(allowNull = false, defaultPrefix = BindingConstants.LITERAL)
	String zone;

	@Parameter
	PagerParameter parameter;
	
	@Parameter
	Object[] context;

	@Property
	List<Object> _context;
	
	//other values "simple"
	@Parameter(allowNull = true, defaultPrefix = BindingConstants.LITERAL, value = "full")
	String style;
	
	@Property
	Integer _currentPage;
	
	@Property
	Integer totalPage;
	
	@Property
	Integer pageNum;
	
	@Property
	List<Integer> pages;
	
	@InjectContainer
	PagerContainer parent;

	
	
	void setupRender() {
		this._context = new ArrayList<Object>();
		for(Object o : context) {
			_context.add(o);
		}
		this._currentPage = parameter.getCurrentPage();
		this.totalPage = parameter.getTotalPage();
		this.pages = constructPages();
	}

	public String getZone() {
		return zone;
	}

	private List<Integer> constructPages() {
		List<Integer> list = new ArrayList<Integer>();
		int totalDisplay = 2 * defaultRadius + 1;
		int start, end; 
		if(totalDisplay >= totalPage) {
			start = 1;
			end = totalDisplay;
		}else if(_currentPage <= defaultRadius) {
			start = 1;
			end = totalDisplay;
		} else if(totalPage - _currentPage <= defaultRadius) {
			start = totalPage - totalDisplay + 1;
			end = totalPage;
		} else {
			start = _currentPage - defaultRadius;
			end = _currentPage + defaultRadius;
		}
		for (int i = start; i <= end; i++) {
			list.add(i);
		}
		return list;
	}
	public boolean displayFirst() {
		return pages.get(0) == 1; 
	}
	
	public boolean displayLast() {
		return pages.get(pages.size()-1) == totalPage; 
	}
	
	public boolean isFirstPage() {
		return _currentPage == 1;
	}

	public boolean isLastPage() {
		return _currentPage == totalPage;
	}

	public boolean isCurPage() {
		return _currentPage == pageNum;
	}

	Object onSwitchToPrePage(EventContext context) {
		String contextString = context.get(String.class, 0);
//		System.out.println("contextString:\t" + contextString);
		JSONArray ja = new JSONArray(contextString);
		Object[] nContext = new Object[ja.length()];
		for(int i=0; i<ja.length(); i++) 
			nContext[i] = ja.get(i);
		_currentPage = context.get(Integer.class, 1);
		_currentPage--;
		parent.prepareData(nContext, _currentPage);
		return parent.getPagerRefreshZoneBody();
	}

	Object onSwitchToNextPage(EventContext context) {
		String contextString = context.get(String.class, 0);
//		System.out.println("contextString:\t" + contextString);
		JSONArray ja = new JSONArray(contextString);
		Object[] nContext = new Object[ja.length()];
		for(int i=0; i<ja.length(); i++) 
			nContext[i] = ja.get(i);
		
		_currentPage = context.get(Integer.class, 1);
		_currentPage++;
		parent.prepareData(nContext, _currentPage);
		return parent.getPagerRefreshZoneBody();
	}

	Object onSwitchToPage(EventContext context) {
		String contextString = context.get(String.class, 0);
//		System.out.println("contextString:\t" + contextString);
		JSONArray ja = new JSONArray(contextString);
		Object[] nContext = new Object[ja.length()];
//		System.out.println("context length:\t" + ja.length());
		for(int i=0; i<ja.length(); i++) {
			nContext[i] = ja.get(i);
			System.out.println(ja.get(i));
		}
		_currentPage = context.get(Integer.class, 1);
		parent.prepareData(nContext, _currentPage);
		return parent.getPagerRefreshZoneBody();
	}

	public Block getStyleBlock() {
		return componentResources.getBlock(style);
	}
	
	@Inject
	private ComponentResources componentResources;
	
	@Inject
	SearchService searchService;
}
