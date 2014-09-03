package SchoolSearch.services.search.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.dao.schooltest.model.SchooltestPersonExt;
import SchoolSearch.services.search.RerankService;
import SchoolSearch.services.services.person.PersonService;
import SchoolSearch.services.services.person.PersonTitleUtil;
import SchoolSearch.services.utils.Strings;

public class RerankServiceImpl implements RerankService {
	private double title_weight_normalizer = 5;

	@Override
	public List<Integer> personRerank(List<ScoreDoc> searchResult, IndexSearcher searcher) {
		List<RerankObject> rerankObjectList = new ArrayList<RerankServiceImpl.RerankObject>();
		Map<Integer, RerankObject> rerankMap = new HashMap<Integer, RerankServiceImpl.RerankObject>();
		List<Integer> allPersonIdList = new ArrayList<Integer>();
		for (ScoreDoc sd : searchResult) {
			Document doc = null;
			try {
				doc = searcher.doc(sd.doc);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Integer pid = Integer.parseInt(doc.get("person.id"));
			RerankObject rerankObject = new RerankObject(pid, sd.score);
			rerankObjectList.add(rerankObject);
			rerankMap.put(pid, rerankObject);
			allPersonIdList.add(pid);
		}

		List<SchooltestPersonExt> personExtList = personService.getPersonExtList(allPersonIdList);
		for (SchooltestPersonExt pe : personExtList) {
			String titleString = pe.getTitle();
			if (Strings.isNotEmpty(titleString)) {
				String[] titleSplit = titleString.split("[;；\n]+");
				List<Double> titleScoreList = new ArrayList<Double>();
				for (String title : titleSplit) {
					titleScoreList.add(PersonTitleUtil.getScore(title));
				}
				Collections.sort(titleScoreList, Collections.reverseOrder());
				double titleWeight = 1.0;
				for (Double titleScore : titleScoreList) {
					rerankMap.get(pe.getId()).importanceScore += titleScore * titleWeight;
					titleWeight /= title_weight_normalizer;
				}
			}
		}

		Collections.sort(rerankObjectList, Collections.reverseOrder(RerankObject.defailt_comparator));

		List<Integer> result = new ArrayList<Integer>();
		for (RerankObject r : rerankObjectList) {
			result.add(r.id);
		}
		return result;
	}

	private static class RerankObject {
		Integer id;
		Float similarityScore;
		Double importanceScore;

		public RerankObject(Integer id, Float similarityScore) {
			super();
			this.id = id;
			this.similarityScore = similarityScore;
			importanceScore = 4.0;
		}

		public Double getScore() {
			return similarityScore * importanceScore;
		}
		
		public static Comparator<RerankObject> defailt_comparator = new Comparator<RerankServiceImpl.RerankObject>() {
			@Override
			public int compare(RerankObject o1, RerankObject o2) {
				return o1.getScore().compareTo(o2.getScore());
			}
		};
	}

	@Inject
	PersonService personService;
}
