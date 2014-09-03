package SchoolSearch.services.dao.publication.model;
/**
 * 
 * @author guanchengran
 *
 */
public class Person2Publication {
	
	public static Person2Publication construct(int id, int publication_id, int position) {

		Person2Publication result = new Person2Publication();
		result.person_id = id;
		result.publication_id = publication_id;
		result.position = position;
		return result;
	}
	
	public int id;
	public int person_id;
	public int publication_id;
	public int position;
}
