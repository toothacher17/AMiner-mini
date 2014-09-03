package SchoolSearch.services.monitor;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import SchoolSearch.services.dao.schooltest.model.SchooltestPersonProfile;
import SchoolSearch.services.dao.schooltest.model.SchooltestUseredit;

public interface MonitorService {
	void visitPage(String pageName, String info);

	// single edit, use to log single edit function like person info component
	void singleEditAction(String componentName, String field, String original, String newValue, Integer personId);

	
	
	// multiple edit, use to log multiple edit function like person profile
	// component
//	void multipleEditionAction(String componentName, SchooltestPersonProfile originalOne, SchooltestPersonProfile newOne);

	void multipleEditionAction(String componentName, Object original, Object newObj, List<Field> fieldList) throws IllegalArgumentException, IllegalAccessException;

//	void multipleEditionAction(String componentName, Map<String, String> field2orignial, Map<String, String> field2new, Integer personId);
}
