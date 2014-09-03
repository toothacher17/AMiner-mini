package SchoolSearch.services.dao.person.model;


/**
 * 
 * @author guanchengran
 *
 */
public class PersonInfo {
	
//	public static Person_Info construct(int id, Faculty f) {
//		if(null == f || Strings.isEmpty(f.education)&&Strings.isEmpty(f.projects)&&Strings.isEmpty(f.resume)&&Strings.isEmpty(f.honor)
//				&&Strings.isEmpty(f.parttime)&&Strings.isEmpty(f.fields)&&Strings.isEmpty(f.summary)&&Strings.isEmpty(f.achievements)
//				&&Strings.isEmpty(f.experience))
//			return null;
//		Person_Info result = new Person_Info();
//		result.id = id;
//		result.education = f.education;
//		result.projects = f.projects;
//		result.resume = f.resume;
//		result.honor = f.honor;
//		result.parttime = f.parttime;
//		result.fields = f.fields;
//		result.summary = result.summary;
//		result.achievements = result.achievements;
//		result.experience = result.experience;
//		return result;
//	}
	
	public int id;
	public String education;
	public String projects;
	public String resume;
	public String honor;
	public String parttime;
	public String fields;
	public String summary;
	public String achievements;
	public String experience;

}
