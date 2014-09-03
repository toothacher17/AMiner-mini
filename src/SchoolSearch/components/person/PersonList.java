package SchoolSearch.components.person;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonInterest;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonProfile;
import SchoolSearch.services.services.organizationLevels.DepartmentService;
import SchoolSearch.services.services.organizationLevels.InstituteService;
import SchoolSearch.services.services.organizationLevels.SchoolService;
import SchoolSearch.services.services.person.PersonService;
import SchoolSearch.services.utils.Strings;

public class PersonList {
	@Parameter(allowNull = false)
	List<Integer> personIdList;

	@Parameter(allowNull = true)
	@Property
	String searchQuery;
	
	@Parameter(allowNull = true)
	@Property
	Integer start;
	
	
	@Property
	List<Integer> _personIdList;
	
	@Property
	List<String> _personInterestNameList;

	@Property
	String _personInterestName;
	
	@Property
	Integer _personId;
	
	
	
	void setupRender() {
		this._personIdList = personIdList;
	}
	
	public SchooltestPerson getPerson() {
		return personService.getPerson(_personId);
	}
	
	SchooltestPersonProfile personprofile;
	public SchooltestPersonProfile getPersonProfile() {
		personprofile = personService.getPersonProfile(_personId);
		return personprofile;
	}
	
	public Integer getAuthorId() {
		return personService.getPersonProfile(_personId).getAuthorId();
	}
	

	public String getAvatar() {
		return personService.getAvatar(_personId);
	}
	
//	posititon + schoolname
	public String getAffiliation() {
//		get school names by person id
		StringBuilder schoolName = new StringBuilder();
		List<String> schoolnameList = schoolService.getSchoolNamesByPersonId(_personId);
		if(schoolnameList.size() > 0) {
			for(int j = 0; j < schoolnameList.size(); j++) {
				schoolName.append(schoolnameList.get(j));
				if(j < schoolnameList.size() - 1) {
					schoolName.append("、 ");
				}
			}
		}
		
//		get department name by person id
		String departmentName = null;
		if(null != departmentService.getDepartmentById(_personId)) {
			departmentName = departmentService.getDepartmentById(departmentService.getDepartmentByPersonId(_personId).get(0));
		}
		
//		get position by person id
		String position = personService.getPersonProfile(_personId).getPosition();
		
//		final builder
		if(Strings.isNotEmpty(position)) {
			if(Strings.isNotEmpty(schoolName.toString())) {
				if(Strings.isNotEmpty(departmentName)) {
					return position + ", " + departmentName + ", " + schoolName.toString();
				} else {
					return position + ", "  + schoolName.toString();
				}
			}else {
				if(Strings.isNotEmpty(departmentName)) {
					return position + ", " + departmentName;
				} else {
					return position;
				}
			}
		}else {
			if(Strings.isNotEmpty(schoolName.toString())) {
				if(Strings.isNotEmpty(departmentName)) {
					return departmentName + ", " + schoolName.toString();
				} else {
					return schoolName.toString();
				}
			}else {
				if(Strings.isNotEmpty(departmentName)) {
					return departmentName;
				} else {
					return null;
				}
			}
		}
		
		
		
	}
	
	SchooltestPersonInterest personinterest;
	
	public List<String> getPersonInterest() {
		_personInterestNameList=new ArrayList<String>();
		if(null != personService.getPersonInterestByPersonId(_personId)) {
			String personInterestName = personService.getPersonInterestByPersonId(_personId).getInterest();
//			System.out.println(personInterestName);
			if(Strings.isNotEmpty(personInterestName)){
				String[] personInterestNames = personInterestName.split(",");
				for (String _personInterestName:personInterestNames){
					_personInterestNameList.add(_personInterestName);
				}
			}
		}
		return _personInterestNameList;
	}

	public String getPersonTitle() {
		String result = null;
		if(null != personService.getPersonExt(_personId)) {
			result = personService.getPersonExt(_personId).getTitle();
		}
		return result;
	}	
		
	@Inject
	PersonService personService;
	
	@Inject
	InstituteService instituteService;
	
	@Inject
	DepartmentService departmentService;

	@Inject
	SchoolService schoolService;


}
