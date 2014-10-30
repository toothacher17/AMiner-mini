package SchoolSearch.startup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.*;

import SchoolSearch.services.dao.schooltest.dao.SchooltestPerson2publicationDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPersonrelationDAO;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson2publication;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonrelation;

//build person relation from person2publication, the count is the co-author times 

public class BuildPersonRelation {

	
	public static void main(String[] args) {

		List<SchooltestPerson2publication> allp2p = SchooltestPerson2publicationDAO.getInstance().walkAll();
		Map<Integer, Set<Integer>> pub2person = new HashMap<Integer, Set<Integer>>();
		for(SchooltestPerson2publication everyp2p : allp2p) {
			Set<Integer> tmp = new HashSet<Integer>();
			if(pub2person.containsKey(everyp2p.getPublicationId())) 
				tmp = pub2person.get(everyp2p.getPublicationId());
			tmp.add(everyp2p.getPersonId());
			pub2person.put(everyp2p.getPublicationId(), tmp);
		}
		
		System.out.println(">> the pub2person size is " + pub2person.size());
		allp2p.clear();
		
//		person 2 (person2countmap) map, for every person, the map shows his coauthor and co-paper count
		Map<Integer, Map<Integer, Integer>> p2p = new HashMap<Integer, Map<Integer,Integer>>();
		for(Integer everypub : pub2person.keySet()) {
			Set<Integer> everyall = pub2person.get(everypub);
			for(Integer everyone : everyall) {
				Map<Integer, Integer> p2c = new HashMap<Integer, Integer>();
				if(p2p.containsKey(everyone)) {
					p2c = p2p.get(everyone);
					for(Integer everycoauthor :everyall) {
						if(everycoauthor != everyone) {
							if(p2c.containsKey(everycoauthor)) {
								int count = p2c.get(everycoauthor);
								count ++;
								p2c.put(everycoauthor, count);
							}else {
								p2c.put(everycoauthor, 1);
							}
						}
					}
				}else {
					for(Integer everycoauthor :everyall) {
						if(everycoauthor != everyone) {
							p2c.put(everycoauthor, 1);
						}
					}
				}
				p2p.put(everyone, p2c);
			}
		}
		
		pub2person.clear();
		System.out.println(">> the p2p size is " + p2p.size());
		
		List<SchooltestPersonrelation> result = new ArrayList<SchooltestPersonrelation>();
		int count = 1;
		for(Integer everyResult : p2p.keySet()) {
			Map<Integer, Integer> person2ScoreMap = p2p.get(everyResult);
			for(Integer everyPerson : person2ScoreMap.keySet()) {
				SchooltestPersonrelation personRelation = new SchooltestPersonrelation();
				personRelation.setId(count);
				personRelation.setPersonid1(everyResult);
				personRelation.setPersonid2(everyPerson);
				personRelation.setCount(person2ScoreMap.get(everyPerson));
				if(null != personRelation) {
					result.add(personRelation);
					count ++;
				}
			}
		}
		
		System.out.println("the person relation size is " + result.size());
		SchooltestPersonrelationDAO.getInstance().insertMultipleBatch(result, 1000);
		
	}
}
