package transfer;
/**
 * @author GCR
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import SchoolSearch.services.dao.graduatePublication.GraduatePublicationDao;
import SchoolSearch.services.dao.graduatePublication.Person2GraduatePublicationDao;
import SchoolSearch.services.dao.graduatePublication.model.Person2GraduatePublication;
import SchoolSearch.services.dao.person.PersonRelationDao;
import SchoolSearch.services.dao.person.model.PersonRelation;
import SchoolSearch.services.dao.person.PersonDao;
import SchoolSearch.services.dao.publication.Person2PublicationDao;
import SchoolSearch.services.dao.publication.PublicationDao;
import SchoolSearch.services.dao.publication.model.Person2Publication;
import SchoolSearch.services.utils.Strings;

public class PersonRelationTransfer {
	
	public void person2RelationTransfer() {
		PersonRelationDao prdao = PersonRelationDao.getInstance();
		prdao.truncate();
		
		boolean withPublication = false;
		
		Person2PublicationDao p2pdao = Person2PublicationDao.getInstance();
		
		Person2GraduatePublicationDao pgpdao = Person2GraduatePublicationDao.getInstance();
		
		GraduatePublicationDao gpdao = GraduatePublicationDao.getInstance();
		
		PublicationDao pdao = PublicationDao.getInstance();
		
		PersonDao persondao = PersonDao.getInstance();
		
		List<PersonRelation> prAll = new ArrayList<PersonRelation>();
		
		List<Person2GraduatePublication> pgpList = pgpdao.walkAll();
		List<Person2Publication> p2pList = p2pdao.walkAll();
		
		Set<Integer> getAllPersonIdsFromp2g =  new HashSet<Integer>();
		for (Person2GraduatePublication pgp : pgpList) {
			getAllPersonIdsFromp2g.add(pgp.person_id);
		}
		Set<Integer> getAllPersonIdsFromp2p =  new HashSet<Integer>();
		for (Person2Publication p2p : p2pList) {
			getAllPersonIdsFromp2p.add(p2p.person_id);
		}
			
		Map<Integer, Map<Object, PersonRelation>> personRelationCache = new HashMap<Integer, Map<Object,PersonRelation>>();
		
		for (Integer person_id : getAllPersonIdsFromp2g) {
			if(!personRelationCache.containsKey(person_id)) {
				personRelationCache.put(person_id, new HashMap<Object, PersonRelation>());
			}
		}
		for (Integer person_id : getAllPersonIdsFromp2p) {
			if (!personRelationCache.containsKey(person_id)) {
				personRelationCache.put(person_id, new HashMap<Object, PersonRelation>());
			}
		}
		
		for(Integer person_id : personRelationCache.keySet()) {
			if(getAllPersonIdsFromp2g.contains(person_id)) {
				
				List<Integer> grapubIds = pgpdao.getGraduatePublicationIdsByPersonIdAndType(person_id, "author");
				
				if(grapubIds.size() > 0) {
					List<Integer> tutorIds = pgpdao.getPersonIdsByGraduatePublicationIdsAndType(grapubIds, "tutor");
					Map<Object, PersonRelation> getTutorsByPersonId = personRelationCache.get(person_id);
					for(Integer tutorId : tutorIds) {
						if(!getTutorsByPersonId.containsKey(tutorId)) {
							PersonRelation pr = new PersonRelation();
							pr.person1_id = person_id;
							pr.person2_id = tutorId;
							pr.type = "student";
							pr.score ++;
							getTutorsByPersonId.put(tutorId, pr);
						} else
							getTutorsByPersonId.get(tutorId).score ++;	
					}
					Set<String> allTutorNames = new HashSet<String>();
					for(Integer grapubId : grapubIds) {
						String tutor1_name = gpdao.getGraduatePublicationById(grapubId).tutor1_name;
						String tutor2_name = gpdao.getGraduatePublicationById(grapubId).tutor2_name;
						if(Strings.isNotEmpty(tutor1_name)) {
							allTutorNames.add(tutor1_name);
						}
						if(Strings.isNotEmpty(tutor2_name)) {
							allTutorNames.add(tutor2_name);
						}
					}
					Set<String> existedTutorNames = new HashSet<String>();
					for(Integer tutorId : tutorIds) {
						existedTutorNames.add(persondao.getPersonById(tutorId).name);
					}
					for(String existedTutorName : existedTutorNames) {
						if(allTutorNames.contains(existedTutorName)) {
							allTutorNames.remove(existedTutorName);
						}
					}
					if(allTutorNames.size() != 0) {
						for(String tutor : allTutorNames) {
							if(!getTutorsByPersonId.containsKey(tutor)) {
								PersonRelation pr = new PersonRelation();
								pr.person1_id = person_id;
								pr.person2_name = tutor;
								pr.type = "student";
								pr.score ++;
								getTutorsByPersonId.put(tutor, pr);
							} else
								getTutorsByPersonId.get(tutor).score ++;	
						}
					}
					
				}
				
				List<Integer> grapubIds2 = pgpdao.getGraduatePublicationIdsByPersonIdAndType(person_id, "tutor");
				if(grapubIds2.size() > 0) {
					List<Integer> coauthorIds = pgpdao.getPersonIdsByGraduatePublicationIdsAndType(grapubIds2, "tutor");
					coauthorIds.remove(person_id);
					Map<Object, PersonRelation> getCoauthorsByPersonId = personRelationCache.get(person_id);
					for(Integer coauthorId : coauthorIds) {
						if(!getCoauthorsByPersonId.containsKey(coauthorId)) {
							PersonRelation pr = new PersonRelation();
							pr.person1_id = person_id;
							pr.person2_id = coauthorId;
							pr.type = "co-author";
							pr.score ++;
							getCoauthorsByPersonId.put(coauthorId, pr);
						} else
							getCoauthorsByPersonId.get(coauthorId).score ++;	
					}
					
					Set<String> allCoauthorNames = new HashSet<String>();
					for(Integer grapubId2 : grapubIds2) {
						String tutor1_name = gpdao.getGraduatePublicationById(grapubId2).tutor1_name;
						String tutor2_name = gpdao.getGraduatePublicationById(grapubId2).tutor2_name;
						if(Strings.isNotEmpty(tutor1_name)) {
							allCoauthorNames.add(tutor1_name);
						}
						if(Strings.isNotEmpty(tutor2_name)) {
							allCoauthorNames.add(tutor2_name);
						}
					}
					Set<String> existedCoauthorNames = new HashSet<String>();
					for(Integer coauthorId : coauthorIds) {
						existedCoauthorNames.add(persondao.getPersonById(coauthorId).name);
					}
					existedCoauthorNames.add(persondao.getPersonById(person_id).name);
					
					for(String existedCoauthorName : existedCoauthorNames) {
						if(allCoauthorNames.contains(existedCoauthorName)) {
							allCoauthorNames.remove(existedCoauthorName);
						}
					}
					if(allCoauthorNames.size() != 0) {
						for(String coauthor : allCoauthorNames) {
							if(!getCoauthorsByPersonId.containsKey(coauthor)) {
								PersonRelation pr = new PersonRelation();
								pr.person1_id = person_id;
								pr.person2_name = coauthor;
								pr.type = "co-author";
								pr.score ++;
								getCoauthorsByPersonId.put(coauthor, pr);
							} else
								getCoauthorsByPersonId.get(coauthor).score ++;	
						}
					}
				}
				
				List<Integer> grapubIds3 = pgpdao.getGraduatePublicationIdsByPersonIdAndType(person_id, "tutor");
				if(grapubIds3.size() > 0) {
					List<Integer> studentIds = pgpdao.getPersonIdsByGraduatePublicationIdsAndType(grapubIds3, "author");
					Map<Object, PersonRelation> getStudentsByPersonId = personRelationCache.get(person_id);
					for(Integer studentId : studentIds) {
						if(!getStudentsByPersonId.containsKey(studentId)) {
							PersonRelation pr = new PersonRelation();
							pr.person1_id = person_id;
							pr.person2_id = studentId;
							pr.type = "tutor";
							pr.score ++;
							getStudentsByPersonId.put(studentId, pr);
						} else
							getStudentsByPersonId.get(studentId).score ++;	
					}
					
					Set<String> allStudentNames = new HashSet<String>();
					for (Integer grapubId3 : grapubIds3) {
						String student_name = gpdao.getGraduatePublicationById(grapubId3).author;
						allStudentNames.add(student_name);
					}
					
					Set<String> existedStudentNames = new HashSet<String>();
					for(Integer studentId : studentIds) {
						existedStudentNames.add(persondao.getPersonById(studentId).name);
					}
					for(String existedStudentName : existedStudentNames) {
						if(allStudentNames.contains(existedStudentName)) {
							allStudentNames.remove(existedStudentName);
						}
					}
					if(allStudentNames.size() != 0) {
						for(String student : allStudentNames) {
							if(!getStudentsByPersonId.containsKey(student)) {
								PersonRelation pr = new PersonRelation();
								pr.person1_id = person_id;
								pr.person2_name = student;
								pr.type = "tutor";
								pr.score ++;
								getStudentsByPersonId.put(student, pr);
							} else
								getStudentsByPersonId.get(student).score ++;	
						}
					}
					
				}
			}
			
			if(getAllPersonIdsFromp2p.contains(person_id)) {
				List<Integer> pubIds = p2pdao.getPublicationIdsByPersonId(person_id);
				List<Integer> coauthorIds = p2pdao.getPersonIdsByPublicationIds(pubIds);
				coauthorIds.remove(person_id);
				Map<Object, PersonRelation> getCoauthorsByPersonId = personRelationCache.get(person_id);
				for(Integer coauthorId : coauthorIds) {
					if(!getCoauthorsByPersonId.containsKey(coauthorId)) {
						PersonRelation pr = new PersonRelation();
						pr.person1_id = person_id;
						pr.person2_id = coauthorId;
						pr.type = "co-author";
						pr.score ++;
						getCoauthorsByPersonId.put(coauthorId, pr);
					} else
						getCoauthorsByPersonId.get(coauthorId).score ++;	
				}
				
				
				if(withPublication == true) {
					Set<String> allCoauthorNames = new HashSet<String>();
					for(Integer pubId : pubIds) {
						String[] authors = pdao.getPublicationById(pubId).authors.split("\\|\\|");
						for (String author : authors) {
							allCoauthorNames.add(author);
						}
					}
					Set<String> existedCoauthorNames = new HashSet<String>();
					for(Integer coauthorId : coauthorIds) {
						existedCoauthorNames.add(persondao.getPersonById(coauthorId).name);
					}
					existedCoauthorNames.add(persondao.getPersonById(person_id).name);
					
					for(String existedCoauthorName : existedCoauthorNames) {
						if(allCoauthorNames.contains(existedCoauthorName)) {
							allCoauthorNames.remove(existedCoauthorName);
						}
					}
					if(allCoauthorNames.size() != 0) {
						for(String coauthor : allCoauthorNames) {
							if(!getCoauthorsByPersonId.containsKey(coauthor)) {
								PersonRelation pr = new PersonRelation();
								pr.person1_id = person_id;
								pr.person2_name = coauthor;
								pr.type = "co-author";
								pr.score ++;
								getCoauthorsByPersonId.put(coauthor, pr);
							} else
								getCoauthorsByPersonId.get(coauthor).score ++;	
						}
					}
				}
			}
		}
		
		for(Integer person1_id : personRelationCache.keySet()) {
			Map<Object, PersonRelation> getPerson2ByPerson1Id = personRelationCache.get(person1_id);
			for(Object person : getPerson2ByPerson1Id.keySet()) {
				PersonRelation pr = getPerson2ByPerson1Id.get(person);
				prAll.add(pr);
			}
		}
		
		System.out.println(prAll.size());
		prdao.insertBatch(prAll);
	}
	public static void main(String[] args) {
		PersonRelationTransfer pr = new PersonRelationTransfer();
		pr.person2RelationTransfer();
	}
}
