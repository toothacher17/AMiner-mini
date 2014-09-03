package SchoolSearch.services.dao.course.model;
/**
 * 
 * @author guanchengran
 *
 */
public class Person2Course {
	
	public static  Person2Course construct(Integer id, Integer course_id, Integer semesterId, String rank) {
		Person2Course result = new Person2Course();
		result.person_id = id;
		result.course_id = course_id;
		result.semesterId = semesterId;
		result.rank = rank;
		return result;
	}
	
	public Integer id;
	public Integer person_id;
	public Integer course_id;
	public Integer semesterId;
	public String rank;
	
}
