package SchoolSearch.services.dao.graduatePublication.model;

import java.util.Comparator;

/**
 * 
 * @author guanchengran
 *
 */
public class Person2GraduatePublication {
	
	public static Person2GraduatePublication construct(int id, int graduatepublication_id, String type, int position) {

		Person2GraduatePublication result = new Person2GraduatePublication();
		result.person_id = id;
		result.graduatePublication_id = graduatepublication_id;
		result.type = type;
		result.position = position;
		return result;
	}
	
	public int id;
	public int person_id;
	public int graduatePublication_id;
	public String type;
	public int position;
	
	public static Comparator<Person2GraduatePublication> positionComparator = new Comparator<Person2GraduatePublication>() {
		@Override
		public int compare(Person2GraduatePublication o1, Person2GraduatePublication o2) {
			return o1.position - o2.position;
		}
	};
}
