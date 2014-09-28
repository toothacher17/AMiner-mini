package SchoolSearch.components.person;

import java.util.List;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;


import SchoolSearch.pages.person.PersonIndex;
import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonProfile;
import SchoolSearch.services.dao.user.model.User;
import SchoolSearch.services.monitor.MonitorService;
import SchoolSearch.services.services.auth.Authenticator;
import SchoolSearch.services.services.organizationLevels.DepartmentService;
import SchoolSearch.services.services.organizationLevels.SchoolService;
import SchoolSearch.services.services.person.PersonService;
import SchoolSearch.services.utils.ReflectUtils;
import SchoolSearch.services.utils.Strings;

@Import(library = ("context:/res/js/jquery.form.js"))
public class PersonProfileComponent {
	
	@Parameter(allowNull = false)
	Integer personId;
	@Property
	Integer _personId;
	@Property
	boolean isEdit = false;
	@Property
	String name;
	@Property
	String position;
	@Property
	String location;
	@Property
	String phone;
	@Property
	String email;
	@Property
	String homepage;
	@Property
	Integer author_id;

	@InjectComponent
	Zone profileZone;

	@InjectPage
	PersonIndex perosnIndex;
	
	@Inject
	Request request;
	
	void setupRender() {
//		isEditAvatar = true;
		this._personId = personId;
		prepareData(_personId);
	}

	
	public Object onSuccessFromProfileEditForm() {
		isEdit = false;
		User user = getUser();

		if (null != user) {
			if (user.isAdmin()) {
				SchooltestPerson person = personService.getPerson(_personId);
				if (!person.getName().equals(name)) {
					personService.updatePerson(_personId, name, person.getNameAlias(), person);
				}
				person.setName(name);
			}
			
			SchooltestPersonProfile personProfile = personService.getPersonProfile(_personId);
			
			SchooltestPersonProfile newObj = new SchooltestPersonProfile(//
					personProfile.getId(), personProfile.getNo(), position, personProfile.getDepartmentId(), personProfile.getDepartmentKey(), location, phone, email, homepage, personProfile.getImagelink(), author_id);
			try {
				monitorService.multipleEditionAction("PersonProfileComponent", personProfile, newObj, ReflectUtils.getFiledList(//
						SchooltestPersonProfile.class, new String[]{"position", "location", "phone", "email", "homepage","author_id"}));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			
			personService.updatePersonProfile(_personId, position, location, phone, email, homepage, author_id, newObj);
//			personProfile.updateProfile(position, location, phone, email, homepage, personProfile.getImagelink(), author_id);
		}
		prepareData(_personId);
		return profileZone.getBody();
	}

	public Object onActionFromEditProfile(Integer personId) {
		_personId = personId;
		isEdit = true;
		prepareData(_personId);
		if(request.isXHR()) return profileZone.getBody(); 
		else return null;
//		System.out.println(">>><<<" + request);
//		return profileZone.getBody();
	}

	public Object onActionFromCancel(Integer personId) {
		_personId = personId;
		isEdit = false;
		prepareData(_personId);
		return profileZone.getBody();
	}

	private void prepareData(int personId) {
		SchooltestPerson person = personService.getPerson(personId);
		name = person.getName();
		SchooltestPersonProfile profile = personService.getPersonProfile(personId);
		position = profile.getPosition();
		location = profile.getLocation();
		phone = profile.getPhone();
		email = profile.getEmail();
		homepage = profile.getHomepage();
		author_id = profile.getAuthorId();
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
	
	
	public User getUser() {
		return authenticator.getCurrentUser();
	}

	@Property
	static String defaultAvatar = ConsistanceService.get("avatar.default");
	
	static String avatarPath = ConsistanceService.get("avatar.path");

	@Inject
	Authenticator authenticator;

	@Inject
	MonitorService monitorService;
	
	@Inject
	DepartmentService departmentService;
	
	@Inject
	SchoolService schoolService; 
	
	
	@Inject
	PersonService personService;
}
