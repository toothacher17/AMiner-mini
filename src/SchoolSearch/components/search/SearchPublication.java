package SchoolSearch.components.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.page.Interface.PagerContainer;
import SchoolSearch.services.search.SearchService;
import SchoolSearch.services.search.model.SearchResult;
import SchoolSearch.services.utils.Strings;
import SchoolSearch.services.utils.model.PagerParameter;

public class SearchPublication implements PagerContainer {
	@Parameter(allowNull=false)
	String query;
	
	@Property
	String _query;
	
	@Property
	String _pagerQuery;
	
	@Property
	List<Integer> publicationIdList;
	
	@Property
	PagerParameter parameter;
	
	@InjectComponent
	Zone publicationDisplayZone;
	
	@SuppressWarnings("unchecked")
	void setupRender() {
		this._query = query;
		this._pagerQuery = query.replaceAll("\\{", "&lcb~").replaceAll("\\}", "&rcb~").replaceAll("\\:", "&col~");
		if(Strings.isNotEmpty(this._query)) {
			SearchResult searchResult = searchService.searchPublication(_query);
			parameter = new PagerParameter(searchResult);
			publicationIdList = (List<Integer>) searchResult.getResult();
		} else {
			parameter = new PagerParameter(0, 0, 0, 0l);
			publicationIdList = new ArrayList<Integer>();
		}
	}
	
	@Override
	public void prepareData(Object[] context, Integer currentPage) {
		this._pagerQuery = (String)context[0];
		String query = _pagerQuery.replaceAll("&lcb~", "{").replaceAll("&rcb~", "}").replaceAll("&col~", ":");
		SearchResult searchResult = searchService.searchPublication(query, currentPage-1);
		parameter = new PagerParameter(searchResult);
		this._query = query;
		this.publicationIdList = (List<Integer>) searchResult.getResult();
		
	}
	
	@Override
	public Object getPagerRefreshZoneBody() {
		return publicationDisplayZone.getBody();
	}
	
	
	@Inject
	SearchService searchService;

	@Override
	public void anotherPrepareData(Integer cuurentPage, Integer totalPage, List<Integer> publicationIdList, Integer personId) {
		// TODO Auto-generated method stub
		
	}

}
