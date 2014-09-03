package SchoolSearch.services.dao.course.model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author guanchengran
 *
 */
public class Course {
	
	public Integer id;
	public Integer courseId;
	public String courseName;
	public String courseNo;
	public String teacherId;
	public String teacherName;
	public String majorId;
	public String majorName;
	public Integer browseTimes;
	public Integer semesterId;
	public String semesterName;
	public String openType;
	public Integer studentNum;
	
	public String sex;
	public String position;
	public String duty;
	public String telephone;
	public String email;
	public String address;
	public Integer postcode;
	public String personProfile;
	public String courseDescription;
	public String teachingMaterial;
	public Integer credit;
	public Integer class_hour;
	public String checking;
	public Integer teachingMaterialNum;
	public Integer paperNum;
	public String homework;
	public String url;
	
	public static Comparator<Course> semesterComparator = new Comparator<Course>() {
		@Override
		public int compare(Course o1, Course o2) {
			return o1.semesterId.compareTo(o2.semesterId);
		}
	}; 
}
