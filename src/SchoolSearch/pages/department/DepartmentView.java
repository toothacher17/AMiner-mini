package SchoolSearch.pages.department;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

import SchoolSearch.services.CollectionsService;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;
import SchoolSearch.services.monitor.MonitorService;
import SchoolSearch.services.services.organizationLevels.DepartmentService;
import SchoolSearch.services.services.person.PersonService;
import SchoolSearch.services.utils.ComparatorUtil;

@Import(stylesheet = {"context:/res/css/pages/department/department.css"} ,//
		library = {	}
	)
public class DepartmentView {
	@Property
	Integer id;
	
	Map<String, List<SchooltestPerson>> institution2PersonMap;

	@Property
	List<String> instituteNameList;
	
	Map<String, List<Integer>> titleMap;
	
	@Property
	List<String> titleList;
	
	@Property
	String departmentName;
	
	@Property
	SchooltestPerson _person;
	
	@Property
	String _instituteName;
	
	@Property
	String _titleName;
	
	@Property
	Integer _index;
	
	@Property
	List<Integer> personIdList;
	
	@Property
	List<SchooltestPerson> allPersonList;
	
	
	@InjectComponent
	Zone departmentDisplayZone;

	public Object onActivate(EventContext context) {
		id = context.get(Integer.class, 0);
		return true;
	}
//	从·url端取出id值，这个id值即用于判断dept，为dept的id，onActive
	
	
	@SetupRender
	void setupRender() {
		monitor.visitPage("departmentView", id.toString());
		
		departmentName = departmentService.getDepartmentById(id);
		
		
		List<Integer> allPersonIdList = new ArrayList<Integer>();
		if(null != personService.getPersonListByDepartmentId(id)&& personService.getPersonListByDepartmentId(id).size() != 0) {
			allPersonList = personService.getPersonListByDepartmentId(id);
			for(SchooltestPerson everyperson : allPersonList) {
				if(null != everyperson) {
					allPersonIdList.add(everyperson.getId());
				}
			}
			titleMap = personService.getTitleClustedPersons(allPersonIdList);
		}
		else {
			allPersonList = new ArrayList<SchooltestPerson>();
			titleMap = new HashMap<String, List<Integer>>();
		}
//		titleMap的构建，关键所在
		List<String> personTitleList = CollectionsService.Title.getTitleList();
		List<String> tempList = new ArrayList<String>(titleMap.keySet()); 
		titleList = new ArrayList<String>();
		for(String temp : tempList) {
			if((personTitleList.contains(temp))) {
				titleList.add(temp);
			}
		}
		Collections.sort(titleList, ComparatorUtil.titleComparator);
//		title排序构造规则
	}
	
	@AfterRender
	void afterRender() {
		
	}
	
	
	public String getTitle() {
		return "院系概况";
	}
	
	public List<SchooltestPerson> getPersonList() {
//		return personService.getPersonListByDepartmentId(id);
		List<SchooltestPerson> departPersonlist = personService.getPersonListByDepartmentId(id);
		Map<String, SchooltestPerson> nameMap = new HashMap<String, SchooltestPerson>();
		List<SchooltestPerson> result = new ArrayList<SchooltestPerson>();
		if(null != departPersonlist) {
			for(SchooltestPerson person : departPersonlist) {
				if(null != person)
					nameMap.put(person.getName() + person.getId(), person);
			}
			List<String> nameList = new ArrayList<String>(nameMap.keySet());
			Collections.sort(nameList, Collator.getInstance(java.util.Locale.CHINA));
			for(String name : nameList) {
				result.add(nameMap.get(name));
			}
		}
		return result;
	}
	
	public List<Integer> getTitlePersonIdList() {
		return titleMap.get(_titleName);
	}
	
	public Object onShowDetails(Integer pid) {
		id = pid;
		return departmentDisplayZone.getBody();
	}
	
	public Object onShowPersons(EventContext context) {
		if(null != personIdList)
			personIdList.clear();
		personIdList = new ArrayList<Integer>();

		String pidArrayStr = context.get(String.class, 0);
		JSONArray pidArray = new JSONArray(pidArrayStr);
		for(int i=0; i<pidArray.length(); i++) {
			personIdList.add(pidArray.getInt(i));
		}
		return departmentDisplayZone.getBody();
	}
	
	
	
	@Inject
	PersonService personService;
	
	@Inject
	DepartmentService departmentService;
	
	@Inject
	private ComponentResources resources;
	
	@Inject
	private JavaScriptSupport jsSupport;
	
	@Inject
	private MonitorService monitor;
	
}
