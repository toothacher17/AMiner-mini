package SchoolSearch.services.cache;
/**
 * @author GCR
 */
import java.util.List;

import SchoolSearch.services.dao.graduatePublication.model.GraduatePublication;
import SchoolSearch.services.dao.graduatePublication.model.Person2GraduatePublication;

public interface GraduatePublicationCacheService extends CacheService {
	void setPerson2GraduatePublicationIndexByGraPubId(Integer grapubId, List<Person2GraduatePublication> value);
	List<Person2GraduatePublication> getPerson2GraduatePublicationListByGraPubId(Integer grapubId);
	
	void setPerson2GraduatePublicationIndexByPersonId(Integer personId, List<Person2GraduatePublication> value);
	List<Person2GraduatePublication> getPerson2GraduatePublicationListByPersonId(Integer personId);
	
	void setGraduatePublicationCache(Integer key, GraduatePublication value);
	GraduatePublication getGraduatePublicationFromCache(Integer key);
}
