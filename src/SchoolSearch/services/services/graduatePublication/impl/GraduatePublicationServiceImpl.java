package SchoolSearch.services.services.graduatePublication.impl;
/**
 * @author GCR
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.cache.GraduatePublicationCacheService;
import SchoolSearch.services.dao.graduatePublication.GraduatePublicationDao;
import SchoolSearch.services.dao.graduatePublication.Person2GraduatePublicationDao;
import SchoolSearch.services.dao.graduatePublication.model.GraduatePublication;
import SchoolSearch.services.dao.graduatePublication.model.Person2GraduatePublication;
import SchoolSearch.services.services.graduatePublication.GraduatePublicationService;
import SchoolSearch.services.utils.dataUpdateTools.basic.read.ReadFromDataBase;

public class GraduatePublicationServiceImpl implements
		GraduatePublicationService {

	@Inject
	GraduatePublicationDao graduatePublicationDao;

	@Inject 
	Person2GraduatePublicationDao personGraduatePublicationDao;
	
	@Override
	public List<GraduatePublication> getStudentGraduatePublicationsByPersonId(
			Integer id) {
		ReadFromDataBase rd = ReadFromDataBase.getIndependentInstance(
				ConsistanceService.get("db.defaultDatabase"), "person2graduatepublication");
		Map<String, List<String>> condition = new HashMap<String, List<String>>();
		condition.put("person_id", Arrays.asList(id.toString()));
		condition.put("type", Arrays.asList("author"));
		rd.getWithConditionString(Arrays.asList("graduatepublication_id"), condition);
		List<Integer> grapubIds = new ArrayList<Integer>();
		for (List<Object> objList : rd.getDataCache()) {
			grapubIds.add((Integer) objList.get(0));
		}

		if(grapubIds.size() == 0)
			return new ArrayList<GraduatePublication>();
		List<GraduatePublication> result = new ArrayList<GraduatePublication>();
		List<Integer> leftIDs = new ArrayList<Integer>();
		
		for(Integer grapubId : grapubIds) {
			GraduatePublication grapub = graduatePublicationCacheService.getGraduatePublicationFromCache(grapubId);
			if(null != grapub) {
				result.add(grapub);
			} else {
				leftIDs.add(grapubId);
			}
		}
		
		if(leftIDs.size() > 0) {
			List<GraduatePublication> graduatePublications = graduatePublicationDao.getGraduatePublicationsByIds(leftIDs);
			for(GraduatePublication grapub : graduatePublications) {
				graduatePublicationCacheService.setGraduatePublicationCache(grapub.id, grapub);
			}
			result.addAll(graduatePublications);
		}
		return result;
	}

	@Override
	public List<GraduatePublication> getTutorGraduatePublicationsByPersonId(
			Integer id) {
		ReadFromDataBase rd = ReadFromDataBase.getIndependentInstance(
				ConsistanceService.get("db.defaultDatabase"), "person2graduatepublication");
		Map<String, List<Object>> condition = new HashMap<String, List<Object>>();
		List<Object> objList1= new ArrayList<Object>();
		objList1.add(id);
		condition.put("person_id", objList1);
		List<Object> objList2= new ArrayList<Object>();
		objList1.add("tutor");
		condition.put("type", objList2);
		rd.getWithCondition(Arrays.asList("graduatepublication_id"), condition);
		List<Integer> grapubIds = new ArrayList<Integer>();
		for (List<Object> objList : rd.getDataCache()) {
			grapubIds.add((Integer) objList.get(0));
		}

		if(grapubIds.size() == 0)
			return new ArrayList<GraduatePublication>();
		List<GraduatePublication> result = new ArrayList<GraduatePublication>();
		List<Integer> leftIDs = new ArrayList<Integer>();
		
		for(Integer grapubId : grapubIds) {
			GraduatePublication grapub = graduatePublicationCacheService.getGraduatePublicationFromCache(grapubId);
			if(null != grapub) {
				result.add(grapub);
			} else {
				leftIDs.add(grapubId);
			}
		}
		
		if(leftIDs.size() > 0) {
			List<GraduatePublication> graduatePublications = graduatePublicationDao.getGraduatePublicationsByIds(leftIDs);
			for(GraduatePublication grapub : graduatePublications) {
				graduatePublicationCacheService.setGraduatePublicationCache(grapub.id, grapub);
			}
			result.addAll(graduatePublications);
		}
		return result;
	}

	@Override
	public List<Integer> getGraduateStudentPublicationIdListByPersonId(
			Integer id) {
		ReadFromDataBase rd = ReadFromDataBase.getIndependentInstance(
				ConsistanceService.get("db.defaultDatabase"), "person2graduatepublication");
		Map<String, List<String>> condition = new HashMap<String, List<String>>();
		condition.put("person_id", Arrays.asList(id.toString()));
		condition.put("type", Arrays.asList("author"));
		rd.getWithConditionString(Arrays.asList("graduatepublication_id"), condition);
		List<Integer> grapubIds = new ArrayList<Integer>();
		for (List<Object> objList : rd.getDataCache()) {
			grapubIds.add((Integer) objList.get(0));
		}
		return grapubIds;
	}

	@Override
	public List<Integer> getGraduateTutorPublicationIdListByPersonId(Integer id) {
		ReadFromDataBase rd = ReadFromDataBase.getIndependentInstance(
				ConsistanceService.get("db.defaultDatabase"), "person2graduatepublication");
		Map<String, List<String>> condition = new HashMap<String, List<String>>();
		condition.put("person_id", Arrays.asList(id.toString()));
		condition.put("type", Arrays.asList("tutor"));
		rd.getWithConditionString(Arrays.asList("graduatepublication_id"), condition);
		List<Integer> grapubIds = new ArrayList<Integer>();
		for (List<Object> objList : rd.getDataCache()) {
			grapubIds.add((Integer) objList.get(0));
		}
		return grapubIds;
	}

	@Override
	public List<GraduatePublication> getGraduatePublicationsByPersonIds(
			List<Integer> ids) {
		ReadFromDataBase rd = ReadFromDataBase.getIndependentInstance(ConsistanceService.get("db.defaultDatabase"), "person2graduatepublication");
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		List<String> idsToString  = new ArrayList<String>();
		for(Integer id:ids) {
			idsToString.add(id.toString());
		}
		map.put("person_id", idsToString);
		rd.getWithConditionString(Arrays.asList("graduatepublication_id"), map);
		List<String> grapubIds = new ArrayList<String>();
		for (List<Object> objList : rd.getDataCache()) {
			grapubIds.add((String)objList.get(0));
		}
		
		rd = ReadFromDataBase.getIndependentInstance(ConsistanceService.get("db.defaultDatabase"), "graduatepublication");
		map = new HashMap<String, List<String>>();
		map.put("id", grapubIds);
		rd.getWithConditionString(null, map);
		return rd.getWrappedList(GraduatePublication.class);
	}

	@Override
	public List<GraduatePublication> getGraduatePublications(
			List<Integer> grapubIds) {
		
		if(null == grapubIds) {
			return null;
		}
		if(grapubIds.size() == 0) {
			return new ArrayList<GraduatePublication>();
		}
		
		List<GraduatePublication> result = new ArrayList<GraduatePublication>();
		List<Integer> leftIDs = new ArrayList<Integer>();

		for(Integer grapubId : grapubIds) {
			GraduatePublication grapub = graduatePublicationCacheService.getGraduatePublicationFromCache(grapubId);
			if(null != grapub) {
				result.add(grapub);
			} else {
				leftIDs.add(grapubId);
			}
		}
		
		if(leftIDs.size() > 0) {
			List<GraduatePublication> graduatePublications = graduatePublicationDao.getGraduatePublicationsByIds(leftIDs);
			for(GraduatePublication grapub : graduatePublications) {
				graduatePublicationCacheService.setGraduatePublicationCache(grapub.id, grapub);
			}
			result.addAll(graduatePublications);
		}
		return result;
	}

	@Override
	public List<Person2GraduatePublication> getPersonGraduatePublicationsBygrapubId(
			Integer id) {
		List<Person2GraduatePublication> result = graduatePublicationCacheService.getPerson2GraduatePublicationListByGraPubId(id);
		if(null == result) {
			result = personGraduatePublicationDao.getPersonGraduatePublicationsByGrapubId(id);
			graduatePublicationCacheService.setPerson2GraduatePublicationIndexByGraPubId(id, result);
		}
		return result;
	}

	@Override
	public List<Person2GraduatePublication> getPersonGraduatePublicationsByPersonId(
			Integer person_id) {
		List<Person2GraduatePublication> result = graduatePublicationCacheService.getPerson2GraduatePublicationListByPersonId(person_id);
		if(null == result) {
			result = personGraduatePublicationDao.getPersonGraduatePublicationsByPersonId(person_id);
			graduatePublicationCacheService.setPerson2GraduatePublicationIndexByPersonId(person_id, result);
		}
		return result;
	}
	
//	@Override
//	public
	
	
	
	@Inject
	GraduatePublicationCacheService graduatePublicationCacheService;
//	public static void main(String args[]) {
//		GraduatePublicationServiceImpl test  = new GraduatePublicationServiceImpl();
//		List<GraduatePublication> result = test.getStudentGraduatePublicationsByPersonId(13);
//		for(GraduatePublication gp: result) {
//			System.out.println(gp.author);
//		}
//	}

@Override
	public List<GraduatePublication> getAllGraduatePublications() {
		List<GraduatePublication> result = graduatePublicationDao.walkAll();
		return result;
	}


}