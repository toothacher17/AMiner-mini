package SchoolSearch.services.services.organizationLevels.impl;

/**
 * @author GCR
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.cache.OrganizationRelationCacheService;
import SchoolSearch.services.dao.schooltest.dao.SchooltestDepartment2schoolDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestDepartmentDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPerson2departmentDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPersonDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestSchoolDAO;
import SchoolSearch.services.dao.schooltest.model.SchooltestDepartment;
import SchoolSearch.services.dao.schooltest.model.SchooltestDepartment2school;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson2department;
import SchoolSearch.services.dao.schooltest.model.SchooltestSchool;
import SchoolSearch.services.services.organizationLevels.DepartmentService;
import SchoolSearch.services.services.organizationLevels.InstituteService;
import SchoolSearch.services.services.organizationLevels.SchoolService;

import SchoolSearch.services.utils.T5RegistryHelper;
import SchoolSearch.services.services.person.PersonService;

public class SchoolServiceImpl implements SchoolService {

	@Override
	public String getSchoolById(Integer id) {
		SchooltestSchool school = schoolDao.selectById(id);
		if(null != school) 
			return school.getSchoolName();
		else
			return null;
		
	}

	@Override
	public List<Integer> getAllSchoolIds() {
		List<SchooltestSchool> schoolList = schoolDao.walkAll();
		List<Integer> schoolIds = new ArrayList<Integer>();
		for(SchooltestSchool school : schoolList) {
			schoolIds.add(school.getId());
		}
		return schoolIds;
	}
	
//	@Override
//	public List<SchooltestPerson> getPersonsInSchoolBySchoolId(Integer id) {
//		List<Integer> instituteIdList = new ArrayList<Integer>();
//		List<Integer> personIdList = new ArrayList<Integer>();
//		List<SchooltestPerson> personList = new ArrayList<SchooltestPerson>();
//		List<SchooltestPerson> allPersonList = new ArrayList<SchooltestPerson>();
//		List<Integer> allDepartmentIds = getAllDepartmentIdsBySchoolId(id);
//	
//		for(Integer departmentId : allDepartmentIds) {
//			instituteIdList = organizationRelationCacheService.getInstituteIdsByDepartmentId(departmentId);
//			if(null == instituteIdList) {
//				instituteIdList = institute2DepartmentDao.getInstituteIdsByDepartmentId(departmentId);
//				organizationRelationCacheService.setInstituteIdsByDepartmentId(departmentId, instituteIdList);
//			}
//		
//			personIdList = person2InstituteDao.getPersonIDsByInstituteIds(instituteIdList);
//			personList = personService.getPersonList(personIdList);
//			if(null == personList) {
//				personList = personDao.getPersonsByIdList(personIdList);
//			}
//			allPersonList.addAll(personList);		
//		}
//		return allPersonList;
//	
//	}
	
//	select all the person in a school from a school id
	@Override
	public List<SchooltestPerson> getPersonsInSchoolBySchoolId(Integer id) {
		List<SchooltestPerson> result = new ArrayList<SchooltestPerson>();
		List<Integer> allDepartmentIds = getAllDepartmentIdsBySchoolId(id);
		List<SchooltestPerson> tmpSchoolPersonList = new ArrayList<SchooltestPerson>();
		for(Integer departmentId : allDepartmentIds) {
			tmpSchoolPersonList = personService.getPersonListByDepartmentId(departmentId);
			result.addAll(tmpSchoolPersonList);
		}
		return result;
	}
	
	
	
	@Override
	public List<Integer> getAllDepartmentIdsBySchoolId(Integer id) {
		List<Integer> allDepartmentIds = new ArrayList<Integer>();
		if(null == id) 
			return null;
		List<SchooltestDepartment> allDepartments = organizationRelationCacheService.getDepartmentsBySchoolId(id);
		
		if(null == allDepartments) {
			allDepartments = departmentService.getDepartmentsBySchoolId(id);
			organizationRelationCacheService.setDepartmentBySchoolId(id, allDepartments);
		}
		for(SchooltestDepartment department : allDepartments) {
			allDepartmentIds.add(department.getId());
		}
		return allDepartmentIds;
	}
	
//	@Override
//	public List<String> getSchoolNamesByPersonId(Integer id) {
//		List<Integer> theInstituteIdList = instituteService.getInstituteIdListByPersonId(id);
//		List<String> result = new ArrayList<String>();
//		for(Integer idTemp : theInstituteIdList ){
//			List<String> schoolNamesTemp = instituteService.getAllSchoolNamesByInstituteId(idTemp);
//			for(String schoolNameTemp : schoolNamesTemp){
//				if(!result.contains(schoolNameTemp)){
//					result.add(schoolNameTemp);
//				}
//			}
//		}
//		return result;
//	}
	
	@Override
	public List<String> getSchoolNamesByPersonId(Integer id) {
		List<Integer> allDepartmentId = departmentService.getDepartmentByPersonId(id);
		Map<Integer, Integer> department2school = new HashMap<Integer, Integer>();
		List<SchooltestDepartment2school> temp = department2schoolDao.walkAll();
		for(SchooltestDepartment2school d2s : temp) {
			department2school.put(d2s.getId(), d2s.getSchoolId());
		}
		List<String> result = new ArrayList<String>();
		for(Integer d : allDepartmentId) {
			result.add(schoolDao.selectById(department2school.get(d)).getSchoolName());
		}
		return result;
	} 
	
	
	public static void main(String args[]) {
		SchoolService s = T5RegistryHelper.getService(SchoolService.class);
		List<SchooltestPerson> personList = s.getPersonsInSchoolBySchoolId(14);
		System.out.println(personList.size());
//		for(Person p : personList) {
//			System.out.println(p.name);
//		}
	}
	
	@Inject
	PersonService personService;
	
	@Inject
	DepartmentService departmentService;
	
	@Inject
	OrganizationRelationCacheService organizationRelationCacheService;
	
	@Inject 
	SchooltestSchoolDAO schoolDao;
	
	@Inject
	SchooltestDepartmentDAO departmentDao;
	
	@Inject
	SchooltestDepartment2schoolDAO department2schoolDao;

	@Inject
	SchooltestPersonDAO personDao;
	
	@Inject
	SchooltestPerson2departmentDAO person2departmentDao;
	
}
