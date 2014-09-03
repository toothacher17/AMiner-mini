package SchoolSearch.services.search;

import SchoolSearch.services.search.model.SearchResult;

public interface SearchService {

	Integer defaultSearchResultSize = 1000;
	Integer defaultPersonPageSize = 13;
	Integer defaultPublicationPageSize = 8;
	Integer defaultCoursePageSize = 8;
	
	SearchResult searchPublication(String query);
	SearchResult searchPublication(String query, int page);
	SearchResult searchPublication(String query, int page, int size);
	
	SearchResult searchCourse(String query);
	SearchResult searchCourse(String query, int page);
	SearchResult searchCourse(String query, int page, int size);

	SearchResult searchPerson(String query);
	SearchResult searchPerson(String query, int page);
	SearchResult searchPerson(String query, int page, int size);

}
