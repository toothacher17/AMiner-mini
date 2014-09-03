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
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause;
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
import SchoolSearch.services.search.AdvancedSearchService;
import SchoolSearch.services.search.BaseSearchService;
import SchoolSearch.services.search.SearchService;
import SchoolSearch.services.search.model.SearchCondition;
import SchoolSearch.services.utils.Strings;

public class AdvancedSearchServiceImpl implements AdvancedSearchService {

	static String[] personAdvanceSearchCondition = { "person.position", "person.title" };
	static String[] publicationAdvanceSearchCondition = { "publication.author.position", "publication.author.title" };
	static String[] courseAdvanceSearchCondition = { "course.person.position", "course.person.title" };

	static String[] advancedSearchCondition = { "position", "authorTitle" };
	static Map<String, Integer> advanceSearchParamMap = new HashMap<String, Integer>();
	static {
		for (int i = 0; i < advancedSearchCondition.length; i++)
			advanceSearchParamMap.put(advancedSearchCondition[i], i);
	}

	IndexSearcher searcher = null;
	IndexReader reader;

	public AdvancedSearchServiceImpl() {
		prepareSearcher();
	}

	@Override
	public List<Integer> searchPerson(String query, Map<String, String> parameter) {
		List<ScoreDoc> searchResult = null;
		Map<String, String> personParameter = new HashMap<String, String>();
		for (String key : parameter.keySet()) {
			Integer index = advanceSearchParamMap.get(key);
			if (null != index)
				personParameter.put(personAdvanceSearchCondition[index], parameter.get(key));
		}
		searchResult = search(SearchCondition.Default.personSearchCondition, query, personParameter);
		List<Integer> result = new ArrayList<Integer>();
		for (ScoreDoc sd : searchResult) {
			Document doc = null;
			try {
				doc = searcher.doc(sd.doc);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Integer pid = Integer.parseInt(doc.get("person.id"));
			result.add(pid);
		}
		return result;
	}

	@Override
	public List<Integer> searchPublication(String query, Map<String, String> parameter) {

		List<ScoreDoc> searchResult = null;
		Map<String, String> publicationParameter = new HashMap<String, String>();
		for (String key : parameter.keySet()) {
			Integer index = advanceSearchParamMap.get(key);
			if (null != index)
				publicationParameter.put(publicationAdvanceSearchCondition[index], parameter.get(key));
		}
		searchResult = search(SearchCondition.Default.publicationSearchCondition, query, publicationParameter);
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
	public List<Integer> searchCourse(String query, Map<String, String> parameter) {
		List<ScoreDoc> searchResult = null;
		Map<String, String> courseParameter = new HashMap<String, String>();
		for (String key : parameter.keySet()) {
			Integer index = advanceSearchParamMap.get(key);
			if (null != index)
				courseParameter.put(courseAdvanceSearchCondition[index], parameter.get(key));
		}
		searchResult = search(SearchCondition.Default.courseSearchCondition, query, courseParameter);
		List<Integer> result = new ArrayList<Integer>();
		for (ScoreDoc sd : searchResult) {
			Document doc = null;
			try {
				doc = searcher.doc(sd.doc);
			} catch (IOException e) {
				e.printStackTrace();
			}
			result.add(Integer.parseInt(doc.get("course.id")));
			
//			List<Integer> group = new ArrayList<Integer>();
//			String[] values = doc.getValues("course.id");
//			for (String v : values) {
//				group.add(Integer.parseInt(v));
//			}
//			result.add(group);
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

	private List<ScoreDoc> search(SearchCondition condition, String query, Map<String, String> parameter) {
		TopDocs fixSearch = fixSearch(condition, query, parameter);
		TopDocs fuzzySearch = fuzzySearch(condition, query, parameter);
		ScoreDoc[] fixDocs = fixSearch.scoreDocs;
		ScoreDoc[] fuzzyDocs = null;
		if (null != fuzzySearch)
			fuzzyDocs = fuzzySearch.scoreDocs;
		else
			fuzzyDocs = new ScoreDoc[0];

		List<ScoreDoc> result = new ArrayList<ScoreDoc>();
		Map<Integer, ScoreDoc> scoreDocMap = new HashMap<Integer, ScoreDoc>();
		for (ScoreDoc sd : fixDocs) {
			sd.score *= fixTimes;
			if (!scoreDocMap.containsKey(sd.doc)) {
				scoreDocMap.put(sd.doc, sd);
				result.add(sd);
			} else {
				ScoreDoc scoreDoc = scoreDocMap.get(sd.doc);
				scoreDoc.score += sd.score;
			}
		}
		
		for (ScoreDoc sd : fuzzyDocs) {
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
		}
	}

	private TopDocs fixSearch(SearchCondition condition, String query, Map<String, String> constrain) {
		BooleanQuery q = new BooleanQuery();
		String[] fields = condition.getFields();
		float[] boosters = condition.getBoosters();
		for (int i = 0; i < fields.length; i++) {
			TermQuery termQuery = new TermQuery(new Term(fields[i], query));
			termQuery.setBoost((float) boosters[i]);
			q.add(new BooleanClause(termQuery, Occur.SHOULD));
		}
		for (String key : constrain.keySet()) {
			TermQuery termQuery = new TermQuery(new Term(key, constrain.get(key)));
			q.add(new BooleanClause(termQuery, Occur.MUST));
		}

		TopDocs sdocs = null;
		try {
			sdocs = searcher.search(q, SearchService.defaultSearchResultSize);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sdocs;
	}

	// TODO: add booster support
	private TopDocs fuzzySearch(SearchCondition condition, String query, Map<String, String> constrain) {
		if (Strings.isEmpty(query))
			return null;
		String[] fields = condition.getFields();

		BooleanClause.Occur[] occurs = new BooleanClause.Occur[fields.length];
		for (int i = 0; i < fields.length; i++) {
			occurs[i] = BooleanClause.Occur.SHOULD;
		}

		BooleanQuery q = new BooleanQuery();

		Query mq = null;
		TopDocs sdocs = null;
		try {
			mq = MultiFieldQueryParser.parse(Version.LUCENE_43, query, fields, occurs, new IKAnalyzer());
			q.add(new BooleanClause(mq, Occur.SHOULD));

			for (String key : constrain.keySet()) {
				TermQuery termQuery = new TermQuery(new Term(key, constrain.get(key)));
				q.add(new BooleanClause(termQuery, Occur.MUST));
			}
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
}
