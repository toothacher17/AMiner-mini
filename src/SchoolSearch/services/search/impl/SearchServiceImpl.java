package SchoolSearch.services.search.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.cache.CacheService;
import SchoolSearch.services.search.AdvancedSearchService;
import SchoolSearch.services.search.BaseSearchService;
import SchoolSearch.services.search.BaseSearchService.SearchType;
import SchoolSearch.services.search.SearchService;
import SchoolSearch.services.search.model.SearchResult;
import SchoolSearch.services.services.person.PersonService;

public class SearchServiceImpl implements SearchService, CacheService {
	static boolean closePersonCache = false;
	static boolean closeCourseCache = false;
	static boolean closePublicationCache = true;
	
	static String advancePattern = "([^\\s:]+):\\{([^\\}]+)\\}";

	Map<String, List<Integer>> publicationSearchCache = new HashMap<String, List<Integer>>();

	@Override
	public SearchResult searchPublication(String query) {
		return searchPublication(query, 0);
	}

	/**
	 * @param page
	 *            start with 0
	 */
	@Override
	public SearchResult searchPublication(String query, int page) {
		return searchPublication(query, page, SearchService.defaultPublicationPageSize);
	}

	/**
	 * @param page
	 *            start with 0
	 */
	@Override
	public SearchResult searchPublication(String fullquery, int page, int size) {
		long searchStartTime = System.currentTimeMillis();
		List<Integer> list = publicationSearchCache.get(fullquery);

		if (null == list || closePublicationCache) { // cache not hit
			Pattern p = Pattern.compile(advancePattern);
			Matcher matcher = p.matcher(fullquery);
			String query = "";
			if (!matcher.find()) { // base search
				query = fullquery;
				list = baseSearchService.searchPublication(query);
			} else { // advanced search
				matcher.reset();
				Map<String, String> parameter = new HashMap<String, String>();
				while (matcher.find()) {
					String key = matcher.group(1);
					String value = matcher.group(2);
					parameter.put(key, value);
//					System.out.println("[S P]" + key + "\t" + value);
				}
				query = fullquery.replaceAll(advancePattern, "").trim();
				list = advancedSearchService.searchPublication(query, parameter);
			}
			publicationSearchCache.put(fullquery, list);
		}
		int start = page * size;
		int end = (page + 1) * size;

		end = Math.min(end, list.size());
		start = Math.min(start, end);

		SearchResult sr = new SearchResult(fullquery, list.subList(start, end), list.size() / size + 1, page, size, (System.currentTimeMillis() - searchStartTime));

		return sr;
	}

	// ----------------------------
	Map<String, List<Integer>> personSearchCache = new HashMap<String, List<Integer>>();

	@Override
	public SearchResult searchPerson(String query) {
		return searchPerson(query, 0);
	}

	/**
	 * @param page
	 *            start with 0
	 */
	@Override
	public SearchResult searchPerson(String query, int page) {
		return searchPerson(query, page, SearchService.defaultPersonPageSize);
	}

	/**
	 * @param page
	 *            start with 0
	 */

	

	@Override
	public SearchResult searchPerson(String fullquery, int page, int size) {
		long searchStartTime = System.currentTimeMillis();
		List<Integer> list = personSearchCache.get(fullquery);

		if (null == list || closePersonCache) { // cache not hit
			Pattern p = Pattern.compile(advancePattern);
			Matcher matcher = p.matcher(fullquery);
			String query = "";
			if (!matcher.find()) { // base search
				query = fullquery;
				if (personService.getPersonByName(query).size() > 0)
					list = baseSearchService.searchPerson(query.toLowerCase(), SearchType.Person);
				else
					list = baseSearchService.searchPerson(query.toLowerCase(), SearchType.Normal);
			} else { // advanced search
				matcher.reset();
				Map<String, String> parameter = new HashMap<String, String>();
				while (matcher.find()) {
					String key = matcher.group(1);
					String value = matcher.group(2);
					parameter.put(key, value);
				}
				query = fullquery.replaceAll(advancePattern, "").trim();
				list = advancedSearchService.searchPerson(query, parameter);
			}
			personSearchCache.put(fullquery, list);
		}
		int start = page * size;
		int end = (page + 1) * size;

		end = Math.min(end, list.size());
		start = Math.min(start, end);

		SearchResult sr = new SearchResult(fullquery, list.subList(start, end), list.size() / size + 1, page, size, (System.currentTimeMillis() - searchStartTime));

		return sr;
	}

	Map<String, List<Integer>> courseSearchCache = new HashMap<String, List<Integer>>();

	@Override
	public SearchResult searchCourse(String query) {
		return searchCourse(query, 0);
	}

	@Override
	public SearchResult searchCourse(String query, int page) {
		return searchCourse(query, page, SearchService.defaultCoursePageSize);
	}

	@Override
	public SearchResult searchCourse(String fullquery, int page, int size) {
		long searchStartTime = System.currentTimeMillis();
		List<Integer> list = courseSearchCache.get(fullquery);

		if (null == list || closeCourseCache) { // cache not hit
			Pattern p = Pattern.compile(advancePattern);
			Matcher matcher = p.matcher(fullquery);
			String query = "";
			if (!matcher.find()) { // base search
				query = fullquery; 
				list = baseSearchService.searchCourse(query.toLowerCase());
			} else { // advanced search
				matcher.reset();
				Map<String, String> parameter = new HashMap<String, String>();
				while (matcher.find()) {
					String key = matcher.group(1);
					String value = matcher.group(2);
					parameter.put(key, value);
				}
				query = fullquery.replaceAll(advancePattern, "").trim();
				list = advancedSearchService.searchCourse(query, parameter);
			}
			courseSearchCache.put(fullquery, list);
		}

		int start = page * size;
		int end = (page + 1) * size;

		end = Math.min(end, list.size());
		start = Math.min(start, end);

		SearchResult sr = new SearchResult(fullquery, list.subList(start, end), list.size() / size + 1, //
				page, size, (System.currentTimeMillis() - searchStartTime));

		return sr;
	}

	@Override
	public void clear() {
		publicationSearchCache.clear();
		personSearchCache.clear();

	}

	@Inject
	PersonService personService;

	@Inject
	BaseSearchService baseSearchService;

	@Inject
	AdvancedSearchService advancedSearchService;
}
