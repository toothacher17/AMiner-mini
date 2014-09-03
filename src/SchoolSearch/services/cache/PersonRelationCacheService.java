package SchoolSearch.services.cache;
/**
 * @author GCR
 */
import java.util.List;

public interface PersonRelationCacheService extends CacheService{
	
	void setDepartmentsPersonIdCache(Integer key, List<Integer> value);

	List<Integer> getDepartmentsPersonIdFromCache(Integer key);
	
	List<Integer> getInstituteIdsByPersonId(Integer personId);
	void setInstituteIdByPersosnId(Integer personId, List<Integer> instituteIdList);

}
