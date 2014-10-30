import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import SchoolSearch.services.dao.schooltest.dao.SchooltestPersonDAO;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;


public class GeneralTest {
	public static void testIKA() {
		InputStream is = GeneralTest.class.getClassLoader().getResourceAsStream("IKAnalyzer/ext.dic");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = null;
		try {
			while((line = reader.readLine())!= null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void testIO () {
		List<SchooltestPerson> walkAll = SchooltestPersonDAO.getInstance().walkAll();
		System.out.println("the walkall size is " + walkAll.size());
	}
	
	
	
	
	
	public static void main(String[] args) {
		testIO();
	}
}

