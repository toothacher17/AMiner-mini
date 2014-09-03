package SchoolSearch.services.utils.model;

import SchoolSearch.services.search.model.SearchResult;

public class PagerParameter {

	public PagerParameter(Integer currentPage, Integer totalPage, Integer pageSize, Long costTime) {
		super();
		this.currentPage = currentPage;
		this.totalPage = totalPage;
		this.pageSize = pageSize;
		this.costTime = costTime;
	}

	public PagerParameter(SearchResult searchResult) {
		currentPage = searchResult.getCurrentPage() + 1;
		totalPage = searchResult.getTotalPage();
		pageSize = searchResult.getPageSize();
		costTime = searchResult.getCostTime();
	}

	Integer currentPage;
	Integer totalPage;
	Integer pageSize;
	Long costTime;
	
	public Integer getCurrentPage() {
		return currentPage;
	}

	public Integer getTotalPage() {
		return totalPage;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public Long getCostTime() {
		return costTime;
	}
	
}
