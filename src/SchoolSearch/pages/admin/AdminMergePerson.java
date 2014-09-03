package SchoolSearch.pages.admin;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.admin.MergePersonService;
import SchoolSearch.services.dao.person.model.Person;
import SchoolSearch.services.dao.person.model.PersonProfile;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonProfile;
import SchoolSearch.services.services.auth.annotation.RequireLogin;
import SchoolSearch.services.services.person.PersonService;
import SchoolSearch.services.utils.Strings;

@Import()
@RequireLogin(admin=true)
public class AdminMergePerson {
	@Property
	String IDString;
	
	@Property
	String NameString;
	
	@Property
	@Persist
	List<SchooltestPerson> personList;
	
	@Property
	Integer _index;
	
	@Property
	Person _person;

	List<Integer> mergePersonIdList;
	
	@InjectComponent
	Zone searchContentZone;
	
	Object setupRender() {
		if(null!=personList)
			personList.clear();
		personList = null;
		return true;
	}
	
	Object onSuccessFromAuthorSearchForm() {
		System.out.println(IDString + "\t" + NameString);
		if(Strings.isEmpty(IDString)) {
			List<String> nameList = new ArrayList<String>();
			String[] split = NameString.split("[,;]");
			for(String name : split) 
				nameList.add(name);
			personList = personService.getPersonByNameList(nameList);
		} else {
			List<Integer> idList = new ArrayList<Integer>();
			String[] split = IDString.split("[,;]");
			for(String s : split) {
				if(Strings.isNotEmpty(s)) {
					idList.add(Integer.parseInt(s));
				}
			}
			personList = personService.getPersonList(idList, true);
		}
		
		return searchContentZone.getBody();
	}

	public Boolean getCheckMergeValue() {
		return false;
	}
	
	public void setCheckMergeValue(Boolean checkMergeValue) {
		if(null == mergePersonIdList) {
			mergePersonIdList = new ArrayList<Integer>();
			_index = 0;
		}
		if(checkMergeValue) {
			mergePersonIdList.add(personList.get(_index).getId());
		}
		_index++;
	}
	
	void onSuccessFromselectMergeForm() {
		for(Integer id : mergePersonIdList) {
			System.out.println(id);
		}
		mergePersonService.mergePersons(mergePersonIdList);
		System.out.println("merged");
	}
	
	public SchooltestPersonProfile getPersonProfile() {
		return personService.getPersonProfile(_person.id);
	}
	
	
	@Inject
	PersonService personService;

	@Inject
	MergePersonService mergePersonService;
}
