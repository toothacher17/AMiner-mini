package SchoolSearch.services.cache;

import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonExt;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonInfo;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonInterest;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonProfile;

public interface PersonCacheService extends CacheService{
	void setPersonCache(Integer key, SchooltestPerson p);
	SchooltestPerson getPersonFromCache(Integer key);
	
	void setPersonExtCache(Integer key, SchooltestPersonExt p);

	SchooltestPersonExt getPersonExtFromCache(Integer key);

	void setPersonInfoCache(Integer key, SchooltestPersonInfo p);

	SchooltestPersonInfo getPersonInfoFromCache(Integer key);

	void setPersonProfileCache(Integer key, SchooltestPersonProfile p);

	SchooltestPersonProfile getPersonProfileFromCache(Integer key);
	
	void setPersonInterestCache(Integer key, SchooltestPersonInterest p);

	SchooltestPersonInterest getPersonInterestFromCache(Integer key);
	
	void removePerson(Integer key);
	
	
}