package SchoolSearch.services.services.publication;

/**
 * @author GCR
 */
import java.util.List;

import SchoolSearch.services.dao.schooltest.model.SchooltestPerson2publication;
import SchoolSearch.services.dao.schooltest.model.SchooltestPublication;


public interface PublicationService {
	
	List<SchooltestPublication> getPublicationsByPersonId(Integer id);
	
	List<Integer> getPublicationsIdListByPersonId(Integer id);

	List<SchooltestPublication> getPublications(List<Integer> pubids);
	
	List<SchooltestPerson2publication> getPersonPublicationsByPublicationId(Integer id);
	
	List<SchooltestPerson2publication> getPersonPublicationsByPersonId(Integer person_id);
	List<SchooltestPerson2publication> getPersonPublicationsByPersonIds(List<Integer> person_ids);
	
	void updatePerson2Publication(Integer personId, Integer publicationId);
	
//	List<SchooltestPublication> sortByScore(List<SchooltestPublication> input);
		
	
	
}
