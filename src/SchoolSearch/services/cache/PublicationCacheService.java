package SchoolSearch.services.cache;
/**
 * @author GCR
 */
import java.util.List;


import SchoolSearch.services.dao.schooltest.model.SchooltestPerson2publication;
import SchoolSearch.services.dao.schooltest.model.SchooltestPublication;



public interface PublicationCacheService extends CacheService {
	
	void setPerson2PublicationIndexByPubId(Integer pubId, List<SchooltestPerson2publication> value);
	List<SchooltestPerson2publication> getPerson2PublicationListByPubId(Integer pubId);
	
	void setPerson2PublicationIndexByPersonId(Integer personId, List<SchooltestPerson2publication> value);
	List<SchooltestPerson2publication> getPerson2PublicationListByPersonId(Integer personId);
	
	void setPublicationCache(Integer key, SchooltestPublication value);
	SchooltestPublication getPublicationFromCache(Integer key);
	
	void setPublicationIdsByPersonId(Integer key, List<Integer> value);
	List<Integer> getPublicationIdsByPersonId(Integer key);
	
	void setPublicationByPubId(Integer key, SchooltestPublication value);
	SchooltestPublication getPublicationByPubId(Integer key);
	
}
