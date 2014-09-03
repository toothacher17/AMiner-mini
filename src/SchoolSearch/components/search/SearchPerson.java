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

public class SearchPerson implements PagerContainer {
	@Parameter(allowNull = false)
	String query;

	@Property
	String _query;

	@Property
	String _pagerQuery;
	
	@Property
	List<Integer> personIdList;

	@Property
	PagerParameter parameter;
	
	@InjectComponent
	Zone personDisplayZone;

	@SuppressWarnings("unchecked")
	void setupRender() {
		this._query = query;
		if(Strings.isNotEmpty(this._query)) {
			this._pagerQuery = query.replaceAll("\\{", "&lcb~").replaceAll("\\}", "&rcb~").replaceAll("\\:", "&col~");
			SearchResult searchResult = searchService.searchPerson(_query);
			parameter = new PagerParameter(searchResult);
			personIdList = (List<Integer>) searchResult.getResult();
		} else {
			parameter = new PagerParameter(0, 0, 0, 0l);
			personIdList = new ArrayList<Integer>();
		}
	}

	@Override
	public void prepareData(Object[] context, Integer currentPage) {
		this._pagerQuery = (String)context[0];
		
		String query = _pagerQuery.replaceAll("&lcb~", "{").replaceAll("&rcb~", "}").replaceAll("&col~", ":");
		SearchResult searchResult = searchService.searchPerson(query, currentPage-1);
		parameter = new PagerParameter(searchResult);
		this._query = query;
		this.personIdList = (List<Integer>) searchResult.getResult();
	}

	@Override
	public Object getPagerRefreshZoneBody() {
		return personDisplayZone.getBody();
	}

	@Inject
	SearchService searchService;

	@Override
	public void anotherPrepareData(Integer cuurentPage, Integer totalPage, List<Integer> publicationIdList, Integer personId) {
		// TODO Auto-generated method stub
		
	}

}
