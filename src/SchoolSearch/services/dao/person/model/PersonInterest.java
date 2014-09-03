package SchoolSearch.services.dao.person.model;

public class PersonInterest {
	public static PersonInterest construct(int id, int aid, String interest, int year) {

		PersonInterest result = new PersonInterest();
		result.person_id = id;
		result.aid = aid;
		result.interest = interest;
		result.year = year;
		return result;
	}
	
	public int id;
	public int person_id;
	public long aid;
	public String interest;
	public int year;
}
