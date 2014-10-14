package SchoolSearch.services.services.course.impl;
/**
 * @author GCR
 */
import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.cache.CourseCacheService;
import SchoolSearch.services.dao.schooltest.dao.SchooltestCourseDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestCourseDetailDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPerson2courseDAO;
import SchoolSearch.services.dao.schooltest.model.SchooltestCourse;
//import SchoolSearch.services.dao.schooltest.model.SchooltestCourseDetail;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson2course;
import SchoolSearch.services.services.course.CourseService;
import SchoolSearch.services.services.person.PersonService;

public class CourseServiceImpl implements CourseService {
	
	@Inject 
	SchooltestCourseDAO courseDao;
	
	@Inject
	SchooltestPerson2courseDAO personCourseDao;
	
	@Inject
	CourseCacheService courseCacheService; 

	@Inject
	PersonService personService;
	
	@Override
	public SchooltestCourse getCourse(Integer id) {
		if(null == id) {
			return null;
		}
		SchooltestCourse course = courseCacheService.getCourseFromCache(id);
		if (null == course) {
			course = courseDao.selectById(id);
			courseCacheService.setCourseCache(id, course);
		}
		return course;
	}
	
	

	@Override
	public List<List<List<SchooltestCourse>>> getCourseList(List<String> ids) {
		if(null==ids)
			return null;
		if(ids.size() == 0)
			return new ArrayList<List<List<SchooltestCourse>>>();	
		List<List<List<SchooltestCourse>>> result = new ArrayList<List<List<SchooltestCourse>>>();
		for(String idl: ids){
			if(idl.equals(""))
				continue;
			List<List<SchooltestCourse>> allCourseSameName = new ArrayList<List<SchooltestCourse>>();
			for(String id : idl.split(";")){
				if(!id.isEmpty()){
					Integer idi=Integer.parseInt(id);
					SchooltestCourse course = courseCacheService.getCourseFromCache(idi);
					if(null == course){
						course=courseDao.selectById(idi);
						courseCacheService.setCourseCache(course.getId(), course);
					}
					boolean flag = false;
					for (List<SchooltestCourse> temp : allCourseSameName){
						if (course.getTeacherName().equals(temp.get(0).getTeacherName())){
							if(temp.contains(course)!=true){
								temp.add(course);
							}
							flag=true;
						}
					}
					if(flag==false){
						List<SchooltestCourse> allCourseSameTeacher = new ArrayList<SchooltestCourse>();
						allCourseSameTeacher.add(course);
						allCourseSameName.add(allCourseSameTeacher);
					}
				}
			}
			result.add(allCourseSameName);
		}
		return result;
	}
	
	@Override
	public List<SchooltestCourse> getCourse(List<Integer> courseIdList) {
		if(null==courseIdList)
			return null;
		if(courseIdList.size() == 0)
			return new ArrayList<SchooltestCourse>();
		
		List<SchooltestCourse> result = new ArrayList<SchooltestCourse>();
		List<Integer> leftIDs = new ArrayList<Integer>();

		for(Integer courseId : courseIdList) {
			SchooltestCourse course = courseCacheService.getCourseFromCache(courseId);
			if(null == course) {
				leftIDs.add(courseId);
			}
		}
		if(leftIDs.size() > 0) {
			List<SchooltestCourse> publications = courseDao.selectByIdList(leftIDs);
			for(SchooltestCourse c : publications) {
				courseCacheService.setCourseCache(c.getId(), c);
			}
		}
		for(Integer courseId : courseIdList) {
			SchooltestCourse c = courseCacheService.getCourseFromCache(courseId);
			if(null != c) {
				result.add(c);
			} 
		}
		return result;
	}

	@Override
	public List <SchooltestPerson> getCourseTeacher(Integer id) {
		// TODO getTheCourseTeacherByCourse.id
		if(null == id) {
			return null;
		}
		List<SchooltestPerson> courseTeachers = courseCacheService.getCourseTeacherFromCache(id);
		if (null == courseTeachers) {
			List<SchooltestPerson2course> tmp = personCourseDao.selectByIntegerField("id", id);
			List<Integer> teacherIds = new ArrayList<Integer>();
			for(SchooltestPerson2course everytmp : tmp) {
				if(everytmp.getConfirmed() == 1) teacherIds.add(everytmp.getPersonId());
			}
			courseTeachers = personService.getPersonList(teacherIds)  ;
			courseCacheService.setCourseTeacherCache(id, courseTeachers);
		}
		return courseTeachers;
	}

	
//	the id is the teacher's id
	@Override
	public List<SchooltestCourse> getRelatedCourseList(Integer id) {
		if(null == id){
			return null;
		}
		List<SchooltestCourse> result = courseCacheService.getRelatedCourseListFromCache(id);
		if(null==result || result.size() == 0){
//			result = getCourse(personCourseDao.selectByIntegerField("person_id", id));
			
			List<SchooltestPerson2course> tmp = personCourseDao.selectByIntegerField("person_id", id);
			List<Integer> tmpIdList = new ArrayList<Integer>();
			for(SchooltestPerson2course everytmp : tmp) {
				if(everytmp.getConfirmed() == 1) tmpIdList.add(everytmp.getId());
			}
			result = courseDao.selectByIdList(tmpIdList);
			courseCacheService.setRelatedCourseListCache(id,result);
		}
		return result;
	}

	public String getCourseUrl(Integer id) {
		return courseDao.selectById(id).getUrl();
	} 
	
	public static void main(String[] args) {
		CourseServiceImpl csl = new CourseServiceImpl();
		List<SchooltestCourse> test = csl.getRelatedCourseList(13);
		for(SchooltestCourse everyCourse : test){
			System.out.println(everyCourse.getId() + "\t"+everyCourse.getCourseName() + "\t"+everyCourse.getTeacherName() + "\t"+everyCourse.getSemesterName());
		}
	}



	@Override
	public void updatePerson2Course(Integer personId, Integer courseId) {
		// TODO Auto-generated method stub
		List<SchooltestPerson2course> courses = personCourseDao.selectByIntegerField("person_id", personId);
		for(SchooltestPerson2course p2c : courses) {
			if (p2c.getId().equals(courseId)) {
				System.out.println(">>>> it mataches the course!");
				p2c.setLabel(1);
				p2c.setConfirmed(0);
				personCourseDao.update(p2c);
				System.out.println("sucessfully updates! ~~~~");
				
				courseCacheService.getRelatedCourseListFromCache(personId).clear();
				if(null != courseCacheService.getCourseTeacherFromCache(courseId))
					courseCacheService.getCourseTeacherFromCache(courseId).clear();
				break;
			}
			
		}
		
		
		
		
	}
	
}
