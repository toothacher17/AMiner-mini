package SchoolSearch.services.services.person;

/**
 * @author GCR
 */
import java.util.List;
import java.util.Map;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonExt;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonInfo;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonInterest;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonProfile;

public interface PersonService {
	//person
	SchooltestPerson getPerson(Integer id);
	SchooltestPerson getPerson(Integer id, boolean forceLoadDB);
	List<SchooltestPerson> getPersonList(List<Integer> ids);
	List<SchooltestPerson> getPersonList(List<Integer> ids, boolean forceLoadDB);
	void createPerson(Integer id);
	void updatePerson(Integer id, String name, String name_alias, SchooltestPerson op);
	List<SchooltestPerson> getAllPersons();
	/**
	 * direct from db;
	 */
	List<SchooltestPerson> getPersonByName(String name);
	List<SchooltestPerson> getPersonByNameList(List<String> nameList);
	
	//person_profile
	SchooltestPersonProfile getPersonProfile(Integer id);
	List<SchooltestPersonProfile> getPersonProfile(List<Integer> idList);
	void createPersonProfile(Integer id);
	void updatePersonProfile(Integer id, String position, String location, String phone, String email, String homepage, Integer author_id, SchooltestPersonProfile op);
	void updatePersonProfileAvatar(Integer id, String avatar, SchooltestPersonProfile op);
	String getAvatar(Integer id); 
	
	
	//person_info
	SchooltestPersonInfo getPersonInfo(Integer id);
	void createPersonInfo(Integer id);
	void updatePersonInfo(Integer id, String field_name, String new_value, SchooltestPersonInfo op);
	
	//person_ext
	SchooltestPersonExt getPersonExt(Integer id);
	List<SchooltestPersonExt> getPersonExtList(List<Integer> ids);
	void createPersonExt(Integer id);
	void updatePersonExt(Integer id, String field_name, String new_value, SchooltestPersonExt op);
	List<Integer> getAllPersonExtIds();
	
	Map<String, List<Integer>> getTitleClustedPersons(List<Integer> ids);
	Map<String, List<Integer>> getTitleClustedPersons();

	//person interest
	SchooltestPersonInterest getPersonInterestByPersonId(Integer person_id);
	List<SchooltestPersonInterest> getPersonInterestByPersonIds(List<Integer> person_ids);
	SchooltestPersonInterest getPersonInterestByAid(Long aid);
	List<SchooltestPersonInterest> getPersonInterestByAids(List<Long> aids);
	
	//person relations		
	List<SchooltestPerson> getPersonListByDepartmentId(Integer id);
}
