package SchoolSearch.services.cache.impl;
/**
 * @author GCR
 */
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import SchoolSearch.services.cache.PersonRelationCacheService;

public class PersonRelationCacheServiceImpl implements PersonRelationCacheService {

	Map<Integer, List<Integer>> departmentsPersonIdCache = new HashMap<Integer, List<Integer>>(); 
	
	@Override
	public void setDepartmentsPersonIdCache(Integer key, List<Integer> value) {
		departmentsPersonIdCache.put(key, value);
	}

	@Override
	public List<Integer> getDepartmentsPersonIdFromCache(Integer key) {
		return departmentsPersonIdCache.get(key);
	}

	Map<Integer, List<Integer>> personId2InstituteIdCache = new HashMap<Integer, List<Integer>>();

	@Override
	public List<Integer> getInstituteIdsByPersonId(Integer personId) {
		return personId2InstituteIdCache.get(personId);
	}

	@Override
	public void setInstituteIdByPersosnId(Integer personId,
			List<Integer> instituteIdList) {
		personId2InstituteIdCache.put(personId, instituteIdList);
	}

	@Override
	public void clear() {
		departmentsPersonIdCache.clear();
		personId2InstituteIdCache.clear();
	}

}
