import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.*;

import SchoolSearch.services.dao.schooltest.dao.SchooltestPersonDAO;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;

public class testtime {

	public static void test() {
//		System.out.println((String.valueOf(System.currentTimeMillis())));
//		System.out.println(System.currentTimeMillis());
////		System.out.println((Double.(System.currentTimeMillis())));
//		long currentTimeMillis = System.currentTimeMillis();
//		System.currentTimeMillis()
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(df.format(new Date()));
	}

	public static void testPinyin() {
		SchooltestPersonDAO personDAO = SchooltestPersonDAO.getInstance();
		List<SchooltestPerson> personList = personDAO.walk(0, 20);
//		Collections.sort(personList, new Comparator<SchooltestPerson>(){
//			@Override
//			public int compare(SchooltestPerson o1, SchooltestPerson o2) {
//				return o1.getName().compareTo(o2.getName());
//			} 
//		});
		Map<String, SchooltestPerson> nameMap = new HashMap<String, SchooltestPerson>();
		for(SchooltestPerson person: personList) {
			nameMap.put(person.getName()+person.getClass(), person);
		}
		Comparator cmp = Collator.getInstance(java.util.Locale.CHINA);
//		Arrays.sort(personList, cmp);
		List<String> temp = new ArrayList<String>(nameMap.keySet());
		Collections.sort(temp, cmp);
		for(String pname : temp) {
			System.out.println(nameMap.get(pname).getName() + "\t" + nameMap.get(pname).getId());
		}
		
	}
	
	public static void main(String[] args) {
//		test();
		testPinyin();
	}
}
