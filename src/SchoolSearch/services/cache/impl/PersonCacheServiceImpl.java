package SchoolSearch.services.cache.impl;
/**
 * @author GCR
 */
import java.util.HashMap;
import java.util.Map;

import SchoolSearch.services.cache.PersonCacheService;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonExt;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonInfo;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonInterest;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonProfile;

public class PersonCacheServiceImpl implements PersonCacheService {
		Map<Integer, SchooltestPerson> personCache = new HashMap<Integer, SchooltestPerson>();

		@Override
		public void setPersonCache(Integer key, SchooltestPerson person) {
			personCache.put(key, person);
		}
		@Override
		public SchooltestPerson getPersonFromCache(Integer key) {
			return personCache.get(key);
		}
		
		Map<Integer, SchooltestPersonExt> personExtCache = new HashMap<Integer, SchooltestPersonExt>();

		@Override
		public void setPersonExtCache(Integer key, SchooltestPersonExt p) {
			personExtCache.put(key, p);
		}

		@Override
		public SchooltestPersonExt getPersonExtFromCache(Integer key) {
			return personExtCache.get(key);
		}

		Map<Integer, SchooltestPersonInfo> personInfoCache = new HashMap<Integer, SchooltestPersonInfo>();

		@Override
		public void setPersonInfoCache(Integer key, SchooltestPersonInfo p) {
			personInfoCache.put(key, p);
		}

		@Override
		public SchooltestPersonInfo getPersonInfoFromCache(Integer key) {
			return personInfoCache.get(key);
		}

		Map<Integer, SchooltestPersonProfile> personProfileCache = new HashMap<Integer, SchooltestPersonProfile>();

		@Override
		public void setPersonProfileCache(Integer key, SchooltestPersonProfile p) {
			personProfileCache.put(key, p);
		}		

		@Override
		public SchooltestPersonProfile getPersonProfileFromCache(Integer key) {
			return personProfileCache.get(key);
		}	
		
		Map<Integer, SchooltestPersonInterest> personInterestCache = new HashMap<Integer, SchooltestPersonInterest>();
		
		@Override
		public void setPersonInterestCache(Integer key, SchooltestPersonInterest p) {
			personInterestCache.put(key, p);
			
		}
		@Override
		public SchooltestPersonInterest getPersonInterestFromCache(Integer key) {
			return personInterestCache.get(key);
		}
		
		@Override
		public void clear() {
			personCache.clear();
			personExtCache.clear();
			personInfoCache.clear();
			personProfileCache.clear();
		}
		
		@Override
		public void removePerson(Integer key) {
			personCache.remove(key);
			personExtCache.remove(key);
			personInfoCache.remove(key);
			personProfileCache.remove(key);
		}
	}