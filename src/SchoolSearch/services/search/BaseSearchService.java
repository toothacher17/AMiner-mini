package SchoolSearch.services.search;

import java.util.List;

public interface BaseSearchService {
	final int fix_Times = 20;
	final int fuzzy_all_Times = 4;
	
	public static enum SearchType {
		Normal, Person
	}
	
	List<Integer> searchPerson(String query, SearchType type);
	List<Integer> searchPublication(String query);

	
	List<Integer> searchCourse(String query);
	
	
	void close();
}
