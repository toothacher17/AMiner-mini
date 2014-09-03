package courseExtraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import SchoolSearch.services.dao.course.CourseDao;
import SchoolSearch.services.dao.course.model.Course;

public class InsertDB {
	
	public static void insertCourseInfo(String filepath) {
		
		List<Course> courseList = new ArrayList<Course>();
		
		File file = new File(filepath);
		BufferedReader reader = null;  
		try {
			reader = new BufferedReader(new FileReader(file));
			String content = null;
			
			while ((content = reader.readLine()) != null) {
				Course course = new Course();
				String[] courseInfo = content.split("\\|\\|");
				if(courseInfo[0].trim().equals("null") || courseInfo[0].trim().equals("")) {
					continue;
				}
				course.courseId = Integer.parseInt(courseInfo[0]);
				course.courseName = courseInfo[1];
				course.courseNo = courseInfo[2];
				course.teacherId = courseInfo[3];
				course.teacherName = courseInfo[4];
				course.majorId = courseInfo[5];
				course.majorName = courseInfo[6];
				if(courseInfo[7].trim().equals("null") || courseInfo[7].trim().equals("")) {
					course.browseTimes = null;
				} else {
					course.browseTimes = Integer.parseInt(courseInfo[7]);
				}
				if(courseInfo[8].trim().equals("null") || courseInfo[8].trim().equals("")) {
					course.semesterId = null;
				} else {
					course.semesterId = Integer.parseInt(courseInfo[8]);
				}
				course.semesterName = courseInfo[9];
				course.openType = courseInfo[10];
				if(courseInfo[11].trim().equals("null") || courseInfo[11].trim().equals("")) {
					course.studentNum = null;
				} else {
					course.studentNum = Integer.parseInt(courseInfo[11]);
				}
				
				course.sex = courseInfo[12];
				course.position = courseInfo[13];
				course.duty = courseInfo[14];
				course.telephone = courseInfo[15];
				course.email = courseInfo[16];
				course.address = courseInfo[17];
				if(courseInfo[18].trim().equals("null") || courseInfo[18].trim().equals("")) {
					course.postcode = null;
				} else {
					course.postcode = Integer.parseInt(courseInfo[18]);
				}
				course.personProfile = courseInfo[19];
				course.courseDescription = courseInfo[20];
				course.teachingMaterial = courseInfo[21];
				if(courseInfo[22].trim().equals("null") || courseInfo[22].trim().equals("")) {
					course.credit = null;
				} else {
					course.credit = Integer.parseInt(courseInfo[22]);
				}
				if(courseInfo[23].trim().equals("null") || courseInfo[23].trim().equals("")) {
					course.class_hour = null;
				} else {
					course.class_hour = Integer.parseInt(courseInfo[23]);
				}
				course.checking = courseInfo[24];
				if(courseInfo[25].trim().equals("null") || courseInfo[25].trim().equals("")) {
					course.teachingMaterialNum =null;
				} else {
					course.teachingMaterialNum = Integer.parseInt(courseInfo[25]);
				}
				if(courseInfo[26].trim().equals("null") || courseInfo[26].trim().equals("") || courseInfo[26].trim().equals("法学院")) {
					course.paperNum = null;
				} else {
					course.paperNum = Integer.parseInt(courseInfo[26]);
				}
				course.homework = courseInfo[27];
				course.url = courseInfo[28];
				courseList.add(course);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		CourseDao courseDao = CourseDao.getInstance();
		courseDao.insertBatch(courseList);
	}
	
	public static void main(String args[]) {
		insertCourseInfo("/Users/guanchengran/work/myEclipse/SchoolSearch2/courseinfodata.txt");
	}
}
