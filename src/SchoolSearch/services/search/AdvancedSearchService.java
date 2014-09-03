package SchoolSearch.services.search;

import java.util.List;
import java.util.Map;

public interface AdvancedSearchService {
	final int fixTimes = 10;
	
	public static enum SearchType {
		Normal, Person
	}
	
	
	
	List<Integer> searchPerson(String query, Map<String,String> parameter);
	List<Integer> searchPublication(String query, Map<String,String> parameter);
	List<Integer> searchCourse(String query, Map<String,String> parameter);
	void close();
}
