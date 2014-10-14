package SchoolSearch.components.admin;

import java.util.Collections;
import java.util.List;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson2publication;
import SchoolSearch.services.dao.schooltest.model.SchooltestPublication;
import SchoolSearch.services.services.person.PersonService;
import SchoolSearch.services.services.publication.PublicationService;
import SchoolSearch.services.utils.ComparatorUtil;
import SchoolSearch.services.utils.Strings;

public class AdminEditablePubList {
	@Parameter(allowNull = true)
	List<Integer> publicationIdList;

	@Parameter(allowNull = true)
	List<SchooltestPublication> publicationList;
	
	@Parameter(allowNull = false)
	Integer personId;

	@Parameter(allowNull = true, value = "year", defaultPrefix = BindingConstants.LITERAL)
	String orderBy;

	@Parameter(allowNull = true, value = "normal", defaultPrefix = BindingConstants.LITERAL)
	String showType;

	@Property
	Integer _personId;

	@Property
	SchooltestPerson person;

	@Property
	boolean _listShowType;

	@Property
	List<SchooltestPublication> _publicationList;

	@Property
	List<SchooltestPerson2publication> person2publicationList;

	@Property
	SchooltestPublication _publication;
	
	@Property
	Integer publicationId;

	@Property
	Integer index;

	@Property
	Integer authorIndex;

	@Property
	List<Integer> authorPosition;

	@Property
	String author;

	@Property
	Integer authorPersonId;

	@InjectComponent
	Zone publicationListZone;
	
	void setupRender() {
		if(null != personId)
			this._personId = personId;
		person = personService.getPerson(_personId);
//		if(null == publicationList) {
			_publicationList = publicationService.getPublicationsByPersonId(_personId);
			System.out.println("this is the test of whether the system is working " + _publicationList.size());
			if (orderBy.equalsIgnoreCase("year")) {
//				Collections.sort(_publicationList, Collections.reverseOrder(SchooltestPublication.defaultComparator));
				Collections.sort(_publicationList, Collections.reverseOrder(new ComparatorUtil.GeneralComparator<SchooltestPublication>(SchooltestPublication.class, "year")));
			}
//		} else {
//			_publicationList = publicationList;
//		}
	}

	String[] authorNames = null;

	public boolean getShowType() {
		boolean _listShowType = true;
		if (showType.equalsIgnoreCase("light")) {
			_listShowType = false;
		}
		return _listShowType;
	}

	public Integer[] getAllAuthorPersonId() {
		if (Strings.isNotEmpty(_publication.getAuthors()))
			authorNames = _publication.getAuthors().split("\\|\\|");
		else
			authorNames = new String[0];
		Integer length = authorNames.length;
		Integer[] allAuthorLink = new Integer[length];

		List<SchooltestPerson2publication> p2p = publicationService.getPersonPublicationsByPublicationId(_publication.getId());

		for (SchooltestPerson2publication p2ptemp : p2p) {
			allAuthorLink[p2ptemp.getPosition() - 1] = p2ptemp.getPersonId();
		}
		return allAuthorLink;
	}

	// 根据publication取出所有的用户的名字，并且split后
	public String getAuthorName() {
		return authorNames[authorIndex];
	}

	public Integer getPublicationListSize() {
		return _publicationList.size();
	}
	
	public boolean isLastAuthor() {
		return authorIndex >= authorNames.length - 1;
	}

	public boolean isSelf() {
		return authorPersonId == _personId;
	}

	public Integer getLineNumber() {
		return _publicationList.size() - index;
	}

	public String getPublicationBorderClass() {
		if (_publication.getType().equals("conf")) {
			return "borderConf";
		} else {
			return "borderJournal";
		}
	}

	public Object onDeletePublication(EventContext context) {
		this._personId = context.get(Integer.class, 1);
		publicationId = context.get(Integer.class, 0);
//		this._publicationList = publicationService.getPublicationsByPersonId(12);
		publicationService.updatePerson2Publication(_personId, publicationId);
		this._publicationList = publicationService.getPublicationsByPersonId(_personId);
		System.out.println("<><><>< the publication size is " +  _publicationList.size());
		return publicationListZone.getBody();
	} 
	
	@Inject
	PersonService personService;

	@Inject
	PublicationService publicationService;

}
