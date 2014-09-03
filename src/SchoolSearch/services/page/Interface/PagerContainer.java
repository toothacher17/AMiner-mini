package SchoolSearch.services.page.Interface;

import java.util.List;

public interface PagerContainer {
	void prepareData(Object[] context, Integer currentPage);
	
	void anotherPrepareData(Integer cuurentPage, Integer totalPage, List<Integer> publicationIdList, Integer personId );
	
	Object getPagerRefreshZoneBody();
}
