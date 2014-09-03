package SchoolSearch.components.publicationPager;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.page.Interface.PagerContainer;
import SchoolSearch.services.search.SearchService;
import SchoolSearch.services.search.model.SearchResult;
import SchoolSearch.services.services.publication.PublicationService;
import SchoolSearch.services.utils.Strings;
import SchoolSearch.services.utils.model.PagerParameter;

public class PublicationPager implements PagerContainer {

	@Parameter(allowNull = false)
	Integer personId;

	@Parameter(allowNull = true, value = "year", defaultPrefix = BindingConstants.LITERAL)
	String orderBy;

	@Parameter(allowNull = true, value = "normal", defaultPrefix = BindingConstants.LITERAL)
	String showType;
	
	@Property
	PagerParameter parameter;
	
	@InjectComponent
	Zone publicationDisplayZone;
		
	@Property
	List<Integer> _publicationIdList;
	
	@Property 
	Integer _personId;
	
	@Property
	String query;
	
	Integer thePersonId;

	List<Integer> thePublicationIdList;
		
	@SuppressWarnings("unchecked")
	void setupRender() {
		this._personId = personId;
		this.thePersonId=personId;
//		this._publicationIdList = publicationIdList;
		this._publicationIdList = publicationService.getPublicationsIdListByPersonId(this._personId);
		this.thePublicationIdList = _publicationIdList;
		query = "liuxiong";
		if(null!=this._personId) {
			int totalPubNum = _publicationIdList.size();
			int pageSize = 8;
			int totalPage = (int)totalPubNum/pageSize+1;
			long costTime = 0;
			parameter = new PagerParameter(1, totalPage, pageSize, costTime);
		} else {
			parameter = new PagerParameter(0, 0, 0, 0l);
		}
	}

	@Override
	public Object getPagerRefreshZoneBody() {
		return publicationDisplayZone.getBody();
	}
	
	@Override
	public void prepareData(Object[] context, Integer currentPage) {
	}

	@Override
	public void anotherPrepareData(Integer currentPage, Integer totalPage, List<Integer> publicationIdList, Integer personId) {
		long cost = 0;
		Integer pageSize = 8;
		parameter = new PagerParameter(currentPage, totalPage, pageSize, cost);
		_personId = personId;
		_publicationIdList = publicationIdList;
		query = "liuxiong";
	}

	@Inject
	PublicationService publicationService;
	
}
