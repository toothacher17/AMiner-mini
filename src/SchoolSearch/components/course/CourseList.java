package SchoolSearch.components.course;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.dao.schooltest.model.SchooltestCourse;
import SchoolSearch.services.services.course.CourseService;
import SchoolSearch.services.services.person.PersonService;
import SchoolSearch.services.utils.ComparatorUtil;

@Import (library = {"context:/res/js/d3.v2.min.js",//
		"context:/res/js/courseList/courseList.js"}
)


public class CourseList {
	@Parameter(allowNull = false)
	@Property
	List<Integer> courseIdList;
	
	@Property
	List<Integer> courseGroup;
	
	@Property
	SchooltestCourse course;
	
	@Property
	Integer index;
	
	@SetupRender
	Object setupRender() {
		index = 0;
		return true;
	}
	
	public List<SchooltestCourse> getCourseList() {
		List<SchooltestCourse> courseList = courseService.getCourse(courseIdList);
//		Collections.sort(courseList, Collections.reverseOrder(SchooltestCourse.semsterComparator));
		Collections.sort(courseList, Collections.reverseOrder(ComparatorUtil.semesterComparator));
		index += 1;
		return courseList;
	}

	
	@Inject
	PersonService personService;

	@Inject
	CourseService courseService;

}
