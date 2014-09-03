package courseExtraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import SchoolSearch.services.dao.course.CourseDao;
import SchoolSearch.services.dao.course.Person2CourseDao;
import SchoolSearch.services.dao.course.model.Course;
import SchoolSearch.services.dao.course.model.Person2Course;
import SchoolSearch.services.dao.oranizationLevels.DepartmentDao;
import SchoolSearch.services.dao.oranizationLevels.SchoolDao;
import SchoolSearch.services.dao.person.PersonDao;
import SchoolSearch.services.dao.person.PersonProfileDao;
import SchoolSearch.services.dao.person.model.Person;

public class Person2CourseTransfer {
	
	public static void person2CourseTransfer() {
		
		List<Person2Course> personCourseList = new ArrayList<Person2Course>();
		Person2CourseDao personCourseDao = Person2CourseDao.getInstance();
		
		PersonDao personDao = PersonDao.getInstance();
		PersonProfileDao personProfileDao = PersonProfileDao.getInstance();
		CourseDao courseDao = CourseDao.getInstance();
		
		System.out.println("walking all person");
		Long t1 = System.currentTimeMillis();
		List<Person> personAll = personDao.walkAll();
		Map<String, List<Integer>> getPersonByName = new HashMap<String, List<Integer>>();
		for(Person person : personAll) {
			if(!getPersonByName.containsKey(person.name)) {
				getPersonByName.put(person.name, new ArrayList<Integer>());
			}
			getPersonByName.get(person.name).add(person.id);
		}
		System.out.println("花费时间：" + (System.currentTimeMillis()-t1));
		
		
		System.out.println("walking all course");
		Long t2 = System.currentTimeMillis();
		List<Course> courseAll = courseDao.walkAll();
		System.out.println("花费时间：" + (System.currentTimeMillis()-t2));
		System.out.println("course num:" + courseAll.size());
		
		DepartmentDao departmentDao = DepartmentDao.getInstance();
		SchoolDao schoolDao = SchoolDao.getInstance();
		
		Map<Integer, List<Course>> getCourseByDepartmentId = new HashMap<Integer, List<Course>>();
		Map<Integer, List<Course>> getCourseBySchoolId = new HashMap<Integer, List<Course>>();
		List<Course> getCourseByOtherDepartment = new ArrayList<Course>();
		
		int count = 0;
		for(Course course : courseAll) {
			
			count ++;
			
			Integer departmentId = null;
			Integer schoolId = null;
			
			if(null != departmentDao.getDepartmentByDepartmentName(course.majorName)) {
				departmentId = departmentDao.getDepartmentByDepartmentName(course.majorName).id;
				
				if(!getCourseByDepartmentId.containsKey(departmentId)) {
					getCourseByDepartmentId.put(departmentId, new ArrayList<Course>());
				}
				getCourseByDepartmentId.get(departmentId).add(course);
				
			} else if(null != schoolDao.getSchoolByName(course.majorName)) {
				schoolId = schoolDao.getSchoolByName(course.majorName).id;
				
				if(!getCourseBySchoolId.containsKey(schoolId)) {
					getCourseBySchoolId.put(schoolId, new ArrayList<Course>());
				}
				getCourseBySchoolId.get(schoolId).add(course);
			} else {
				getCourseByOtherDepartment.add(course);
			}
			
			
			System.out.println("建立map的进度：" + (double)count/courseAll.size());
			
		}
		
		
		System.out.println("开始根据department进行匹配");
		System.out.println("共有" + getCourseByDepartmentId.keySet().size() +"个department");
		int count1 = 1;
		for(Integer departmentId : getCourseByDepartmentId.keySet()) {
			
			System.out.println("正在进行第" + count1 + "个department匹配");
			List<Integer> personIdsByDepartment = personProfileDao.getPersonIdsByDepartmentId(departmentId);
			List<Person> personList = personDao.getPersonsByIdList(personIdsByDepartment);
			Map<String, List<Integer>> persons = new HashMap<String, List<Integer>>();
			for(Person person : personList) {
				if(!persons.containsKey(person.name)) {
					persons.put(person.name, new ArrayList<Integer>());
				}
				persons.get(person.name).add(person.id);
			}
			System.out.println("部门人物提取完毕，开始课程匹配");
			System.out.println("该部门课程数共" + getCourseByDepartmentId.get(departmentId).size() + "个");
			int courseCount = 1;
			for(Course course : getCourseByDepartmentId.get(departmentId)) {
				if(persons.containsKey(course.teacherName)) {
					Person2Course person2Course = new Person2Course();
					for(Integer personId : persons.get(course.teacherName)) {
						person2Course.person_id = personId;
						person2Course.course_id = course.id;
						person2Course.semesterId = course.semesterId;
						person2Course.rank = "A";
						personCourseList.add(person2Course);
					}
				} else {
					getCourseByOtherDepartment.add(course);
				}
				
				System.out.println("部门课程匹配已完成" + (double)courseCount/getCourseByDepartmentId.get(departmentId).size());
				
				courseCount ++;
			}
			count1 ++;
		}
		
		System.out.println("开始根据School进行匹配");
		System.out.println("共有" + getCourseBySchoolId.keySet().size() +"个School");
		int count2 = 1;
		for(Integer schoolId : getCourseBySchoolId.keySet()) {
			
			System.out.println("正在进行第" + count2 + "个School匹配");
			List<Integer> departmentIds = departmentDao.getDepartmentIdsBySchoolId(schoolId);
			List<Integer> personIdsByDepartments = personProfileDao.getPersonIdsByDepartmentIds(departmentIds);
			List<Person> personList = personDao.getPersonsByIdList(personIdsByDepartments);
			Map<String, List<Integer>> persons = new HashMap<String, List<Integer>>();
			for(Person person : personList) {
				if(!persons.containsKey(person.name)) {
					persons.put(person.name, new ArrayList<Integer>());
				}
				persons.get(person.name).add(person.id);
			}
			System.out.println("学院人物提取完毕，开始课程匹配");
			System.out.println("该学院课程数共" + getCourseBySchoolId.get(schoolId).size() + "个");
			int courseCount = 1;
			for(Course course : getCourseBySchoolId.get(schoolId)) {
				if(persons.containsKey(course.teacherName)) {
					Person2Course person2Course = new Person2Course();
					for(Integer personId : persons.get(course.teacherName)) {
						person2Course.person_id = personId;
						person2Course.course_id = course.id;
						person2Course.semesterId = course.semesterId;
						person2Course.rank = "A";
						personCourseList.add(person2Course);
					}
				} else {
					getCourseByOtherDepartment.add(course);
				}
				
				System.out.println("学院课程匹配已完成" + (double)courseCount/getCourseBySchoolId.get(schoolId).size());
				
				courseCount ++;
			}
			count2 ++;
		}
		
		
		System.out.println("开始进行剩余课程匹配");
		System.out.println("共有" + getCourseByOtherDepartment.size() +"门剩余课程");
		int count3 = 1;
		for(Course course : getCourseByOtherDepartment) {
			
			if(getPersonByName.containsKey(course.teacherName)) {
				Person2Course person2Course = new Person2Course();
				for(Integer personId : getPersonByName.get(course.teacherName)) {
					person2Course.person_id = personId;
					person2Course.course_id = course.id;
					person2Course.semesterId = course.semesterId;
					person2Course.rank = "B";
					personCourseList.add(person2Course);
				}
			}
			
			System.out.println("学院课程匹配已完成" + (double)count3/getCourseByOtherDepartment.size());
			
			count3 ++;
		}
		
		System.out.println("开始插入数据库");
		personCourseDao.insertBatch(personCourseList);
	}
	
	public static void main(String[] args) {
		person2CourseTransfer();
	}
}
