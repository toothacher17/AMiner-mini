package SchoolSearch.services.utils.dataUpdateTools.utils;

import java.util.HashMap;
import java.util.Map;

import SchoolSearch.services.dao.course.model.Course;
import SchoolSearch.services.dao.editLog.model.EditLog;
import SchoolSearch.services.dao.graduatePublication.model.GraduatePublication;
import SchoolSearch.services.dao.oranizationLevels.model.Department;
import SchoolSearch.services.dao.oranizationLevels.model.Institute;
import SchoolSearch.services.dao.oranizationLevels.model.Person2Institute;
import SchoolSearch.services.dao.oranizationLevels.model.School;
import SchoolSearch.services.dao.person.model.Person;
import SchoolSearch.services.dao.person.model.PersonExt;
import SchoolSearch.services.dao.person.model.PersonInfo;
import SchoolSearch.services.dao.person.model.PersonInterest;
import SchoolSearch.services.dao.person.model.PersonProfile;
import SchoolSearch.services.dao.publication.model.Person2Publication;
import SchoolSearch.services.dao.publication.model.Publication;
import SchoolSearch.services.dao.user.model.User;
import SchoolSearch.services.dao.userLog.model.UserLog;

public class DataUpdateConstants {
	static Map<Class<?>, TableConfig> classMap = new HashMap<Class<?>, TableConfig>();
	
	static {
		classMap.put(Person.class, new TableConfig("person"));
		classMap.put(Department.class, new TableConfig("department"));
		classMap.put(Institute.class, new TableConfig("institute"));
		classMap.put(Course.class, new TableConfig("course"));
		classMap.put(EditLog.class, new TableConfig("editlog"));
		classMap.put(GraduatePublication.class, new TableConfig("graduatepublication"));
		classMap.put(PersonExt.class, new TableConfig("person_ext"));
		classMap.put(PersonInfo.class, new TableConfig("person_info"));
		classMap.put(PersonInterest.class, new TableConfig("person_interest"));
		classMap.put(PersonProfile.class, new TableConfig("person_profile"));
		classMap.put(Publication.class, new TableConfig("publication"));
		classMap.put(School.class, new TableConfig("school"));
		classMap.put(User.class, new TableConfig("user"));
		classMap.put(UserLog.class, new TableConfig("userlog"));
		classMap.put(Person2Institute.class, new TableConfig("person2institute"));
		classMap.put(Person2Publication.class, new TableConfig("person2publication"));
		
	}
	
	public static class TableConfig {
		public TableConfig(String tableName) {
			super();
			this.tableName = tableName;
		}
		String tableName;
	}
	
	public static <T> String getTableName(Class<T> type) {
		if(classMap.containsKey(type)) {
			return classMap.get(type).tableName;
		}else{
			return null;
		}
	}
	
	public static void main(String[] args) {
		System.out.println(getTableName(Person2Publication.class));
	}
}
