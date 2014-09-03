package SchoolSearch.services.services.organizationLevels;

import java.util.List;
import java.util.Map;

import SchoolSearch.services.dao.oranizationLevels.model.Institute;

public interface InstituteService {
	Institute getInstitute(Integer id);
	
	List<Institute> getInstituteList(List<Integer> ids);
	
	Map<Integer, List<Integer>> getInstitute2PersonInDepartment(int departmentId);
	
	List<Integer> getInstituteIdListByPersonId(Integer pid);
	
	List<String> getAllSchoolNamesByInstituteId(Integer Institute_id);
}
