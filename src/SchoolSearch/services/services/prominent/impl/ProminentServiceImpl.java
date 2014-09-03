package SchoolSearch.services.services.prominent.impl;
/**
 * @author GCR
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.cache.OrganizationLevelsCacheService;
import SchoolSearch.services.dao.oranizationLevels.DepartmentDao;
import SchoolSearch.services.dao.oranizationLevels.Institute2DepartmentDao;
import SchoolSearch.services.dao.oranizationLevels.InstituteDao;
import SchoolSearch.services.dao.oranizationLevels.Person2InstituteDao;
import SchoolSearch.services.dao.oranizationLevels.SchoolDao;
import SchoolSearch.services.dao.oranizationLevels.model.Department;
import SchoolSearch.services.dao.oranizationLevels.model.School;
import SchoolSearch.services.services.organizationLevels.DepartmentService;
import SchoolSearch.services.services.organizationLevels.SchoolService;
import SchoolSearch.services.services.person.PersonService;
import SchoolSearch.services.services.prominent.ProminentService;

public class ProminentServiceImpl implements ProminentService {
	
	@Override
	public  Map<Integer, String> getIndex2TitleMap() {
//		List<String> titleList = new ArrayList<String>();
		Map<Integer, String> result = new HashMap<Integer, String>();
		result.put(0, "中国科学院院士");
		result.put(1, "中国工程院院士");
		result.put(2, "“长江学者奖励计划”特聘教授");
		result.put(3, "“长江学者奖励计划”讲座教授");
		result.put(4, "何梁何利基金科学与技术进步奖获得者");
		result.put(5, "国家级教学名师奖获得者");
		result.put(6, "国家杰出青年科学基金获得者");
		result.put(7, "国家“百千万人才工程”入选者");
		result.put(8, "国家自然科学基金委创新研究群体学术带头人");
		result.put(9, "清华大学“学术新人奖”获得者");
		return  result;
	}
	
	Map<String, Map<String, List<Integer>>> showMap;
	
	Map<Integer, String> index2TitleNameMap;
	
	List<String> titleNameList;
	
//	
	@Override
	public Map<String, Map<String, List<Integer>>> makeTheFinalShowMap() {
		Map<String, Map<String, List<Integer>>> result = new HashMap<String, Map<String,List<Integer>>>();
		List <String> titleList = new ArrayList<String>(getIndex2TitleMap().values()) ;
		List<Integer> allTitledPersonIdList = personService.getAllPersonExtIds();
		for(String titleName : titleList ){
			Map<String, List<Integer>> school2personMap = new HashMap<String, List<Integer>>();
			List<Integer> personIdList = new ArrayList<Integer>();
			for(Integer id : allTitledPersonIdList ){
				String personTitleName = personService.getPersonExt(id).getTitle();
				if(null == personTitleName)
					continue;
				if(personTitleName.contains(titleName)){
					personIdList.add(id);
				}
			}
			for(Integer personIdTemp : personIdList){
				List<String> schoolNameListTemp = schoolService.getSchoolNamesByPersonId(personIdTemp);
				for(String  schoolNameTemp : schoolNameListTemp){
					if(school2personMap.containsKey(schoolNameTemp)){
						school2personMap.get(schoolNameTemp).add(personIdTemp);
					}else{
						List<Integer> newListTemp = new ArrayList<Integer>();
						newListTemp.add(personIdTemp);
						school2personMap.put(schoolNameTemp, newListTemp);
					}
				}
			}
			result.put(titleName, school2personMap);
		}
		return result;
	}

	@Override
	public Map<String, List<Integer>> getTheFinalShowMap(String Title) {
		if (null==showMap){
			showMap = makeTheFinalShowMap();
		}
		return showMap.get(Title);
	}

	
	@Inject
	PersonService personService;
	
	@Inject
	SchoolService schoolService;

	

	
}
