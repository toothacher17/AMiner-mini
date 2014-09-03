package SchoolSearch.components.person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.dao.schooltest.model.SchooltestCourse;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;
import SchoolSearch.services.services.course.CourseService;
import SchoolSearch.services.services.person.PersonService;
import SchoolSearch.services.services.publication.PublicationService;
import SchoolSearch.services.utils.ComparatorUtil;
import SchoolSearch.services.utils.Strings;

public class PersonCourseList {
	
	@Parameter(allowNull = true)
	Integer personId;

	@Property
	Integer _personId;

	@Property
	SchooltestPerson person;

	@Property
	Integer index;

	@Property
	SchooltestCourse course;
	
	@Property
	List<SchooltestCourse> courseList;
	
	@Property
	Integer number;
	
	@Property
	String semester;
	
	
	@SetupRender
	void setupRender() {
		this._personId = personId;
	}
	
	public List<SchooltestCourse> getAllShowedCourseList(){
		List<SchooltestCourse> temp = courseService.getRelatedCourseList(_personId);
//		Collections.sort(temp,Collections.reverseOrder(SchooltestCourse.semsterComparator));
		Collections.sort(temp,Collections.reverseOrder(ComparatorUtil.semesterComparator));
		return temp;
	}
	
	
	public List<String> getAllSemesternameList() {
		List<String> result = new ArrayList<String>();
		String[] temp = course.getSemesterName().split("\\|\\|");
		for(String t : temp) {
			result.add(t);
		}
		return result;
	}
	
//	public List<List<SchooltestCourse>> getAllCollapsedCourseList(){
//		List<List<SchooltestCourse>> result = new ArrayList<List<SchooltestCourse>>();
//		List<SchooltestCourse> original = courseService.getRelatedCourseList(_personId);
//		for(SchooltestCourse eachCourse : original){
//			boolean flag = false;
//			for(List<SchooltestCourse> courseList : result){
//				if(eachCourse.getCourseName().equals(courseList.get(0).getCourseName())){
//					courseList.add(eachCourse);
//					flag = true;
//				}
//			}
//			if(flag == false){
//				List<SchooltestCourse> temp = new ArrayList<SchooltestCourse>();
//				temp.add(eachCourse);
//				result.add(temp);
//			}
//		}
//		for(List<SchooltestCourse> tempResultList : result){
//			Collections.sort(tempResultList,Collections.reverseOrder(SchooltestCourse.semsterComparator));
//		}
//		this.number = result.size();
//		return result;
//	}
	
	public List<SchooltestCourse> getAllCourseList() {
		this.number = courseService.getRelatedCourseList(_personId).size();
		return courseService.getRelatedCourseList(_personId);
	}
	
	public String geturl() {
		return course.getUrl();
//		return courseService.getCourseUrl(course.getId());
	}
	
	public Integer getLineNumber(){
		return number - index;
	}
	
	
	@Inject
	PersonService personService;

	@Inject
	CourseService courseService;
}
