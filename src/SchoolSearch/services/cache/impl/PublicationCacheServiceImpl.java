package SchoolSearch.services.cache.impl;
/**
 * @author GCR
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import SchoolSearch.services.cache.PublicationCacheService;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson2publication;
import SchoolSearch.services.dao.schooltest.model.SchooltestPublication;


public class PublicationCacheServiceImpl implements PublicationCacheService {
	//publication 2 person relation cache START
	Map<Integer, SchooltestPerson2publication> person2PublicationCache = new HashMap<Integer, SchooltestPerson2publication>();
	Map<Integer, List<SchooltestPerson2publication>> person2PublicationCacheIndexByPubId = new HashMap<Integer, List<SchooltestPerson2publication>>();
	Map<Integer, List<SchooltestPerson2publication>> person2PublicationCacheIndexByPersonId = new HashMap<Integer, List<SchooltestPerson2publication>>();
	
	@Override
	public void setPerson2PublicationIndexByPubId(Integer pubId, List<SchooltestPerson2publication> value) {
		List<Integer> p2pidList = new ArrayList<Integer>();
		for(SchooltestPerson2publication p2p : value) {
			p2pidList.add(p2p.getId());
			if(!person2PublicationCache.containsKey(p2p.getId()))
				person2PublicationCache.put(p2p.getId(), p2p);
		}
		
		if(person2PublicationCacheIndexByPubId.containsKey(pubId)) {
			person2PublicationCacheIndexByPubId.get(pubId).clear();
		} else {
			person2PublicationCacheIndexByPubId.put(pubId, new ArrayList<SchooltestPerson2publication>());
		}
		List<SchooltestPerson2publication> list = person2PublicationCacheIndexByPubId.get(pubId);
		for(Integer p2pid : p2pidList) {
			list.add(person2PublicationCache.get(p2pid));
		}
	}
	
	@Override
	public List<SchooltestPerson2publication> getPerson2PublicationListByPubId(Integer pubId) {
		return person2PublicationCacheIndexByPubId.get(pubId);
	}
	
	@Override
	public void setPerson2PublicationIndexByPersonId(Integer personId, List<SchooltestPerson2publication> value) {
		List<Integer> p2pidList = new ArrayList<Integer>();
		for(SchooltestPerson2publication p2p : value) {
			p2pidList.add(p2p.getId());
			if(!person2PublicationCache.containsKey(p2p.getId()))
				person2PublicationCache.put(p2p.getId(), p2p);
		}
		if(person2PublicationCacheIndexByPersonId.containsKey(personId)) {
			person2PublicationCacheIndexByPersonId.get(personId).clear();
		} else {
			person2PublicationCacheIndexByPersonId.put(personId, new ArrayList<SchooltestPerson2publication>());
		}
		List<SchooltestPerson2publication> list = person2PublicationCacheIndexByPersonId.get(personId);
		for(Integer p2pid : p2pidList) {
			list.add(person2PublicationCache.get(p2pid));
		}
	}
	
	@Override
	public List<SchooltestPerson2publication> getPerson2PublicationListByPersonId(Integer personId) {
		return person2PublicationCacheIndexByPersonId.get(personId);
	}
	//publication 2 person relation cache END

	

	Map<Integer, SchooltestPublication> publicationCache = new HashMap<Integer, SchooltestPublication>();
	
	@Override
	public void setPublicationCache(Integer key, SchooltestPublication value) {
		publicationCache.put(key, value);
	}

	@Override
	public SchooltestPublication getPublicationFromCache(Integer key) {
		return publicationCache.get(key);
	}

	Map<Integer, List<Integer>> publicationIdsCacheIndexByPersonId = new HashMap<Integer, List<Integer>>(); 
	@Override
	public void setPublicationIdsByPersonId(Integer key, List<Integer> value) {
		publicationIdsCacheIndexByPersonId.put(key, value);
	}

	@Override
	public List<Integer> getPublicationIdsByPersonId(Integer key) {
		return publicationIdsCacheIndexByPersonId.get(key);
	}
	
	Map<Integer, SchooltestPublication> publicationCacheByPubId = new HashMap<Integer, SchooltestPublication>();
	@Override
	public void setPublicationByPubId(Integer key, SchooltestPublication value) {
		publicationCacheByPubId.put(key, value);
	}

	@Override
	public SchooltestPublication getPublicationByPubId(Integer key) {
		return publicationCacheByPubId.get(key);
	}
	
	@Override
	public void clear() {
		person2PublicationCache.clear();
		person2PublicationCacheIndexByPubId.clear();
		person2PublicationCacheIndexByPersonId.clear();
		publicationIdsCacheIndexByPersonId.clear();
	}

}
