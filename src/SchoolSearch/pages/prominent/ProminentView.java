package SchoolSearch.pages.prominent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Cached;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

import SchoolSearch.services.dao.oranizationLevels.model.Department;
import SchoolSearch.services.dao.oranizationLevels.model.Institute;
import SchoolSearch.services.dao.person.model.Person;
import SchoolSearch.services.dao.person.model.PersonProfile;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonProfile;
import SchoolSearch.services.monitor.MonitorService;
import SchoolSearch.services.services.organizationLevels.DepartmentService;
import SchoolSearch.services.services.organizationLevels.InstituteService;
import SchoolSearch.services.services.organizationLevels.SchoolService;
import SchoolSearch.services.services.person.PersonService;
import SchoolSearch.services.utils.ComparatorUtil;

@Import(stylesheet = {"context:/res/css/pages/department/department.css"} ,//
		library = {	}
	)
public class ProminentView {

	@Property
	Map<String, List<Integer>> titleMap;
	
	@Property
	Map<String, List<SchooltestPerson>> schoolName2PersonMap;
	
	@Property
	List<String> schoolNameList;

	@Property
	List<Integer> personIdList;
//	
	@Property
	Map<String,List<SchooltestPerson> > SchoolName2TitledPersons;
	
	@Property
	List<String> titleList;
	
	@Property
	String departmentName;
	
	@Property
	String schoolName;
	
	@Property
	Person _person;
	
	@Property
	String _instituteName;
	
	@Property
	String _titleName;
	
	@Property
	Integer _index;
	
	@Property
	List<SchooltestPerson> personList;
	
	@InjectComponent
	Zone prominentDisplayZone;

	@Property
	List<Integer> schoolIdList;

	@Property
	Integer schoolId;
	
	@SetupRender
	void setupRender() {
		monitor.visitPage("ProminentView", null);
		
		
//		List<Integer> allPersonIdList = new ArrayList<Integer>();
//		schoolIdList=schoolService.getAllSchoolIds();
//		schoolName2PersonMap = constructSchoolMap(schoolIdList);
//		schoolNameList = new ArrayList<String>(schoolName2PersonMap.keySet());
//		for(String schName : schoolNameList) {
//			List<Person> list = schoolName2PersonMap.get(schName);
//			for(Person p : list) {
//				if(!allPersonIdList.contains(p.id))
//					allPersonIdList.add(p.id);
//			}
//		}
//		rebuild titlemap, the titleList has been given, make a map from certain title to perosonlist
		titleList = getTheTitleList();
		Collections.sort(titleList, ComparatorUtil.titleComparator);
		titleMap = getTitleClustedPersons(titleList);
//		titleMap = personService.getTitleClustedPersons(allPersonIdList);
//		titleList = new ArrayList<String>(titleMap.keySet());
	}
	
	@AfterRender
	void afterRender() {
	}
	
	
	public String getTitle() {
		return "杰出人才";
	}
		
	
	public List<Integer> getSelectedPersonIdList(){
		return titleMap.get(_titleName);
	}
	
	public Object onShowPersonIds(EventContext context) {
		if(null != personIdList)
			personIdList.clear();
		personIdList = new ArrayList<Integer>();
		String pidArrayStr = context.get(String.class, 0);
		JSONArray pidArray = new JSONArray(pidArrayStr);
		for(int i=0; i<pidArray.length(); i++) {
			personIdList.add(pidArray.getInt(i));
		}
		_titleName = context.get(String.class, 1);
//		System.out.println(_titleName);
		return prominentDisplayZone.getBody();
	}
	
	public List<String> setSchoolName2TitledPersons(){
//		List<String> selectedSchoolNames = new ArrayList<String>();
		SchoolName2TitledPersons = new HashMap<String, List<SchooltestPerson>>();
		schoolIdList=schoolService.getAllSchoolIds();
		schoolName2PersonMap = constructSchoolMap(schoolIdList);
		schoolNameList = new ArrayList<String>(schoolName2PersonMap.keySet());
		
		List<SchooltestPerson> personList = personService.getPersonList(personIdList);
		
//		System.out.println("personList.size"+personList.size());
		
		for(String schoolNameTemp : schoolNameList){
			List<SchooltestPerson> personResult = new ArrayList<SchooltestPerson>();
			List<SchooltestPerson> personTest =  schoolName2PersonMap.get(schoolNameTemp);
			boolean flag = false;
			for(SchooltestPerson personTemp : personList){
				if (personTest.contains(personTemp)){
					personResult.add(personTemp);
					flag = true;
				}
			}
			if(flag == true){
				SchoolName2TitledPersons.put(schoolNameTemp,personResult);
			}
		}
//		System.out.println("<><>>"+SchoolName2TitledPersons.size());
		List<String> selectedSchoolNames = new ArrayList<String>(SchoolName2TitledPersons.keySet());
//		selectedSchoolNames = SchoolName2TitledPersons;
		return selectedSchoolNames;
	}
	

