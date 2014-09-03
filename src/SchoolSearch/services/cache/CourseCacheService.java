package SchoolSearch.services.cache;

import java.util.List;

import SchoolSearch.services.dao.schooltest.model.SchooltestCourse;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;


public interface CourseCacheService extends CacheService{
	
	void setCourseCache(Integer key, SchooltestCourse c);
	SchooltestCourse getCourseFromCache(Integer key);
	
	void setCourseTeacherCache(Integer id, List<SchooltestPerson> courseTeachers);
	List<SchooltestPerson> getCourseTeacherFromCache(Integer id); 
	
//	the id is the teacher's id;
	void setRelatedCourseListCache(Integer id, List<SchooltestCourse> relatedCourseList );
	List<SchooltestCourse> getRelatedCourseListFromCache(Integer id);
}
