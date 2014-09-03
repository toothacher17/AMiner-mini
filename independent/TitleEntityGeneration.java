import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import SchoolSearch.services.dao.person.Person2TitleDao;
import SchoolSearch.services.dao.person.PersonDao;
import SchoolSearch.services.dao.person.PersonTitleDao;
import SchoolSearch.services.dao.person.model.Person;
import SchoolSearch.services.dao.person.model.Person2Title;
import SchoolSearch.services.dao.person.model.PersonTitle;


public class TitleEntityGeneration {
	
	
	
	static PersonTitleDao personTitleDao = PersonTitleDao.getInstance();
	static Person2TitleDao person2TitleDao = Person2TitleDao.getInstance();
	static PersonDao personDao = PersonDao.getInstance();

	public static void insertPersonTitle() {
		List<PersonTitle> personTitleList = new ArrayList<PersonTitle>();
		
		File file = new File("/Users/guanchengran/work/myEclipse/SchoolDemo/resources/Data/titles.txt");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String line = null;
		try {
			while((line = reader.readLine())!=null) {
				if(line.contains(":")) {
					PersonTitle personTitle = new PersonTitle();
					String title = line.substring(0, line.length()-1);
					System.out.println(title);
					personTitle.name = title;
					if(title.equals("清华大学“学术新人奖”获得者")) {
						personTitle.score = 3;
					} else if (title.equals("国家杰出青年科学基金获得者")) {
						personTitle.score = 3;
					} else if(title.equals("国家自然科学基金委创新研究群体学术带头人")||title.equals("国家级教学名师奖获得者")||title.equals("国家“百千万人才工程”入选者")) {
						personTitle.score = 4;
					} else if(title.equals("何梁何利基金科学与技术进步奖获得者")||title.equals("“长江学者奖励计划”讲座教授")||title.equals("“长江学者奖励计划”特聘教授")) {
						personTitle.score = 4.5;
					} else if(title.equals("中国工程院院士")) {
						personTitle.score = 5;
					} else if(title.equals("中国科学院院士")) {
						personTitle.score = 5;
					}
					personTitleList.add(personTitle);
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		personTitleDao.insertBatch(personTitleList);
	}
	
	public static void insertPerson2Title() {
		
		List<PersonTitle> personTitleList = personTitleDao.walkAll();
		Map<String, Integer> personTitleMap = new HashMap<String, Integer>();
		for(PersonTitle personTitle : personTitleList) {
			personTitleMap.put(personTitle.name, personTitle.id);
		}
		System.out.println("personTitleMap finished");
		
		List<Person> personList = personDao.walkAll();
		Map<String, List<Integer>> personMap = new HashMap<String, List<Integer>>();
		for(Person person : personList) {
			if(!personMap.containsKey(person.name)) {
				personMap.put(person.name, new ArrayList<Integer>());
			}
			personMap.get(person.name).add(person.id);
		}
		System.out.println("personMap finished");

		
		List<Person2Title> person2TitleList = new ArrayList<Person2Title>();

		File file = new File(
				"/Users/guanchengran/work/myEclipse/SchoolDemo/resources/Data/titles.txt");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "utf-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String line = null;
		int count = 1;
		int beginNum = 0;
		String year = null;
		String title = null;
		Integer title_id = null;
		
		Integer person2TitleNum = 1;
		
		try {
			while ((line = reader.readLine()) != null) {
				if(line.contains(":")) {
					beginNum = count;
					title = line.substring(0, line.length()-1);
					title_id = personTitleMap.get(title);
				}
				if(count == beginNum + 1) {
					if(line.contains("#")) {
						year = line.substring(1).trim();
					} else {
						year = null;
					}
				}
				
				if(!line.contains(":")&&!line.contains("#")) {
					if(personMap.containsKey(line.trim())) {
						for(Integer person_id : personMap.get(line.trim())) {
							Person2Title person2Title = new Person2Title();
							person2Title.year = year;
							person2Title.title_id = title_id;
							person2Title.person_id = person_id;
							person2TitleList.add(person2Title);
							System.out.println("产生第" + person2TitleNum +"条关系记录");
							person2TitleNum ++;
						}
					}
				}
				count ++;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		person2TitleDao.insertBatch(person2TitleList);
	}
	
	public static void main(String args[]) {
		//insertPersonTitle();
		insertPerson2Title();
	}
}
