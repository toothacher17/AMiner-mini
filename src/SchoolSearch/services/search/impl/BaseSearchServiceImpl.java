package SchoolSearch.services.search.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.util.Version;
import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.cache.CourseCacheService;
import SchoolSearch.services.dao.course.CourseDao;
import SchoolSearch.services.dao.course.model.Course;
import SchoolSearch.services.org.wltea.analyzer.lucene.IKAnalyzer;
import SchoolSearch.services.search.BaseSearchService;
import SchoolSearch.services.search.RerankService;
import SchoolSearch.services.search.SearchService;
import SchoolSearch.services.search.model.SearchCondition;

public class BaseSearchServiceImpl implements BaseSearchService {

	IndexSearcher searcher = null;
	IndexReader reader;

	public BaseSearchServiceImpl() {
		prepareSearcher();
	}

	@Override
	public List<Integer> searchPerson(String query, SearchType type) {
		List<ScoreDoc> searchResult = null;
		if(type.equals(SearchType.Normal))
			searchResult = search(SearchCondition.Default.personSearchCondition, query);
		else if(type.equals(SearchType.Person))
			searchResult = search(SearchCondition.Default.personSearchCondition_NameQuery, query);
		return rerankService.personRerank(searchResult, searcher);
	}

	@Override
	public List<Integer> searchPublication(String query) {
		List<ScoreDoc> searchResult = search(SearchCondition.Default.publicationSearchCondition, query);
		List<Integer> result = new ArrayList<Integer>();
		for (ScoreDoc sd : searchResult) {
			Document doc = null;
			try {
				doc = searcher.doc(sd.doc);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Integer pid = Integer.parseInt(doc.get("publication.id"));
			result.add(pid);
		}
		return result;
	}

	@Inject
	CourseDao courseDao;

	@Inject
	CourseCacheService coursecacheService;

	@Override
	public List<Integer> searchCourse(String query) {
		List<ScoreDoc> searchResult = search(SearchCondition.Default.courseSearchCondition, query);
		List<Integer> result = new ArrayList<Integer>();
		for (ScoreDoc sd : searchResult) {
			Document doc = null;
			try {
				doc = searcher.doc(sd.doc);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Integer pid = Integer.parseInt(doc.get("course.id"));
			result.add(pid);
		}
		return result;
	}

	@Override
	public void close() {
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private List<ScoreDoc> search(SearchCondition condition, String query) {
//		Pattern p = Pattern.compile("[^\\s:]+:\\{([^\\}]+)\\}");
		query = query.replaceAll("[^\\s:]+:\\{[^\\}]+\\}", "").replaceAll("\\s+", " ").trim();
//		System.out.println("search query : " + query);
		TopDocs fixSearch = fixSearch(condition, query);
//		TopDocs fuzzySearchAll = fuzzySearchALL(condition, query);
		TopDocs fuzzySearchSingle = fuzzySearchSingle(condition, query);
		ScoreDoc[] fixDocs = fixSearch.scoreDocs;
//		ScoreDoc[] fuzzyDocsAll = null;
		ScoreDoc[] fuzzyDocsSingle = null;
//		if (null != fuzzySearchAll)
//			fuzzyDocsAll = fuzzySearchAll.scoreDocs;
//		else
//			fuzzyDocsAll = new ScoreDoc[0];
		
		if (null != fuzzySearchSingle)
			fuzzyDocsSingle = fuzzySearchSingle.scoreDocs;
		else
			fuzzyDocsSingle = new ScoreDoc[0];

		List<ScoreDoc> result = new ArrayList<ScoreDoc>();
		Map<Integer, ScoreDoc> scoreDocMap = new HashMap<Integer, ScoreDoc>();
		for (ScoreDoc sd : fixDocs) {
			sd.score *= fix_Times;
			if (!scoreDocMap.containsKey(sd.doc)) {
				scoreDocMap.put(sd.doc, sd);
				result.add(sd);
			} else {
				ScoreDoc scoreDoc = scoreDocMap.get(sd.doc);
				scoreDoc.score += sd.score;
			}
		}
//		for (ScoreDoc sd : fuzzyDocsAll) {
//			sd.score *= fuzzy_all_Times;
//			if (!scoreDocMap.containsKey(sd.doc)) {
//				scoreDocMap.put(sd.doc, sd);
//				result.add(sd);
//			} else {
//				ScoreDoc scoreDoc = scoreDocMap.get(sd.doc);
//				scoreDoc.score += sd.score;
//			}
//		}
		for (ScoreDoc sd : fuzzyDocsSingle) {
			if (!scoreDocMap.containsKey(sd.doc)) {
				scoreDocMap.put(sd.doc, sd);
				result.add(sd);
			} else {
				ScoreDoc scoreDoc = scoreDocMap.get(sd.doc);
				scoreDoc.score += sd.score;
			}
		}
		Collections.sort(result, Collections.reverseOrder(scoreDocComparator));
		return result;
	}

	private void prepareSearcher() {
		if (null == searcher) {
			File indexDir = new File(ConsistanceService.get("lucene.index"));
			try {
				reader = DirectoryReader.open(FSDirectory.open(indexDir));
			} catch (IOException e) {
				e.printStackTrace();
			}
			searcher = new IndexSearcher(reader);
			// try {
			// reader.close();
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
		}
	}

	private TopDocs fixSearch(SearchCondition condition, String query) {
		BooleanQuery q = new BooleanQuery();
		String[] fields = condition.getFields();
		float[] boosters = condition.getBoosters();
		for (int i = 0; i < fields.length; i++) {
			TermQuery termQuery = new TermQuery(new Term(fields[i], query));
			termQuery.setBoost((float) boosters[i]);
			q.add(new BooleanClause(termQuery, Occur.SHOULD));
			q.add(new BooleanClause(new TermQuery(new Term(fields[i], query)), Occur.SHOULD));
		}
		TopDocs sdocs = null;
		try {
			sdocs = searcher.search(q, SearchService.defaultSearchResultSize);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sdocs;
	}
	
	
	//TODO: add booster support
//	private TopDocs fuzzySearchALL(SearchCondition condition, String query) {
//		String[] fields = condition.getFields();
//		
//		BooleanClause.Occur[] occurs = new BooleanClause.Occur[fields.length];
//		for (int i = 0; i < fields.length; i++) {
//			occurs[i] = BooleanClause.Occur.MUST;
//		}
//		Query q = null;
//		TopDocs sdocs = null;
//		try {
//			q = MultiFieldQueryParser.parse(Version.LUCENE_43, query, fields, occurs, new IKAnalyzer());
//			sdocs = searcher.search(q, SearchService.defaultSearchResultSize);
//		} catch (ParseException e1) {
//			e1.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return sdocs;
//	}
	
	private TopDocs fuzzySearchSingle(SearchCondition condition, String query) {
		String[] fields = condition.getFields();
		
		BooleanClause.Occur[] occurs = new BooleanClause.Occur[fields.length];
		for (int i = 0; i < fields.length; i++) {
			occurs[i] = BooleanClause.Occur.SHOULD;
		}
		Query q = null;
		TopDocs sdocs = null;
		try {
			q = MultiFieldQueryParser.parse(Version.LUCENE_43, query, fields, occurs, new IKAnalyzer());
			sdocs = searcher.search(q, SearchService.defaultSearchResultSize);
		} catch (ParseException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sdocs;
	}

	private Comparator<ScoreDoc> scoreDocComparator = new Comparator<ScoreDoc>() {
		@Override
		public int compare(ScoreDoc o1, ScoreDoc o2) {
			return (int) Math.signum(o1.score - o2.score);
		}
	};
	
	@Inject
	RerankService rerankService;
}
