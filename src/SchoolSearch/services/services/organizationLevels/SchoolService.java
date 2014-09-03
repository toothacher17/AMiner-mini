package SchoolSearch.services.services.organizationLevels;

import java.util.List;

import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;


/**
 * 
 * @author guanchengran
 *
 */
public interface SchoolService {
	String getSchoolById(Integer id);
	
	List<Integer> getAllSchoolIds();
	
	List<Integer> getAllDepartmentIdsBySchoolId(Integer id);
	
	List<SchooltestPerson> getPersonsInSchoolBySchoolId(Integer id);
	
	List<String> getSchoolNamesByPersonId(Integer id);
}
