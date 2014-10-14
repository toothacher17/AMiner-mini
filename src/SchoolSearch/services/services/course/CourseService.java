package SchoolSearch.services.services.course;
/**
 * @author GCR
 */
import java.util.List;

import SchoolSearch.services.dao.schooltest.model.SchooltestCourse;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;




public interface CourseService {
	
	SchooltestCourse getCourse(Integer id);
	List<SchooltestCourse> getCourse(List<Integer> courseIdList);
	List<List<List<SchooltestCourse>>> getCourseList(List<String> courseIdList);
	List<SchooltestPerson> getCourseTeacher (Integer id);

	//get related courseList by personId	
	List<SchooltestCourse> getRelatedCourseList(Integer id);
	String getCourseUrl(Integer id);
	
	void updatePerson2Course(Integer personId, Integer courseId);
}
