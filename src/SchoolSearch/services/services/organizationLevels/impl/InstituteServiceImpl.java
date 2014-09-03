package SchoolSearch.services.services.organizationLevels.impl;
/**
 * @author GCR
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.cache.OrganizationLevelsCacheService;
import SchoolSearch.services.cache.PersonRelationCacheService;
import SchoolSearch.services.dao.oranizationLevels.DepartmentDao;
import SchoolSearch.services.dao.oranizationLevels.Institute2DepartmentDao;
import SchoolSearch.services.dao.oranizationLevels.InstituteDao;
import SchoolSearch.services.dao.oranizationLevels.Person2InstituteDao;
import SchoolSearch.services.dao.oranizationLevels.SchoolDao;
import SchoolSearch.services.dao.oranizationLevels.model.Department;
import SchoolSearch.services.dao.oranizationLevels.model.Institute;
import SchoolSearch.services.dao.oranizationLevels.model.Person2Institute;
import SchoolSearch.services.services.organizationLevels.InstituteService;
import SchoolSearch.services.services.person.PersonService;
import SchoolSearch.services.utils.T5RegistryHelper;

public class InstituteServiceImpl implements InstituteService {
	
	@Override
	public Institute getInstitute(Integer id) {
		Institute instituteFromCache = orgCacheService.getInstituteFromCache(id);
		if(null == instituteFromCache) {
			instituteFromCache = instituteDao.getInstituteById(id);
			orgCacheService.setInstituteCache(id, instituteFromCache);
		}
		return instituteFromCache;
	}

	@Override
	public List<Institute> getInstituteList(List<Integer> ids) {
		if (null == ids)
			return null;
		if (ids.size() == 0)
			return new ArrayList<Institute>();
		List<Institute> result = new ArrayList<Institute>();
		List<Integer> leftIDs = new ArrayList<Integer>();
		for (Integer id : ids) {
			Institute ins = orgCacheService.getInstituteFromCache(id);
			if (null != ins) {
				result.add(ins);
			} else {
				leftIDs.add(id);
			}
		}
		if (leftIDs.size() > 0) {
			List<Institute> institutes = instituteDao.getInstitutesByIds(leftIDs);
			for (Institute ins : institutes) {
				orgCacheService.setInstituteCache(ins.id, ins);
			}
			result.addAll(institutes);
		}
		return result;

	}

	@Override
	public Map<Integer, List<Integer>> getInstitute2PersonInDepartment(int departmentId) {
		List<Integer> instituteIDList = institute2departmentDao.getInstituteIdsByDepartmentId(departmentId);
		List<Person2Institute> person2InstituteList = person2instituteDao.getPerson2InstituteByInstituteIds(instituteIDList);
		Map<Integer, List<Integer>> i2pMap = new HashMap<Integer, List<Integer>>();
		for (Person2Institute p2i : person2InstituteList) {
			if (!i2pMap.containsKey(p2i.institute_id))
				i2pMap.put(p2i.institute_id, new ArrayList<Integer>());
			i2pMap.get(p2i.institute_id).add(p2i.person_id);
		}
		return i2pMap;
	}

	@Override
	public List<Integer> getInstituteIdListByPersonId(Integer pid) {
		List<Integer> instituteIdsByPersonId = personRelationCacheService.getInstituteIdsByPersonId(pid);
		if(null == instituteIdsByPersonId) {
			instituteIdsByPersonId = person2instituteDao.getInstituteIdsByPersonId(pid);
			personRelationCacheService.setInstituteIdByPersosnId(pid, instituteIdsByPersonId);
		}
		return instituteIdsByPersonId;
	}
	
	@Override
	public List<String> getAllSchoolNamesByInstituteId(Integer Institute_id) {
		List<Integer> departmentIds = institute2departmentDao.getDepartmentIdsByInstituteId(Institute_id);
		Set<Integer> schoolIds = new HashSet<Integer>();
		for(Integer departmentId : departmentIds) {
			Department departmentById = departmentDao.getDepartmentById(departmentId);
			if(null == departmentById)
				System.out.println(Institute_id + "\t" + departmentId);
			schoolIds.add(departmentById.school_id);
		}
		List<String> schoolNames = new ArrayList<String>();
		for(Integer schoolId : schoolIds) {
			schoolNames.add(schoolDao.getSchoolById(schoolId).schoolname);
		}
		return schoolNames;
	}
	
	public static void main(String[] args) {
		InstituteService in = T5RegistryHelper.getService(InstituteService.class);
		List<String> names = in.getAllSchoolNamesByInstituteId(120);
		for(String name : names) {
			System.out.println(name);
		}
	}
	
	@Inject
	PersonService personService;

	@Inject
	Institute2DepartmentDao institute2departmentDao;

	@Inject
	DepartmentDao departmentDao;

	@Inject
	SchoolDao schoolDao;

	@Inject
	InstituteDao instituteDao;

	@Inject
	Person2InstituteDao person2instituteDao;

	@Inject
	PersonRelationCacheService personRelationCacheService;
	
	@Inject
	OrganizationLevelsCacheService orgCacheService;

	
}
