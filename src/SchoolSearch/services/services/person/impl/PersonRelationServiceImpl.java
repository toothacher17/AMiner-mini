package SchoolSearch.services.services.person.impl;
/**
 * @author GCR
 */
import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.cache.PersonRelationCacheService;
import SchoolSearch.services.dao.graduatePublication.GraduatePublicationDao;
import SchoolSearch.services.dao.graduatePublication.Person2GraduatePublicationDao;
import SchoolSearch.services.dao.oranizationLevels.DepartmentDao;
import SchoolSearch.services.dao.oranizationLevels.Institute2DepartmentDao;
import SchoolSearch.services.dao.oranizationLevels.Person2InstituteDao;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPersonrelationDAO;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonrelation;
import SchoolSearch.services.services.person.PersonRelationService;

public class PersonRelationServiceImpl implements PersonRelationService {

	@Override
	public List<Integer> getPersonIdByDepartmentId(Integer departmentId) {
		List<Integer> result = personRelationCacheService.getDepartmentsPersonIdFromCache(departmentId);
		if(null == result) {
			List<Integer> insIdList = ins2dDao.getInstituteIdsByDepartmentId(departmentId);
			result = person2insDao.getPersonIDsByInstituteIds(insIdList);
			personRelationCacheService.setDepartmentsPersonIdCache(departmentId, result);
		}
		return result;
	}
	
	
	
	@Override
	public List<List<Integer>> getPersonRelation(Integer id) {
	
		List<List<Integer>> personRelation = new ArrayList<List<Integer>>();
		
		List<Integer> graduation_publicationIds = person2GraduatePublicationDao.getGraduatePublicationIdsByPersonIdAndType(id, "author");
		List<Integer> tutor = person2GraduatePublicationDao.getPersonIdsByGraduatePublicationIdsAndType(graduation_publicationIds, "tutor");
		
		List<Integer> graduation_publicationIds2 = person2GraduatePublicationDao.getGraduatePublicationIdsByPersonIdAndType(id, "tutor");
		List<Integer> colleague = person2GraduatePublicationDao.getPersonIdsByGraduatePublicationIdsAndType(graduation_publicationIds2, "tutor");
		for(Integer grapubid : graduation_publicationIds2) {
			colleague.remove(grapubid);
		}
		List<Integer> student = person2GraduatePublicationDao.getPersonIdsByGraduatePublicationIdsAndType(graduation_publicationIds2, "author");
		
		personRelation.add(tutor);
		personRelation.add(colleague);
		personRelation.add(student);
		
		return personRelation;
	}
	
	@Override
	public List<SchooltestPersonrelation> getExistedPersonRelationByPersonId(Integer id) {
		return person2RelationDao.selectByIntegerField("personid1", id);
//				getExsitedPersonRelationByPersonId(id);
	}

	@Override
	public List<SchooltestPersonrelation> getPersonRelationByPersonId(Integer id) {
		return person2RelationDao.selectByIntegerField("personid1", id);
	}
	
	
	@Inject
	PersonRelationCacheService personRelationCacheService;

	@Inject
	DepartmentDao departmentDao;
	
	@Inject
	Institute2DepartmentDao ins2dDao;
	
	@Inject
	Person2InstituteDao person2insDao;
	
	@Inject
	Person2GraduatePublicationDao person2GraduatePublicationDao;
	
	@Inject
	GraduatePublicationDao graduatePublicationDao;

//	@Inject
	SchooltestPersonrelationDAO person2RelationDao = SchooltestPersonrelationDAO.getInstance();
}
