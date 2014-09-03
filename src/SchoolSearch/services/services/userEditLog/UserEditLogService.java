package SchoolSearch.services.services.userEditLog;

import java.util.List;
import java.util.Map;

import SchoolSearch.services.dao.schooltest.model.SchooltestUseredit;

public interface UserEditLogService {

//	void singleEidt(Integer uid, String userName, String userType, String ip, String componentName, Integer personId, String filed, String original, String newvalue);
//	void multipleEdit(Integer uid, String userName, String userType, String ip, String componentName, Integer personId, Map<String, String> field2orignial, Map<String, String>field2new);
	void insert(SchooltestUseredit obj);
	void insertMultiple(List<SchooltestUseredit> objList);
	
	
	
}
