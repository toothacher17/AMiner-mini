package SchoolSearch.services.services.prominent;

import java.util.List;
import java.util.Map;


public interface ProminentService {
	Map<Integer, String> getIndex2TitleMap();
	
//	String getTheTitleByTitleId(Integer id);

	Map<String, Map<String, List<Integer>>> makeTheFinalShowMap();

	Map<String, List<Integer>> getTheFinalShowMap(String Title);

}
