package SchoolSearch.pages.admin;

import java.util.List;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Service;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.dao.schooltest.model.SchooltestPublication;
import SchoolSearch.services.services.person.PersonService;
import SchoolSearch.services.services.publication.PublicationService;
import SchoolSearch.services.utils.Strings;

public class AdminModification {

	@Property
	Integer personId;
	
	@Property
	List<SchooltestPublication> publicationList;
	
	@Property
	SchooltestPublication publication;
	
	@Property
	Integer publicationId;
	
	@Property
	Integer index;
	
	@Property 
	Zone publicationListZone;
	
	
	public Object onActivate(EventContext context) {
		if(context.getCount()>0)
			personId = context.get(Integer.class, 0);
		return true;
	}
	
	@SetupRender
	void setupRender() {
	}
	
	@AfterRender
	void afterRender() {
	}
	
	
	public Integer getPublicationListSize() {
		return publicationList.size();
	}
	
	public Integer getLineNumber() {
		return publicationList.size() - index;
	}
	
	
	
	
	
	@Inject
	PublicationService publicationService;
	
	@Inject
	PersonService personService;
	
	
}
