package SchoolSearch.components.graduatepublication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.dao.graduatePublication.model.GraduatePublication;
import SchoolSearch.services.dao.graduatePublication.model.Person2GraduatePublication;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;
import SchoolSearch.services.services.graduatePublication.GraduatePublicationService;
import SchoolSearch.services.services.person.PersonService;
import SchoolSearch.services.utils.Strings;

public class GraduatePublicationList {
	@Parameter(allowNull = false)
	Integer personId;

	@Parameter(allowNull = false, defaultPrefix = BindingConstants.LITERAL)
	String type;

	@Parameter(allowNull = true, value = "year", defaultPrefix = BindingConstants.LITERAL)
	String orderBy;

	@Parameter(allowNull = false)
	List<Integer> graduatePublicationIdList;

	// @Parameter(allowNull = false)
	// List<Integer> graduatePublicationIdList;
	//
	// @Parameter(allowNull = false)
	// List<Integer> graduatePublicationIdList;
	//
	@Property
	Integer _personId;

	@Property
	SchooltestPerson _person;

	// @Property
	// Integer authorId;

	@Property
	Integer TutorPersonId;

	@Property
	List<GraduatePublication> graduatepublicationList;

	@Property
	GraduatePublication graduatepublication;

	@Property
	Integer index;

	@Property
	Integer tutorindex;

	void setupRender() {
		this._personId = personId;
		_person = personService.getPerson(_personId);
		graduatepublicationList = graduatePublicationService.getGraduatePublications(graduatePublicationIdList);
		if (null != graduatepublicationList) {
			if (orderBy.equalsIgnoreCase("year")) {
				Collections.sort(graduatepublicationList, Collections.reverseOrder(GraduatePublication.defaultComparator));
			}
		}
	}

	// 现在我已经拿到一个具体的grapub，然后开始进行页面上的逻辑显示

	// 在tml端需要判断的是两个tutor的类型
	// private Integer getAllAuthorNameLength(GraduatePublication grapub){
	// String AllAuthorNames = grapub.tutor;
	// String []AuthorName = AllAuthorNames.split("\\|\\|");
	// Integer length = 0;
	// length=AuthorName.length;
	// return length;
	// }

	public Integer getauthorId() {
		Integer id = null;
		List<Person2GraduatePublication> p2g = new ArrayList<Person2GraduatePublication>();
		p2g = graduatePublicationService.getPersonGraduatePublicationsBygrapubId(graduatepublication.id);
		for (Person2GraduatePublication p2gtemp : p2g) {
			if (p2gtemp.type.equalsIgnoreCase("author"))
				id = p2gtemp.person_id;
		}
		// authorId=id;
		return id;
	}

	Integer selfid = null;

	// final int tutorSize = 2;
	int currentLength = 0;
	public Integer[] getAllTutorPersonId() {
		List<String> tutorNameList = new ArrayList<String>();
		if(Strings.isNotEmpty(graduatepublication.tutor1_name)) 
			tutorNameList.add(graduatepublication.tutor1_name);
		if(Strings.isNotEmpty(graduatepublication.tutor2_name)) 
			tutorNameList.add(graduatepublication.tutor2_name);
		currentLength = tutorNameList.size();
		
		Integer[] allTutorPersonId = new Integer[currentLength];

		List<Person2GraduatePublication> p2gList = new ArrayList<Person2GraduatePublication>();
		p2gList = graduatePublicationService.getPersonGraduatePublicationsBygrapubId(graduatepublication.id);
		Collections.sort(p2gList, Person2GraduatePublication.positionComparator);
		for (Person2GraduatePublication p2g : p2gList) {
			if(p2g.type.equalsIgnoreCase("tutor")) { 
				allTutorPersonId[p2g.position-1]=p2g.person_id;
			}
		}
		return allTutorPersonId;
	}

	public boolean isLastTutor() {
		return !(tutorindex < (currentLength - 1));
	}

	public boolean getauthorisselfid() {
		boolean flag = false;
		if (this.getauthorId() == personId) {
			flag = true;
		}
		return flag;
	}
	
	
	public boolean checkGrapubEnglishName(){
		boolean flag= false;
		if(graduatepublication.title_en!=null){
			flag = true;
		}
		return flag;
	}

	public boolean gettutorisselfid() {
		boolean flag = false;
		if (TutorPersonId == personId) {
			flag = true;
		}
		return flag;
	}

	public String getTutorName() {
		if (tutorindex == 0)
			return graduatepublication.tutor1_name;
		else
			return graduatepublication.tutor2_name;
	}

	public Integer getLineNumber() {
		return graduatepublicationList.size() - index;
	}

	@Inject
	PersonService personService;

	@Inject
	GraduatePublicationService graduatePublicationService;

}
