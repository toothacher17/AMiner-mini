package instante;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import SchoolSearch.services.dao.graduatePublication.model.Person2GraduatePublication;
import SchoolSearch.services.dao.person.model.Person;
import SchoolSearch.services.dao.person.model.PersonExt;
import SchoolSearch.services.dao.publication.model.Person2Publication;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson2publication;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonExt;
import SchoolSearch.services.services.graduatePublication.GraduatePublicationService;
import SchoolSearch.services.services.person.PersonService;
import SchoolSearch.services.services.publication.PublicationService;
import SchoolSearch.services.utils.T5RegistryHelper;

public class TriePersonScore {
	
	static PersonService personService = T5RegistryHelper.getService(PersonService.class);
	static PublicationService publicationService = T5RegistryHelper.getService(PublicationService.class);
	static GraduatePublicationService graduatePublicationService = T5RegistryHelper.getService(GraduatePublicationService.class);
	
	double allScore = 0.0;
	double pubScore = 0.0;
	double grapubScore = 0.0;
	
	static double HighestPubScore = 0.0;
	static double HighestGraPubScore = 0.0;
	static List<SchooltestPerson> allPersons = personService.getAllPersons();
	static List<Double> sortAllScores = new ArrayList<Double>();
	
	static {		
		for(SchooltestPerson p : allPersons) {
			if(scoreRankByPublication_Person(p.getId()) > HighestPubScore) {
				HighestPubScore = scoreRankByPublication_Person(p.getId());
			}
			if(scoreRankByGraduatePublication_Person(p.getId()) > HighestGraPubScore) {
				HighestGraPubScore = scoreRankByGraduatePublication_Person(p.getId());
			}
		}
		System.out.println("HighestPubScore: " + HighestPubScore);
		System.out.println("HighestGraPubScore: " + HighestGraPubScore);
	}
	
	public static double scoreRankByGraduatePublication_Person(Integer person_id) {
		double grapubScoreByPerson = 0.0;
		List<Person2GraduatePublication> pgpList = graduatePublicationService.getPersonGraduatePublicationsByPersonId(person_id);
		grapubScoreByPerson = pgpList.size();
		return grapubScoreByPerson;
	}
	
	public static double scoreRankByPublication_Person(Integer person_id) {
		double pubScoreByPerson = 0.0;
		List<SchooltestPerson2publication> p2pList = publicationService.getPersonPublicationsByPersonId(person_id);
		
		if(p2pList.size() == 0) {
			return 0.0;
		}
		
		for(SchooltestPerson2publication p2p : p2pList) {
			if(p2p.getPosition() == 1) {
				pubScoreByPerson += 1.0;
			} else if(p2p.getPosition() == 2) {
				pubScoreByPerson += 0.8;
			} else if(p2p.getPosition() == 3) {
				pubScoreByPerson += 0.75;
			} else if(p2p.getPosition() > 3) {
				pubScoreByPerson += 0.7;
			}
		}	
		return pubScoreByPerson;
	}
	
	public double scoreRankByPublication(Integer person_id) {
	
		pubScore = Math.sqrt(scoreRankByPublication_Person(person_id)/HighestPubScore) * 3.0;
		System.out.print(pubScore + "|");
		return pubScore;
	}
	
	
	public double scoreRankByGraduatePublication(Integer person_id) {
		
		grapubScore = Math.sqrt(scoreRankByGraduatePublication_Person(person_id)/HighestGraPubScore) * 2.0;
		System.out.print(grapubScore + "|");
		return grapubScore;
	}
	
	
	
	public double scoreRankByTitle(Integer person_id) {
		double titleScore = 0.0;
		SchooltestPersonExt pe = personService.getPersonExt(person_id);
		if(null == pe || null == pe.getTitle()) {
			System.out.println(titleScore);
			return titleScore;
		}
		String[] titles = pe.getTitle().split("\n");
		for(String title : titles) {
			if(title.equals("清华大学“学术新人奖”获得者")) {
				titleScore += 3;
			} else if (title.equals("国家杰出青年科学基金获得者")) {
				titleScore += 3;
			} else if(title.equals("国家自然科学基金委创新研究群体学术带头人")||title.equals("国家级教学名师奖获得者")||title.equals("国家“百千万人才工程”入选者")) {
				titleScore += 4;
			} else if(title.equals("何梁何利基金科学与技术进步奖获得者")||title.equals("“长江学者奖励计划”讲座教授")||title.equals("“长江学者奖励计划”特聘教授")) {
				titleScore += 4.5;
			} else if(title.equals("中国工程院院士") || title.equals("中国科学院院士")) {
				titleScore += 5;
			}
		}
		System.out.println(titleScore);
		return titleScore;
	}
	
	public double scoreRankByPerson(Integer person_id) {
		allScore = scoreRankByPublication(person_id) + scoreRankByGraduatePublication(person_id) + scoreRankByTitle(person_id);
		sortAllScores.add(allScore);
		System.out.println("总得分: " + allScore);
		return allScore;
	}
	
	public static void main(String args[]) {
		TriePersonScore s = new TriePersonScore();
		long begin =System.currentTimeMillis();
		for(SchooltestPerson p : allPersons) {
			long t0 =System.currentTimeMillis();
			s.scoreRankByPerson(p.getId());
			long t1 =System.currentTimeMillis();
			System.out.println("第" + p.getId() + "号耗时 " + (t1-t0) + "ms");
		}
		long end =System.currentTimeMillis();
		System.out.println("总耗时：" + (end-begin) + "ms");
		
		Collections.sort(sortAllScores, Collections.reverseOrder());
		
		int count = 0;
		for(Double score : sortAllScores) {
			if(score == 0.0)
				continue;
			System.out.println(score);
			count ++;
		}
		System.out.println("得分不为0的人数：" + count);
	}
}