	public List<SchooltestPerson> getSchoolTitledPerson(){
		List<SchooltestPerson> schoolTitledPerson = SchoolName2TitledPersons.get(schoolName);
//		System.out.println(">>" + schoolName + "\t" + schoolTitledPerson.size());
		return schoolTitledPerson;
	}
	
	private Map<String, List<SchooltestPerson>> constructSchoolMap(List<Integer> ids) {
		long to = System.currentTimeMillis();
		Map<String, List<SchooltestPerson>> result = new HashMap<String, List<SchooltestPerson>>();
		
		for(Integer id : ids ){
			String selectedSchoolName=schoolService.getSchoolById(id);
			
//			List<Person> allPerson=new ArrayList<Person>();
//			List<Department> allDepartments = departmentService.getDepartmentsBySchoolId(id);
//			if(null!=allDepartments){
//				for(Department dept: allDepartments){
//					Map<Integer, List<Integer>> ins2personidMap = instituteService.getInstitute2PersonInDepartment(dept.id);
//					List<Integer> insIDs = new ArrayList<Integer>(ins2personidMap.keySet());
//					List<Institute> insList = instituteService.getInstituteList(insIDs);
//					for(Institute i : insList) {
//						List<Integer> pidList = ins2personidMap.get(i.id);
//						List<Person> personList = personService.getPersonList(pidList);
//						for(Person pp:personList){
//							if(!(allPerson.contains(pp))){
//								allPerson.add(pp);
//							}
//						}
//					}
//				}
//			}
			
			List<SchooltestPerson> allPerson = schoolService.getPersonsInSchoolBySchoolId(id);
//			try {
//				for(int i=0; i<allPerson.size(); i++) {
//					System.out.println(allPerson.get(i).id + "\t" + allPerson2.get(i).id);
//				}
//			}catch (Exception e) {
//				e.printStackTrace();
//			}
			
			if (null!=allPerson&&allPerson.size()!=0){
				result.put(selectedSchoolName, allPerson);
			}
		}
//		System.out.println("result.size"+result.size());
//		System.out.println("信息科技学院"+result.get("信息科学技术学院").size());
//		System.out.println(System.currentTimeMillis()-to+"ms");
		return result;
	}
	
	@Cached
	private  List<String> getTheTitleList() {
		List<String> titleList = new ArrayList<String>();
		titleList.add("中国科学院院士");
		titleList.add("中国工程院院士");
		titleList.add("“长江学者奖励计划”特聘教授");
		titleList.add("“长江学者奖励计划”讲座教授");
		titleList.add("何梁何利基金科学与技术进步奖获得者");
		titleList.add("国家级教学名师奖获得者");
		titleList.add("国家杰出青年科学基金获得者");
		titleList.add("国家“百千万人才工程”入选者");
		titleList.add("国家自然科学基金委创新研究群体学术带头人");
		titleList.add("清华大学“学术新人奖”获得者");
		return  titleList;
	}
	
	private Map<String, List<Integer>> getTitleClustedPersons(List<String> titleList){
		List<Integer> allTitledPersonIdList = personService.getAllPersonExtIds();
		Map<String, List<Integer>> titlnew2personMap = new HashMap<String, List<Integer>>(); 
		for(String titleName : titleList ){
			List<Integer> personIdList = new ArrayList<Integer>();
			for(Integer id : allTitledPersonIdList ){
				String personTitleName = personService.getPersonExt(id).getTitle();
				if(null == personTitleName)
					continue;
				if(personTitleName.contains(titleName)){
					personIdList.add(id);
				}
			}
			titlnew2personMap.put(titleName, personIdList);
		}
		return titlnew2personMap;
	}
	
	
	public SchooltestPersonProfile getPersonProfile(){
		SchooltestPersonProfile result = personService.getPersonProfile(_person.id);
		return result;
	}
		
	@Inject
	InstituteService instituteService;
	
	@Inject
	PersonService personService;
	
	@Inject
	DepartmentService departmentService;
	

	@Inject
	SchoolService schoolService;
	
	
	@Inject
	private ComponentResources resources;
	
	@Inject
	private JavaScriptSupport jsSupport;
	
	@Inject
	private MonitorService monitor;
}
