package SchoolSearch.services.cache;

import java.util.List;

import SchoolSearch.services.dao.schooltest.model.SchooltestDepartment;


public interface OrganizationRelationCacheService extends CacheService{
	
	
	List<SchooltestDepartment> getDepartmentsBySchoolId(Integer schoolId);
	void setDepartmentBySchoolId(Integer schoolId, List<SchooltestDepartment> departmentList);
	
}