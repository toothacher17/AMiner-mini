package SchoolSearch.services.search.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.tapestry5.ioc.annotations.Inject;

import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

import SchoolSearch.services.dao.person.model.PersonInfo;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPersonDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPersonInfoDAO;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonExt;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonInfo;
import SchoolSearch.services.search.RerankService;
import SchoolSearch.services.services.person.PersonService;
import SchoolSearch.services.services.person.PersonTitleUtil;
import SchoolSearch.services.services.person.impl.PersonServiceImpl;
import SchoolSearch.services.utils.Strings;

public class RerankServiceImpl2 implements RerankService {
	private double title_weight_normalizer = 5;
	private double rebundance_weight_normalizer = 2;
	
	public static String rootpath = "D:\\Users\\jingyuanliu\\aminiNA";
	
	public double getRebundanceScore2(Integer personId) {
//		PersonService temp = new PersonServiceImpl();
		SchooltestPersonInfoDAO dao = SchooltestPersonInfoDAO.getInstance();
		double rebundanceBase = 1.0;
		System.out.println("the person id is " + personId);
//		SchooltestPersonInfo personinfo = personService.getPersonInfo(personId);
		SchooltestPersonInfo personinfo = dao.selectById(personId);
		Field[] declaredFields = SchooltestPersonInfo.class.getDeclaredFields();
		int count = 0;
		for(Field f : declaredFields) {
			if(f.getName().equalsIgnoreCase("id")) 
				continue;
			f.setAccessible(true);
			try {
				Object fieldValue = f.get(personinfo);
				if(null != fieldValue) {
					count++; 
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		rebundanceBase = rebundanceBase + rebundance_weight_normalizer - 1 / (Math.pow(rebundance_weight_normalizer, count - 1));
		return rebundanceBase;
		
	}
	
	public double getRebundanceScore(Integer personId) {
		double rebundanceBase = 1.0;
//		System.out.println("the person id is " + personId);
		SchooltestPersonInfo personinfo = personService.getPersonInfo(personId); 
		Field[] declaredFields = SchooltestPersonInfo.class.getDeclaredFields();
		int count = 0;
		for(Field f : declaredFields) {
			if(f.getName().equalsIgnoreCase("id")) 
				continue;
			f.setAccessible(true);
			try {
				Object fieldValue = f.get(personinfo);
				if(null != fieldValue) {
					count++; 
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		rebundanceBase = rebundanceBase + rebundance_weight_normalizer - 1 / (Math.pow(rebundance_weight_normalizer, count - 1));
		return rebundanceBase;
	}
	
	@Override
	public List<Integer> personRerank(List<ScoreDoc> searchResult, IndexSearcher searcher) {
		long start = System.currentTimeMillis();
		List<RerankObject> rerankObjectList = new ArrayList<RerankServiceImpl2.RerankObject>();
		Map<Integer, RerankObject> rerankMap = new HashMap<Integer, RerankServiceImpl2.RerankObject>();
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

//		if(rerankObjectList.size() < 32) {
			for(RerankObject re : rerankObjectList) {
				re.SetRebundanceScore(getRebundanceScore(re.id));
			}
//		}else {
//			for(int index = 0; index < 32; index ++) {
//				RerankObject re = rerankObjectList.get(index);
//				re.SetRebundanceScore(getRebundanceScore(re.id));
//			}
//		}
		
		Collections.sort(rerankObjectList, Collections.reverseOrder(RerankObject.defailt_comparator));

		List<Integer> result = new ArrayList<Integer>();
		for (RerankObject r : rerankObjectList) {
			result.add(r.id);
		}
		
		long end = System.currentTimeMillis();
		
		System.out.println("the rerank service takes " + (end - start) + "ms");
		
		return result;
	}

	private static class RerankObject {
		Integer id;
		Float similarityScore;
		Double importanceScore;
		Double rebundanceScore;

		public RerankObject(Integer id, Float similarityScore) {
			super();
			this.id = id;
			this.similarityScore = similarityScore;
			importanceScore = 4.0;
			rebundanceScore = 1.0;
		}

		public void SetRebundanceScore(Double rebundanceScore) {
			this.rebundanceScore = rebundanceScore;
		}
		
		public Double getScore() {
			return similarityScore * importanceScore * rebundanceScore;
		}
		
		public static Comparator<RerankObject> defailt_comparator = new Comparator<RerankServiceImpl2.RerankObject>() {
			@Override
			public int compare(RerankObject o1, RerankObject o2) {
				return o1.getScore().compareTo(o2.getScore());
			}
		};
	}

	@Inject
	PersonService personService;
	
	public void calculateScore() {
		List<SchooltestPersonInfo> allpersonInfo = SchooltestPersonInfoDAO.getInstance().walkAll();
		Map<Integer, Double> result = new HashMap<Integer, Double>();
		Set<Double> diff = new HashSet<Double>(); 
		for(SchooltestPersonInfo pi : allpersonInfo) {
			result.put(pi.getId(), getRebundanceScore2(pi.getId()));
		}
		try {
			PrintWriter out = new PrintWriter(new File(rootpath, "rerankresult.txt"));
			for(Integer k : result.keySet()) {
				out.println(k + "\t" + result.get(k));
				System.out.println(k + "\t" + result.get(k));
				diff.add(result.get(k));
			}
			out.close();
			System.out.println("the result size is " + result.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(Double d : diff) {
			System.out.println("the diff score is " + d);
		}
	}
	

	public static void main(String[] args) {
//		RerankServiceImpl2 temp = new RerankServiceImpl2();
//		temp.calculateScore();
		
	}
	
	
	
}
