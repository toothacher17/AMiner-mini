package SchoolSearch.services.cache.impl;

import java.util.HashMap;
import java.util.Map;

import SchoolSearch.services.cache.OrganizationLevelsCacheService;
import SchoolSearch.services.dao.oranizationLevels.model.Institute;

public class OrganizationLevelsCacheServiceImpl implements OrganizationLevelsCacheService {
	Map<Integer, Institute> instituteCache = new HashMap<Integer, Institute>();

	@Override
	public void setInstituteCache(Integer key, Institute i) {
		instituteCache.put(key, i);
	}

	@Override
	public Institute getInstituteFromCache(Integer key) {
		return instituteCache.get(key);
	}

	@Override
	public void clear() {
		instituteCache.clear();
	}
}