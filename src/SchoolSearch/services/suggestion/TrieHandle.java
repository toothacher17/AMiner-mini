package SchoolSearch.services.suggestion;

import java.util.List;

import SchoolSearch.services.suggestion.model.TrieNodeContent;


public interface TrieHandle {
	List<TrieNodeContent> findNodeContent(String key);
	void rebuild();
}
