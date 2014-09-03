package SchoolSearch.pages.prominent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonProfile;
import SchoolSearch.services.monitor.MonitorService;
import SchoolSearch.services.services.organizationLevels.DepartmentService;
import SchoolSearch.services.services.organizationLevels.SchoolService;
import SchoolSearch.services.services.person.PersonService;
import SchoolSearch.services.services.prominent.ProminentService;
import SchoolSearch.services.utils.ComparatorUtil;

@Import(stylesheet = {"context:/res/css/pages/department/department.css"} ,//
		library = {	}
	)
public class ProminentPersonsView {
	@Property
	Integer titleIdTemp;
	
	@Property
	Integer titleId;
	
	@Property
	String titleName;
	
	@Property
	Map<Integer,String> index2titleNameMap;
		
	@Property
	List<Integer> titleIdList;
		
	@Property
	Map<String, List<Integer>> school2personMap;
	
	@Property
	String schoolName;
	
	@Property
	SchooltestPerson person;
	
	@Property
	Integer index;
	
	@Property
	boolean flag;
	
	public Object onActivate(EventContext context){
		if(context.getCount()>0){
			titleId = context.get(Integer.class, 0);
			flag = true;
		}else{
			titleId = null;
			flag = false;
		}
			
		return true;
	}
	
	@SetupRender
	void setupRender(){
		monitor.visitPage("ProminentView", null);
		index2titleNameMap = prominentService.getIndex2TitleMap();
		titleIdList = new ArrayList<Integer>(index2titleNameMap.keySet());
		if(null!=titleId){
			titleName = index2titleNameMap.get(titleId);
		}
	}
	
	public String getTitleNameByTitleId(){
		return index2titleNameMap.get(titleIdTemp);
	}
	
	public List<String> getTheSchoolList(){
		school2personMap = prominentService.getTheFinalShowMap(titleName);
		List<String> result = new  ArrayList<String>(school2personMap.keySet());     
		return result;
	}
	
	public List<SchooltestPerson> getThePersonInSchool(){
		List<Integer> schoolPersonIdList = school2personMap.get(schoolName);
		Set<Integer> tempSet = new HashSet<Integer>();
		for(Integer id : schoolPersonIdList) {
			tempSet.add(id);
		}
		List<Integer> result = new ArrayList<Integer>();
		for(Integer setId : tempSet) {
			result.add(setId);
		}
		return personService.getPersonList(result);
	}
	
	public String getAvatar() {
		return personService.getAvatar(person.getId());
	}
	
	public SchooltestPersonProfile getPersonProfile(){
		return personService.getPersonProfile(person.getId());
	}
	
	@Inject
	PersonService personService;
	

	@Inject
	ProminentService prominentService;
	
	@Inject
	private MonitorService monitor;
	



}
