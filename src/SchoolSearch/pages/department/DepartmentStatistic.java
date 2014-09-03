package SchoolSearch.pages.department;

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

import SchoolSearch.services.dao.oranizationLevels.model.Institute;
import SchoolSearch.services.dao.person.model.PersonProfile;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonProfile;
import SchoolSearch.services.monitor.MonitorService;
import SchoolSearch.services.services.organizationLevels.DepartmentService;
import SchoolSearch.services.services.organizationLevels.InstituteService;
import SchoolSearch.services.services.person.PersonService;
import SchoolSearch.services.services.publication.PublicationService;
import SchoolSearch.services.utils.ComparatorUtil;
import SchoolSearch.services.utils.Strings;

@Import(stylesheet = {"context:/res/css/pages/department/department.css"} ,//
		library = {	}
	)
public class DepartmentStatistic {
	Integer id;
	
	@Property
	Integer personNumber;
	Map <String, Integer> position2PersonNumber;
	
	@Property
	List<String> depPositionList;
	
	@Property
	String depPosition;
	
	public Object onActivate(EventContext context) {
		id = context.get(Integer.class, 0);
		return true;
	}
//	从·url端取出id值，这个id值即用于判断dept，为dept的id，onActive
	
	
	@SetupRender
	void setupRender() {
//		monitor.visitPage("departmentView", id.toString());
		personNumber = 0;
		List<Integer> depPersonIdList = new ArrayList<Integer>(); 
		Map<Integer, List<Integer>> institute2PersonInDepartment = instituteService.getInstitute2PersonInDepartment(id);
		for(Integer instituteId : institute2PersonInDepartment.keySet()) {
			List<Integer> personIdList = institute2PersonInDepartment.get(instituteId);
			if(null != personIdList) {
				personNumber += personIdList.size();
				depPersonIdList.addAll(personIdList);
			}
		}
		position2PersonNumber = new HashMap<String, Integer>();
		List<SchooltestPersonProfile> personProfileList = personService.getPersonProfile(depPersonIdList);
		for(SchooltestPersonProfile pp: personProfileList){
			String[] split = pp.getPosition().split("[,，;；、\\s]");
			for(String s : split) {
				if(Strings.isNotEmpty(s)) {
					s = s.trim();
					if(!position2PersonNumber.containsKey(s)) 
						position2PersonNumber.put(s, 1);
					else
						position2PersonNumber.put(s, position2PersonNumber.get(s)+1);
				}
			}
			
		}
		depPositionList = new ArrayList<String>(position2PersonNumber.keySet());
		
		List<SchooltestPersonProfile> personProfileList1 = personService.getPersonProfile(depPersonIdList);
	}
	public Integer getPositionPersonNumber() {
		return position2PersonNumber.get(depPosition);
	}
	
	@AfterRender
	void afterRender() {
		
	}
	
	
	public String getTitle() {
		return "院系数据";
	}
	
	@Inject
	InstituteService instituteService;
	
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
	
	@Inject
//	publicationService publicationService;
	PublicationService publicationService;
	
}
