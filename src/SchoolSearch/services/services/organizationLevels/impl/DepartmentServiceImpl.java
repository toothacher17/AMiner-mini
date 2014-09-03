package SchoolSearch.services.services.organizationLevels.impl;
/**
 * @author GCR
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.cache.OrganizationLevelsCacheService;
import SchoolSearch.services.dao.schooltest.dao.SchooltestDepartment2schoolDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestDepartmentDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPerson2departmentDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestSchoolDAO;
import SchoolSearch.services.dao.schooltest.model.SchooltestDepartment;
import SchoolSearch.services.dao.schooltest.model.SchooltestDepartment2school;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson2department;
import SchoolSearch.services.dao.schooltest.model.SchooltestSchool;
import SchoolSearch.services.services.organizationLevels.DepartmentService;
import SchoolSearch.services.services.person.PersonService;

public class DepartmentServiceImpl implements DepartmentService {

	@Override
	public Map<String, List<SchooltestDepartment>> getExistedDepartments() {
		Map<String, List<SchooltestDepartment>> result=  new HashMap<String, List<SchooltestDepartment>>();
		List<SchooltestSchool> allSchool = schoolDao.walkAll();
		for(SchooltestSchool s : allSchool) {
			List<SchooltestDepartment2school> tmpd2s = department2schoolDao.selectByIntegerField("school_id", s.getId());
			List<SchooltestDepartment> tmpd = new ArrayList<SchooltestDepartment>();
			for(SchooltestDepartment2school d2s : tmpd2s) {
				tmpd.add(departmentDao.selectById(d2s.getId()));
			}
			result.put(s.getSchoolName(), tmpd);
		}
		return result;
	}

	@Override
	public String getDepartmentById(Integer id){
		SchooltestDepartment department = departmentDao.selectById(id);
		if(null != department)
			return department.getName();
		else 
			return null;
	}
	
	@Override
	public List<SchooltestDepartment> getDepartmentsBySchoolId(Integer id) {
		List<SchooltestDepartment2school> tmpd2s = department2schoolDao.selectByIntegerField("school_id", id);
		List<SchooltestDepartment> tmpd = new ArrayList<SchooltestDepartment>();
		for(SchooltestDepartment2school d2s : tmpd2s) {
			tmpd.add(departmentDao.selectById(d2s.getId()));
		}
		return tmpd;
	}
	
	@Override
	public List<Integer> getDepartmentByPersonId(Integer id) {
		List<SchooltestPerson2department> temp = person2departmentDAO.getInstance().selectByIntegerField("person_id", id);
		List<Integer> result = new ArrayList<Integer>();
		for(SchooltestPerson2department p2d : temp) {
			result.add(p2d.getDepartmentId());
		}
		return result;
	}
	
	@Inject
	PersonService personService;

	@Inject
	SchooltestDepartmentDAO departmentDao;

	@Inject
	SchooltestDepartment2schoolDAO department2schoolDao;
	
	@Inject
	SchooltestPerson2departmentDAO person2departmentDAO;
	
	@Inject
	SchooltestSchoolDAO schoolDao;
	
	@Inject
	OrganizationLevelsCacheService orgCacheService;



	
}
