package NAanalysis;

import SchoolSearch.pages.person.PersonIndex;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPerson2departmentDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPersonDAO;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson2department;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class PersonAnalysis {
	
	public static String rootpath = "D:\\Users\\jingyuanliu\\na\\new\\";
	
	public static void main(String[] args) {
		SchooltestPersonDAO persondao = SchooltestPersonDAO.getInstance();
		SchooltestPerson2departmentDAO person2departdao = SchooltestPerson2departmentDAO.getInstance();
		
		List<Map<String, List<Integer>>> result = new ArrayList<Map<String,List<Integer>>>(); 
		
		List<SchooltestPerson> allperson = persondao.walkAll();
		Map<Integer, String> personMap = new HashMap<Integer, String>();
		for(SchooltestPerson p : allperson) {
			personMap.put(p.getId(), p.getName());
		}
		
		List<SchooltestPerson2department> allperson2depart = person2departdao.walkAll();
		Map<Integer, List<Integer>> depart2person = new HashMap<Integer, List<Integer>>();
		for(SchooltestPerson2department p2d : allperson2depart) {
			if(depart2person.containsKey(p2d.getDepartmentId())) {
				depart2person.get(p2d.getDepartmentId()).add(p2d.getPersonId());
			}else {
				List<Integer> temp = new ArrayList<Integer>();
				temp.add(p2d.getPersonId());
				depart2person.put(p2d.getDepartmentId(), temp);
			}
		}
		
		System.out.println(">>> the size of depart2person map " + depart2person.size());

		for(Integer depart : depart2person.keySet()) {
			List<Integer> personindepart = depart2person.get(depart);
			Map<String, List<Integer>> tempMap = new HashMap<String, List<Integer>>();
			for(Integer everyperson : personindepart) {
				String tempname = personMap.get(everyperson);
				if(tempMap.containsKey(tempname)) {
					tempMap.get(tempname).add(everyperson);
				}else {
					List<Integer> tempList = new ArrayList<Integer>();
					tempList.add(everyperson);
					tempMap.put(tempname, tempList);
				}
			}
			result.add(tempMap);
		}
		
		try {
			PrintWriter out = new PrintWriter(new File(rootpath,"PersonAnalysis3.txt"));
			int count1 = 0;
			int count2 = 0;
			for(Map<String, List<Integer>> r : result) {
				for(String name : r.keySet()) {
					List<Integer> personidlist = r.get(name);
//					int number = 0;
//					for(Integer everyone : personidlist) {
//						if(everyone > 4735) {
//							number ++;
//						}
//					}
					Set<Integer> tempSet = new HashSet<Integer>(); 
					for(Integer everyone : personidlist) {
						tempSet.add(everyone);
					}		
					if(tempSet.size() > 1 ) {
						out.println(name + "\t" + r.get(name));
						System.out.println(name + "\t" + r.get(name));
						count2 ++;
					} 
					count1 ++;
				}
			}
			out.close();
			System.out.println(">> the whole size of person name is " + count1);
			System.out.println(">> the whole size of person name ambiguaty is " + count2);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
