package SchoolSearch.services.search.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
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
import org.apache.lucene.util.Version;
import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.cache.CourseCacheService;
import SchoolSearch.services.dao.course.CourseDao;
import SchoolSearch.services.org.wltea.analyzer.lucene.IKAnalyzer;
import SchoolSearch.services.search.BaseSearchService;
import SchoolSearch.services.search.SearchService;
import SchoolSearch.services.search.model.SearchCondition;
import SchoolSearch.services.translation.TranslationService;

public class BaseSearchServiceImpl2 implements BaseSearchService {

	IndexSearcher searcher = null;
	IndexReader reader;

	public BaseSearchServiceImpl2() {
		prepareSearcher();
	}

	@Override
	public List<Integer> searchPerson(String query, SearchType type) {
		List<ScoreDoc> searchResult = null;
		if(type.equals(SearchType.Normal))
			searchResult = search(SearchCondition.Default.personSearchCondition, query);
		else if(type.equals(SearchType.Person))
			searchResult = search(SearchCondition.Default.personSearchCondition_NameQuery, query);
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

	protected List<String> printAnalysisResult(Analyzer analyzer, String query) {
		List<String> result = new ArrayList<String>();
		TokenStream tokenStream;
		try {
			tokenStream = analyzer.tokenStream("content", new StringReader(query));
			CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
			tokenStream.reset();
			while (tokenStream.incrementToken()) {
				result.add(charTermAttribute.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
//		for(String word : result) {
//			System.out.println(word);
//		}
		return result;
	} 
	
//	public static void main(String[] args) {
//		BaseSearchServiceImpl bs = new BaseSearchServiceImpl();
//		bs.printAnalysisResult(new IKAnalyzer(true), "social network analysis");
//	}
	
	private List<ScoreDoc> search(SearchCondition condition, String query) {
		
		List<String> keywords = printAnalysisResult(new IKAnalyzer(false), query);
		if(!keywords.contains(query)) {
			keywords.add(query);
		}
		
		for(String k : keywords) {
			System.out.println(">>>>" + k);
		}
		
		List<ScoreDoc> result = new ArrayList<ScoreDoc>();
		Map<Integer, ScoreDoc> scoreDocMap = new HashMap<Integer, ScoreDoc>();
		
		String keyword2 = null;
		
		for(String keyword : keywords) {
			if(!(null == TranslationService.translate(keyword))) {
				keyword2 = TranslationService.translate(keyword);
				TopDocs fixSearch = fixSearch(condition, keyword);
				TopDocs fuzzySearch = fuzzySearch(condition, keyword);
				TopDocs fixSearch2 = fixSearch(condition, keyword2);
				TopDocs fuzzySearch2 = fuzzySearch(condition, keyword2);
				ScoreDoc[] fixDocs = fixSearch.scoreDocs;
				ScoreDoc[] fuzzyDocs = fuzzySearch.scoreDocs;
				ScoreDoc[] fixDocs2 = fixSearch2.scoreDocs;
				ScoreDoc[] fuzzyDocs2 = fuzzySearch2.scoreDocs;
				
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
				for (ScoreDoc sd : fuzzyDocs) {
					if (!scoreDocMap.containsKey(sd.doc)) {
						scoreDocMap.put(sd.doc, sd);
						result.add(sd);
					} else {
						ScoreDoc scoreDoc = scoreDocMap.get(sd.doc);
						scoreDoc.score += sd.score;
					}
				}
				for (ScoreDoc sd : fixDocs2) {
					sd.score *= fix_Times;
					if (!scoreDocMap.containsKey(sd.doc)) {
						scoreDocMap.put(sd.doc, sd);
						result.add(sd);
					} else {
						ScoreDoc scoreDoc = scoreDocMap.get(sd.doc);
						scoreDoc.score += sd.score;
					}
				}
				for (ScoreDoc sd : fuzzyDocs2) {
					if (!scoreDocMap.containsKey(sd.doc)) {
						scoreDocMap.put(sd.doc, sd);
						result.add(sd);
					} else {
						ScoreDoc scoreDoc = scoreDocMap.get(sd.doc);
						scoreDoc.score += sd.score;
					}
				}
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
		System.out.println(">>search>>" + condition.getFields()[0] + ">>" + query);
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
	private TopDocs fuzzySearch(SearchCondition condition, String query) {
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
}
