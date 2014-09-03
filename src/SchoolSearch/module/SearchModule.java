package SchoolSearch.module;

import org.apache.tapestry5.ioc.ServiceBinder;

import SchoolSearch.services.search.AdvancedSearchService;
import SchoolSearch.services.search.BaseSearchService;
import SchoolSearch.services.search.RerankService;
import SchoolSearch.services.search.SearchService;
import SchoolSearch.services.search.impl.AdvancedSearchServiceImpl;
import SchoolSearch.services.search.impl.BaseSearchServiceImpl;
import SchoolSearch.services.search.impl.RerankServiceImpl;
import SchoolSearch.services.search.impl.RerankServiceImpl2;
import SchoolSearch.services.search.impl.RerankServiceImpl3;
import SchoolSearch.services.search.impl.SearchServiceImpl;

public class SearchModule {

	public static void bind(ServiceBinder binder) {
		binder.bind(SearchService.class, SearchServiceImpl.class);
		binder.bind(BaseSearchService.class, BaseSearchServiceImpl.class);
		binder.bind(AdvancedSearchService.class, AdvancedSearchServiceImpl.class);
		
		binder.bind(RerankService.class, RerankServiceImpl3.class);
	}	

}
