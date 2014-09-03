package SchoolSearch.services.cache.impl;
/**
 * @author GCR
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import SchoolSearch.services.cache.GraduatePublicationCacheService;
import SchoolSearch.services.dao.graduatePublication.model.GraduatePublication;
import SchoolSearch.services.dao.graduatePublication.model.Person2GraduatePublication;

public class GraduatePublicationCacheServiceImpl implements GraduatePublicationCacheService{
	
	Map<Integer, Person2GraduatePublication> person2GraduatePublicationCache = new HashMap<Integer, Person2GraduatePublication>();
	Map<Integer, List<Person2GraduatePublication>> person2GraduatePublicationCacheIndexByGraPubId = new HashMap<Integer, List<Person2GraduatePublication>>();
	Map<Integer, List<Person2GraduatePublication>> person2GraduatePublicationCacheIndexByPersonId = new HashMap<Integer, List<Person2GraduatePublication>>();
	
	@Override
	public void setPerson2GraduatePublicationIndexByGraPubId(Integer grapubId, List<Person2GraduatePublication> value) {
		List<Integer> p2gidList = new ArrayList<Integer>();
		for(Person2GraduatePublication p2g : value) {
			p2gidList.add(p2g.id);
			if(!person2GraduatePublicationCache.containsKey(p2g.id))
				person2GraduatePublicationCache.put(p2g.id, p2g);
		}
		
		if(person2GraduatePublicationCacheIndexByGraPubId.containsKey(grapubId)) {
			person2GraduatePublicationCacheIndexByGraPubId.get(grapubId).clear();
		} else {
			person2GraduatePublicationCacheIndexByGraPubId.put(grapubId, new ArrayList<Person2GraduatePublication>());
		}
		List<Person2GraduatePublication> list = person2GraduatePublicationCacheIndexByGraPubId.get(grapubId);
		for(Integer p2gid : p2gidList) {
			list.add(person2GraduatePublicationCache.get(p2gid));
		}
	}
	
	@Override
	public List<Person2GraduatePublication> getPerson2GraduatePublicationListByGraPubId(Integer grapubId) {
		return person2GraduatePublicationCacheIndexByGraPubId.get(grapubId);
	}
	
	@Override
	public void setPerson2GraduatePublicationIndexByPersonId(Integer personId, List<Person2GraduatePublication> value) {
		List<Integer> p2gidList = new ArrayList<Integer>();
		for(Person2GraduatePublication p2g : value) {
			p2gidList.add(p2g.id);
			if(!person2GraduatePublicationCache.containsKey(p2g.id))
				person2GraduatePublicationCache.put(p2g.id, p2g);
		}
		if(person2GraduatePublicationCacheIndexByPersonId.containsKey(personId)) {
			person2GraduatePublicationCacheIndexByPersonId.get(personId).clear();
		} else {
			person2GraduatePublicationCacheIndexByPersonId.put(personId, new ArrayList<Person2GraduatePublication>());
		}
		List<Person2GraduatePublication> list = person2GraduatePublicationCacheIndexByPersonId.get(personId);
		for(Integer p2gid : p2gidList) {
			list.add(person2GraduatePublicationCache.get(p2gid));
		}
	}
	
	@Override
	public List<Person2GraduatePublication> getPerson2GraduatePublicationListByPersonId(Integer personId) {
		return person2GraduatePublicationCacheIndexByPersonId.get(personId);
	}
	//publication 2 person relation cache END

	Map<Integer, GraduatePublication> graduatePublicationCache = new HashMap<Integer, GraduatePublication>();
	
	@Override
	public void setGraduatePublicationCache(Integer key,
			GraduatePublication value) {
		graduatePublicationCache.put(key, value);
	}

	@Override
	public GraduatePublication getGraduatePublicationFromCache(Integer key) {
		return graduatePublicationCache.get(key);
	}
	
	@Override
	public void clear() {
		person2GraduatePublicationCache.clear();
		person2GraduatePublicationCacheIndexByGraPubId.clear();
		person2GraduatePublicationCacheIndexByPersonId.clear();
	}
}
