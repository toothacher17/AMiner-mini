package SchoolSearch.services.services.person;
/**
 * @author GCR
 */
import java.util.List;

import SchoolSearch.services.dao.person.model.PersonRelation;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonrelation;

public interface PersonRelationService {
	
	List<Integer> getPersonIdByDepartmentId(Integer departmentId);

	List<List<Integer>> getPersonRelation(Integer id);  
	
	List<SchooltestPersonrelation> getExistedPersonRelationByPersonId(Integer id);
	
	List<SchooltestPersonrelation> getPersonRelationByPersonId(Integer id);
}
