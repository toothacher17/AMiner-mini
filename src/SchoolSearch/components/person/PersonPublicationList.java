package SchoolSearch.components.person;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.dao.schooltest.model.SchooltestPublication;
import SchoolSearch.services.services.auth.Authenticator;
import SchoolSearch.services.services.person.PersonService;
import SchoolSearch.services.services.publication.PublicationService;
import SchoolSearch.services.utils.ComparatorUtil;

@Import(library="context:/res/js/person/personinfoComponent.js")
public class PersonPublicationList {
	@Parameter(allowNull = false)
	Integer personId;
	
	@Property
	@Persist(PersistenceConstants.SESSION)
	Integer _personId;
	
	@Property
	final int recentSize = 10;
	
	@Property
	@Persist(PersistenceConstants.SESSION)
	Map<String, List<SchooltestPublication>> cacheMap;
	
	@Property
	@Persist(PersistenceConstants.SESSION)
	List<SchooltestPublication> publist;
	
	@Property
	@Persist(PersistenceConstants.SESSION)
	List<String> yearList;
	
	@Property
	Integer allSize;
	
	@Property
	String _year;
	
	@Property
	String currentSelect;
	
	@InjectComponent
	Zone authorPubZone;
	
	void setupRender() {
		this._personId = personId;
		if(null != cacheMap)
			cacheMap.clear();
		if(null != yearList)
			yearList.clear();
		this.cacheMap = new HashMap<String, List<SchooltestPublication>>();
		this.yearList = new ArrayList<String>();
		publist = publicationService.getPublicationsByPersonId(_personId);
		for(SchooltestPublication  p : publist) {
			String year = p.getYear();
			if(!cacheMap.containsKey(year))
				cacheMap.put(year, new ArrayList<SchooltestPublication>());
			cacheMap.get(year).add(p);
		}
		yearList.addAll(cacheMap.keySet());
		Collections.sort(yearList, Collections.reverseOrder());
		Collections.sort(publist, Collections.reverseOrder(new ComparatorUtil.GeneralComparator<SchooltestPublication>(SchooltestPublication.class, "year")));
		
		allSize = publist.size();
	}
	
	public int getPublicationSize() {
		return cacheMap.get(_year).size();
	}
	
	public List<SchooltestPublication> getShowPubList(){
		if(null == currentSelect || currentSelect.equals("recent")) {
			return publist.subList(0, Math.min(recentSize, publist.size()));
		} else if(currentSelect.equals("all")) {
			return publist;
		} else {
			System.out.println(cacheMap == null);
			return cacheMap.get(currentSelect);
		}
	}
	
	Object onSelect(String selectOption) {
		currentSelect = selectOption;
		return authorPubZone.getBody();
	}
	
	@Inject
	Authenticator authenticator;
	
	@Inject
	PersonService personService;
	
	@Inject
	PublicationService publicationService;
}
