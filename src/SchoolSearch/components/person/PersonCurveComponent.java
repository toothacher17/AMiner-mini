package SchoolSearch.components.person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

import SchoolSearch.services.dao.schooltest.model.SchooltestPublication;
import SchoolSearch.services.services.graduatePublication.GraduatePublicationService;
import SchoolSearch.services.services.person.PersonService;
import SchoolSearch.services.services.publication.PublicationService;
import SchoolSearch.services.utils.ComparatorUtil;
import SchoolSearch.services.utils.Strings;

@Import(library = { "context:/res/js/d3.v2.min.js",//
				"context:/res/js/visualization/visualizationPubLineChart.js" }, //
stylesheet = { "context:/res/css/visualizationPubLineChart.css" } //
)
public class PersonCurveComponent {
	@Parameter(allowNull = false)
	Integer personId;

	@Property
	Integer _personId;

	@Property
	Map<String, Integer> allWordIndex;

	@Property
	Map<String, Integer> selectedWordIndex;

	@Property
	List<Integer> allWordCount;

	@Property
	List<Integer> selectedWordCount;

	@Property
	Integer countindex;

	@Property
	String wordName;

	@Property
	int[][] finalCount;

	@Property
	List<Integer> selectedYearIndex;

	@Property
	Integer selectedYear;

	@Property
	Integer maxYear;

	@Property
	Integer minYear;

	@Property
	List<Integer> indexOfSelectedWords;

	@Property
	List<String> selectedWordList;

	@Property
	Integer index;

	JSONObject json;
	
	Integer allWordNumber;

	@SetupRender
	void setupRender() {
		_personId = personId;
		List<SchooltestPublication> publicationList = publicationService.getPublicationsByPersonId(_personId);
//		Collections.sort(publicationList, Collections.reverseOrder(SchooltestPublication.defaultComparator));
		Collections.sort(publicationList, Collections.reverseOrder(new ComparatorUtil.GeneralComparator<SchooltestPublication>(SchooltestPublication.class, "year")));
		selectedWordCount = getSelectedWordCountFun(publicationList);

		selectedYearIndex = new ArrayList<Integer>();
		if(null!=maxYear&&null!=minYear){
			Integer totalYearNum = maxYear - minYear;
			for (Integer i = 0; i < totalYearNum + 1; i++) {
				selectedYearIndex.add(i + minYear);
			}
		}
		finalCount = getFinalCountFun(publicationList);
		
//		List<double[]> countOfWordsEveryYear() = new new ArrayList<double[]>();
		
		List<double[]> yearCountList = new ArrayList<double[]>();
		for(Integer inte : selectedYearIndex ){
			double[] hitCount = new double[selectedWordList.size()];
			for(int tempint=0 ;tempint< selectedWordList.size();tempint++){
				hitCount[tempint] = finalCount[tempint][inte-minYear];
			}
			yearCountList.add(hitCount);
		}
		double [] publicationTrendCount = getPublicationTrendCount(publicationList);
		this.json = new JSONObject();
		this.json.put("json", formJson(selectedWordList,selectedYearIndex,yearCountList, publicationTrendCount));
	}

	@AfterRender
	void afterRender() {
//		json.put("getFunctionUrl", resources.createEventLink("get", "%s").toURI());
//		System.out.println(json);
		jsSupport.addInitializerCall("visualization_pub_line_chart", json);
	}
	
	public List<Integer> getSelectedWordCountFun(List<SchooltestPublication> publicationList) {
		allWordIndex = new HashMap<String, Integer>();
		Integer countindexTemp = 0;
		List<String> allYears = new ArrayList<String>();
		final List<Integer> allWordCount = new ArrayList<Integer>();
		for (SchooltestPublication p : publicationList) {
			if (!(allYears.contains(p.getYear())))
				allYears.add(p.getYear());
			if (null != p.getKeywords()) {
				List<String> keyWords = getKeywords(p) ;
				for (String s : keyWords) {
					if (!(allWordIndex.containsKey(s))) {
						allWordIndex.put(s, countindexTemp);
						allWordCount.add(1);
						countindexTemp++;
					} else {
						int itemp = allWordCount.get(allWordIndex.get(s)) + 1;
						allWordCount.set(allWordIndex.get(s), itemp);
					}
				}

			}
		}
//		System.out.println(allWordIndex.size());
		List<String> listToSort = new ArrayList<String>(allWordIndex.keySet());
		List<Integer> selectedWordCountTemp = new ArrayList<Integer>();
		Comparator<String> wordComparator = new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return allWordCount.get(allWordIndex.get(o1)).compareTo(allWordCount.get(allWordIndex.get(o2)));
			}
		};
		Collections.sort(listToSort, Collections.reverseOrder(wordComparator));
