package SchoolSearch.services.search.model;

import java.util.List;

public class SearchResult {
	public SearchResult() {
		super();
	}

	public SearchResult(String query, Object result, int totalPage, int currentPage, int pageSize, long costTime) {
		super();
		this.query = query;
		this.result = result;
		this.totalPage = totalPage;
		this.currentPage = currentPage;
		this.pageSize = pageSize;
		this.costTime = costTime;
	}

	String query;
	Object result;
	int totalPage;
	int currentPage;
	int pageSize;
	long costTime;

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(List<Object> result) {
		this.result = result;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	
	
	public long getCostTime() {
		return costTime;
	}

	public void setCostTime(long costTime) {
		this.costTime = costTime;
	}

	public void setResult(Object result) {
		this.result = result;
	}

}
