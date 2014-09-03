package SchoolSearch.pages.person;

import java.util.List;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.ActivationRequestParameter;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.RequestParameter;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;


import SchoolSearch.services.dao.schooltest.model.SchooltestCourse;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonExt;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonInfo;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonProfile;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonrelation;
import SchoolSearch.services.monitor.MonitorService;
import SchoolSearch.services.services.course.CourseService;
import SchoolSearch.services.services.graduatePublication.GraduatePublicationService;
import SchoolSearch.services.services.person.PersonRelationService;
import SchoolSearch.services.services.person.PersonService;
import SchoolSearch.services.services.publication.PublicationService;
import SchoolSearch.services.utils.Strings;

@Import(stylesheet = {"context:/res/css/pages/person/person.css"})
public class PersonIndex {
	@Persist(PersistenceConstants.FLASH)
	@ActivationRequestParameter
	Integer id;

	@Property
	SchooltestPerson person;
	
	@Property
	SchooltestPersonProfile personProfile;
	
	@Property
	List<Integer> publicationIdList;
//
//	@Property
//	List<Integer> graduateStudentPublicationIdList;
//
//	@Property
//	List<Integer> graduateTutorPublicationIdList;
//	
	@Property
	List<SchooltestCourse> courseList;
	
	@Property
	SchooltestPersonInfo personInfo;
	
	@Property
	SchooltestPersonExt personExt;

	@Property
	List<SchooltestPersonrelation> personRelation;
	
	String searchQuery;
	
	public Object onActivate(EventContext context) {
		if(context.getCount()>0) {
			id = context.get(Integer.class, 0);
			if(context.getCount()>1) {
				searchQuery = context.get(String.class, 1);
//				System.out.println("searchQuery" + searchQuery);
			}
		}
		return true;
	}
	
	@SetupRender
	void setupRender() {
		monitor.visitPage("PersonIndex", id.toString());
		
		person = personService.getPerson(id);
		personProfile = personService.getPersonProfile(id);
		publicationIdList = publicationService.getPublicationsIdListByPersonId(id);
		personInfo = personService.getPersonInfo(id);
		personExt = personService.getPersonExt(id);
//		graduateStudentPublicationIdList = graduatePublicationService.getGraduateStudentPublicationIdListByPersonId(id);
//		graduateTutorPublicationIdList = graduatePublicationService.getGraduateTutorPublicationIdListByPersonId(id);
		courseList = courseService.getRelatedCourseList(id);
	}
	
	public void activate(Integer id) {
		this.id = id;
	}
	
	public String getTitle() {
		if(null != person) {
			if(null != personProfile && Strings.isNotEmpty(personProfile.getPosition()))
				return person.getName() + " - " + personProfile.getPosition();
			else
				return person.getName();
		} else {
			return "校园搜索";
		}
	}
	
	public boolean getPublicationFlag(){
		boolean flag=false;
		if(publicationIdList.size()>=5)
			flag=true;
		return flag;
	}
	
	public boolean getPersonRelationGraph() {
		boolean flag = false;
		personRelation = personRelationService.getExistedPersonRelationByPersonId(id);
		if(personRelation.size() > 0) {
			flag = true;
		}
		return flag;
	}
	
	
	@Inject
	PersonService personService;
	
	@Inject
	PersonRelationService personRelationService;
	
	@Inject
	PublicationService publicationService;
	
	@Inject
	GraduatePublicationService graduatePublicationService;
	
	@Inject
	CourseService courseService;
	
	@Inject
	private MonitorService monitor;
}