//		System.out.println(listToSort.get(0));
		selectedWordIndex = new HashMap<String, Integer>();
		for (int i = 0; i < Math.min(10, listToSort.size()); i++) {
			selectedWordIndex.put(listToSort.get(i), i);
			selectedWordCountTemp.add(allWordCount.get(allWordIndex.get(listToSort.get(i))));
		}
		selectedWordList = new ArrayList<String>(selectedWordIndex.keySet());
		if((null!=allYears)&&!allYears.isEmpty()){
			maxYear = Integer.parseInt(allYears.get(0));
			minYear = Integer.parseInt(allYears.get(allYears.size() - 1));
		}
		return selectedWordCountTemp;
	}
	
	public double[] getPublicationTrendCount(List<SchooltestPublication> publicationList){
		double [] result = new double[maxYear - minYear + 1]; 
		for(SchooltestPublication pTemp : publicationList){
			result[Integer.parseInt(pTemp.getYear())-minYear ] += 1;
		}
		return result;
	}

	public int[][] getFinalCountFun(List<SchooltestPublication> publicationList) {
		int countbuild[][];
		countbuild = new int[selectedWordList.size()][selectedYearIndex.size()];
		for (SchooltestPublication pub : publicationList) {
			if (null != pub.getKeywords()) {
				List<String> keyWords=getKeywords(pub);
				for (String keyTemp : keyWords) {
					if(Strings.isEmpty(keyTemp)) 
						continue;
					for (String str : selectedWordList) {
						if (keyTemp.equalsIgnoreCase(str)) {
							int test1 = selectedWordIndex.get(str);
							int test2 = Integer.parseInt(pub.getYear()) - minYear;
//							if (null == countbuild[test1][test2]) {
//								countbuild[test1][test2] = 1;
//							} else {
								countbuild[test1][test2] += 1;
//							}
						}
					}
				}
			}
		}
		return countbuild;
	}

	public Integer getCount() {
		Integer result = finalCount[selectedWordIndex.get(wordName)][selectedYear - minYear];
		return result;
	}

	
	private List<String> getKeywords(SchooltestPublication pub) {
		List<String> result = new ArrayList<String>();
		if (null != pub.getKeywords()) {
			String[] keywordsTemp = pub.getKeywords().split("\\|\\|");
			for (String keyTemp : keywordsTemp) {
				if(Strings.isEmpty(keyTemp)) 
					continue;
				keyTemp = keyTemp.trim();
				StringBuilder sb = new StringBuilder();
				String[] split = keyTemp.split(" ");
				for(String term : split) {
					if(term.matches("\\w\\d+\\w")) {
						continue;
					}
					sb.append(term).append(" ");
				}
				if(sb.length() > 0)
					sb.deleteCharAt(sb.length()-1);
				result.add(sb.toString());
			}
		}
		return result;
	}
	
	
//	this.json.put("json", formJson(selectedWordList,selectedYearIndex,yearCountList),publicationTrendCount);
	private JSONObject formJson(List<String> terms, List<Integer> years, List<double[]> normalizedYearCount, double[] publicationTrendCount ) {
		JSONArray termArray = new JSONArray();
		for (String term : terms) {
			termArray.put(term);
		}

		JSONArray yearCountsArray = new JSONArray();
		for (int i = 0; i < years.size(); i++) {
			int year = years.get(i);
			double[] nycount = normalizedYearCount.get(i);

			JSONArray yArray = new JSONArray();
			for (double yc : nycount) {
				yArray.put(Math.floor(yc*100)/100);
			}

			JSONObject yearObject = new JSONObject();
			yearObject.put("year", year);
			yearObject.put("count", yArray);

			yearCountsArray.put(yearObject);
		}
		
		JSONArray pTrend = new JSONArray();
		for(double pc : publicationTrendCount ){
			pTrend.put(Math.floor(pc*100)/100);
		}
		
		JSONObject result = new JSONObject();
		result.put("terms", termArray);
		result.put("yearCount", yearCountsArray);
		result.put("publicationTrend",pTrend);
		return result;
	}
	
	
	
	
	
	
	
	
	
	@Inject
	JavaScriptSupport jsSupport;
	
	@Inject
	PersonService personService;

	@Inject
	PublicationService publicationService;

	@Inject
	GraduatePublicationService graduatePublicationService;

}
