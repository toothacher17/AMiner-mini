package SchoolSearch.services.search;

import java.util.List;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;

public interface RerankService {
	List<Integer> personRerank(List<ScoreDoc> searchResult, IndexSearcher searcher);
}
