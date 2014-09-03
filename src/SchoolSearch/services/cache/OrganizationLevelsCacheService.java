package SchoolSearch.services.cache;

import SchoolSearch.services.dao.oranizationLevels.model.Institute;

public interface OrganizationLevelsCacheService extends CacheService{
	void setInstituteCache(Integer key, Institute i);
	Institute getInstituteFromCache(Integer key);
}