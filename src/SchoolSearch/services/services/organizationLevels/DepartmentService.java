package SchoolSearch.services.services.organizationLevels;
/**
 * @author GCR
 */
import java.util.List;
import java.util.Map;

import SchoolSearch.services.dao.schooltest.model.SchooltestDepartment;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson2department;
import SchoolSearch.services.dao.schooltest.model.SchooltestSchool;


public interface DepartmentService {

	Map<String, List<SchooltestDepartment>> getExistedDepartments();

	String getDepartmentById(Integer id);
	
	List<SchooltestDepartment> getDepartmentsBySchoolId(Integer id);
	
	List<Integer> getDepartmentByPersonId(Integer id);
	
}
