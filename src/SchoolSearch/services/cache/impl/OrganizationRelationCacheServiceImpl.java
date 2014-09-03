package SchoolSearch.services.cache.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import SchoolSearch.services.cache.OrganizationRelationCacheService;
import SchoolSearch.services.dao.schooltest.model.SchooltestDepartment;

public class OrganizationRelationCacheServiceImpl implements OrganizationRelationCacheService {
	
	

	Map<Integer, List<SchooltestDepartment>> departmentId2SchoolIdCache = new HashMap<Integer, List<SchooltestDepartment>>();  
	@Override
	public List<SchooltestDepartment> getDepartmentsBySchoolId(Integer schoolId) {
		return departmentId2SchoolIdCache.get(schoolId);
	}
	
	@Override
	public void setDepartmentBySchoolId(Integer schoolId,
			List<SchooltestDepartment> departmentList) {
		departmentId2SchoolIdCache.put(schoolId, departmentList);
	}
	
	@Override
	public void clear() {
		departmentId2SchoolIdCache.clear();
	}

	
}
