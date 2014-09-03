package SchoolSearch.services.cache.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import SchoolSearch.services.cache.CourseCacheService;
import SchoolSearch.services.dao.schooltest.model.SchooltestCourse;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;

public class CourseCacheServiceImpl implements CourseCacheService {

	Map<Integer, SchooltestCourse> courseCache = new HashMap<Integer, SchooltestCourse>();
	Map<Integer, List<SchooltestPerson>> course2teacherCache = new HashMap<Integer, List<SchooltestPerson>>();
	Map<Integer, List<SchooltestCourse>> relatedCourseMap = new HashMap<Integer, List<SchooltestCourse>>();
 	 
	@Override
	public void clear() {
		courseCache.clear();
	}

	@Override
	public void setCourseCache(Integer key, SchooltestCourse c) {
		courseCache.put(key, c);
		
	}

	@Override
	public SchooltestCourse getCourseFromCache(Integer key) {
		return courseCache.get(key);
	}

	@Override
	public void setCourseTeacherCache(Integer id, List<SchooltestPerson> courseTeachers) {
		// TODO Auto-generated method stub
		course2teacherCache.put(id, courseTeachers);
	}

	@Override
	public List<SchooltestPerson> getCourseTeacherFromCache(Integer id) {
		// 
		return course2teacherCache.get(id);
	}

	@Override
	public void setRelatedCourseListCache(Integer id, List<SchooltestCourse> relatedCourseList) {
		relatedCourseMap.put(id, relatedCourseList);
	}

	@Override
	public List<SchooltestCourse> getRelatedCourseListFromCache(Integer id) {
		return relatedCourseMap.get(id);
	}

}
