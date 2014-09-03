package SchoolSearch.pages.course;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.dao.course.model.Course;
import SchoolSearch.services.dao.person.model.Person;
import SchoolSearch.services.dao.person.model.PersonProfile;
import SchoolSearch.services.dao.schooltest.model.SchooltestCourse;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonProfile;
import SchoolSearch.services.monitor.MonitorService;
import SchoolSearch.services.services.course.CourseService;
import SchoolSearch.services.services.person.PersonService;
import SchoolSearch.services.utils.ComparatorUtil;

@Import (library = {"context:/res/js/d3.v2.min.js",//
"context:/res/js/courseList/courseIndex.js"}
)

public class CourseIndex {
	@Property
	Integer courseId;
	
	@Property
	SchooltestCourse course;

	@Property
	List<SchooltestPerson> courseTeachers;
	
	@Property
	Person person;
	
	@Property
	PersonProfile personProfile;
	
//	@Property
//	List<List<Course>> relatedCourseFullList;
	
	@Property
	List<Course> relatedCoursePartList;
	
	@Property
	Course relatedCourse;
	
	public Object onActivate(EventContext context) {
		if(context.getCount()>0)
			courseId = context.get(Integer.class, 0);
		return true;
	}
	
	@SetupRender
	void setupRender() {
		monitor.visitPage("CourseIndex", courseId.toString());
		course = courseService.getCourse(courseId);
		courseTeachers = courseService.getCourseTeacher(courseId);
		
	}
	
	public boolean courseDescriptionIsNull(){
//		boolean falg = false;
//		if(course.courseDescription.equals("null")||course.courseDescription==null){
//			falg = true;
//		}else{
//			falg = false;
//		}
//		return falg;
		return true;
	}
	
	public SchooltestPersonProfile getThePersonProfile(){
		SchooltestPersonProfile thePersonProfile = personService.getPersonProfile(person.id);
		return thePersonProfile;
	}
	
	public List<List<SchooltestCourse>> getTheRelatedCourseList(){
		List<SchooltestCourse> original = courseService.getRelatedCourseList(person.id);
		List<List<SchooltestCourse>> result = new ArrayList<List<SchooltestCourse>>();
		for (SchooltestCourse eachCourse : original){
			boolean flag = false;
			for (List<SchooltestCourse> courseList : result ){
				if (eachCourse.getCourseName().equals(courseList.get(0).getCourseName())){
					courseList.add(eachCourse);
					flag=true;
				}
			}
			if (flag==false){
				List<SchooltestCourse> temp = new ArrayList<SchooltestCourse>();
				temp.add(eachCourse);
				result.add(temp);
			}
		}
		for (List<SchooltestCourse> tempResultList : result ){
			Collections.sort(tempResultList,Collections.reverseOrder(ComparatorUtil.semesterComparator));
		}
		return result;
	}
	
	@Inject
	PersonService personService;
	
	@Inject
	CourseService courseService;

	@Inject
	private MonitorService monitor;

}
