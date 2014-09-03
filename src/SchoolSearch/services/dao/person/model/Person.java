package SchoolSearch.services.dao.person.model;


/**
 * 
 * @author guanchengran
 *
 */
public class Person {
//	public static Person construct(int id, Faculty f) {
//		Person result = new Person();
//		result.id = id;
//		result.name = f.name;
//		return result;
//	}
	
	public Person() {
		
	}
	
	public Person(String name, String name_alias) {
		this.name = name;
		this.name_alias = name_alias;
	}
	
	public int id;
	public String name;
	public String name_alias;
	
	
}
