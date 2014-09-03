package SchoolSearch.pages.admin.publication;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.ActivationRequestParameter;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson2publication;
import SchoolSearch.services.dao.schooltest.model.SchooltestPublication;
import SchoolSearch.services.services.auth.annotation.RequireLogin;
import SchoolSearch.services.services.person.PersonService;
import SchoolSearch.services.services.publication.PublicationService;

@Import()
@RequireLogin(admin=true)
public class AdminPublicationRelationManage {
	@Persist(PersistenceConstants.FLASH)
	@ActivationRequestParameter
	Integer id;
	
	@Property
	SchooltestPerson person;

	@Property
	List<SchooltestPerson> personByName;

	@Property
	List<SchooltestPublication> publications;

	
	public Object onActivate(EventContext context) {
		if(context.getCount()>0) {
			id = context.get(Integer.class, 0);
		}
		return true;
	}
	
	@SetupRender
	void setupRender() {
		List<SchooltestPerson2publication> personPublicationsByPersonId = pubService.getPersonPublicationsByPersonId(id);
		List<Integer> publicationIdList = new ArrayList<Integer>();
		for(SchooltestPerson2publication p2p : personPublicationsByPersonId) {
			publicationIdList.add(p2p.getPublicationId());
		}
		publications = pubService.getPublications(publicationIdList);
//		person = personService.getPerson(id);
//		personByName = personService.getPersonByName(person.getName());
//		personService.getPersonByName(name);
	}
	
	@Inject
	PublicationService pubService;
	
	@Inject
	PersonService personService;
}
