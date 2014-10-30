package SchoolSearch.services.services.publication.impl;

/**
 * @author GCR
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.cache.PublicationCacheService;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPerson2publicationDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPublicationDAO;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson2publication;
import SchoolSearch.services.dao.schooltest.model.SchooltestPublication;
import SchoolSearch.services.services.publication.PublicationService;

public class PublicationServiceImpl implements PublicationService{
	
	@Inject 
	SchooltestPerson2publicationDAO personPublicationDao;
	
	@Inject 
	SchooltestPublicationDAO publicationDao;

	@Override
	public List<SchooltestPublication> getPublicationsByPersonId(Integer id) {
		List<Integer> publicatoin_ids = publicationCacheService.getPublicationIdsByPersonId(id);
		List<Integer> publication_ids = new ArrayList<Integer>();
		List<SchooltestPublication> publications = new ArrayList<SchooltestPublication>(); 
		if(null == publicatoin_ids || publication_ids.size() == 0) {
			List<SchooltestPerson2publication> result = personPublicationDao.selectByIntegerField("person_id", id);
			for(SchooltestPerson2publication everyresult : result) {
				if(everyresult.getConfirmed() == 1)
					publication_ids.add(everyresult.getPublicationId());
			}
			publicationCacheService.setPublicationIdsByPersonId(id, publication_ids);
			for(Integer publication_id : publication_ids) {
				SchooltestPublication pub = publicationCacheService.getPublicationFromCache(publication_id);
				if(null == pub) {
					pub = publicationDao.selectById(publication_id);
					publicationCacheService.setPublicationCache(publication_id, pub);
				}
				publications.add(pub);
			}
		}else {
			for(Integer publication_id : publicatoin_ids) {
				SchooltestPublication pub = publicationCacheService.getPublicationFromCache(publication_id);
				if(null == pub) {
					pub = publicationDao.selectById(publication_id);
					publicationCacheService.setPublicationCache(publication_id, pub);
				}
				publications.add(pub);
			}
		}
		return publications;
	}

	@Override
	public List<Integer> getPublicationsIdListByPersonId(Integer id) {
		List<Integer> publicatoin_ids = publicationCacheService.getPublicationIdsByPersonId(id);
		List<Integer> publication_ids = new ArrayList<Integer>();
		if(null == publicatoin_ids) {
			List<SchooltestPerson2publication> result = personPublicationDao.selectByIntegerField("person_id", id);
			for(SchooltestPerson2publication everyresult : result) {
//				publicatoin_ids.add(everyresult.getPublicationId());
				if(everyresult.getConfirmed() == 1)
					publication_ids.add(everyresult.getPublicationId());
			}
			publicationCacheService.setPublicationIdsByPersonId(id, publication_ids);
			return publication_ids;
		}else {
			return publicatoin_ids;
		}
	}

	@Override
	public List<SchooltestPublication> getPublications(List<Integer> pubIds) {
		if(null==pubIds)
			return null;
		if(pubIds.size() == 0)
			return new ArrayList<SchooltestPublication>();
		
		List<SchooltestPublication> result = new ArrayList<SchooltestPublication>();
		List<Integer> leftIDs = new ArrayList<Integer>();

		for(Integer pubId : pubIds) {
			SchooltestPublication pub = publicationCacheService.getPublicationByPubId(pubId);
			if(null == pub) {
				leftIDs.add(pubId);
			}
		}
		if(leftIDs.size() > 0) {
			List<SchooltestPublication> publications = publicationDao.selectByIdList(leftIDs);
			for(SchooltestPublication pub : publications) {
				publicationCacheService.setPublicationByPubId(pub.getId(), pub);
			}
		}
		for(Integer pubId : pubIds) {
			SchooltestPublication pub = publicationCacheService.getPublicationByPubId(pubId);
			if(null != pub) {
				result.add(pub);
			} 
		}
		return result;
	}

	@Override
	public List<SchooltestPerson2publication> getPersonPublicationsByPublicationId(
			Integer pub_id) {
		
		List<SchooltestPerson2publication> result = publicationCacheService.getPerson2PublicationListByPubId(pub_id);
		if(null == result) {
			result = personPublicationDao.selectByIntegerField("publication_id", pub_id);
			publicationCacheService.setPerson2PublicationIndexByPubId(pub_id, result);
		}
		return result;
	}

	@Override
	public List<SchooltestPerson2publication> getPersonPublicationsByPersonId(
			Integer person_id) {
		List<SchooltestPerson2publication> result = publicationCacheService.getPerson2PublicationListByPersonId(person_id);
		if(null == result || result.size() == 0) {
			result = personPublicationDao.selectByIntegerField("person_id", person_id);
//			List<SchooltestPerson2publication> tempList = personPublicationDao.selectByIntegerField("person_id", person_id);
//			for(SchooltestPerson2publication temp : tempList) {
//				if(temp.getRight() == 1) 
//					result.add(temp);
//			}
			publicationCacheService.setPerson2PublicationIndexByPersonId(person_id, result);
		}
		return result;
	}

	@Override
	public List<SchooltestPerson2publication> getPersonPublicationsByPersonIds(
			//TODO add cache to this Service.
			List<Integer> person_ids) {
		List<SchooltestPerson2publication> result = new ArrayList<SchooltestPerson2publication>();
		for(Integer pid : person_ids) {
			List<SchooltestPerson2publication> resulttmp = personPublicationDao.selectByIntegerField("person_id", pid);
//			for(SchooltestPerson2publication p2p : resulttmp) {
////				if(p2p.getRight() == 1) 
//					result.add(p2p);
//			}
			result.addAll(resulttmp);
		}
		return result;
	}
	
	@Inject
	PublicationCacheService publicationCacheService;

	@Override
	public void updatePerson2Publication(Integer personId, Integer publicationId) {
		// TODO Auto-generated method stub
		List<SchooltestPerson2publication> publications = getPersonPublicationsByPersonId(personId);
		for(SchooltestPerson2publication p2p : publications) {
//			System.out.println(">>>>>>>>> it works!");
			System.out.println("the p2p publication id is " + p2p.getPublicationId());
			if (p2p.getPublicationId().equals(publicationId)) {
				System.out.println(">>>>>>>>> it matchs!");
				p2p.setLabel(1);
				p2p.setConfirmed(0);
				personPublicationDao.update(p2p);
				System.out.println(" >>>>>> sucessfully update!sss");
//				publicationCacheService.setPerson2PublicationIndexByPersonId(personId, null);
				
				publicationCacheService.getPerson2PublicationListByPersonId(personId).clear();
				System.out.println(" >>>>>> sucessfully clear");
				
//				publicationCacheService.setPublicationIdsByPersonId(personId, null);
				
				publicationCacheService.getPublicationIdsByPersonId(personId).clear();
				
//				publicationCacheService.setPerson2PublicationIndexByPubId(publicationId, null);
				
				publicationCacheService.getPerson2PublicationListByPubId(publicationId).clear();
				break;
			}
		}
	}

//	@Override
//	public List<SchooltestPublication> sortByScore(List<SchooltestPublication> input) {
//		
//		
//		Collections.sort(input, new Comparator<SchooltestPublication>() {
//
//			@Override
//			public int compare(SchooltestPublication o1, SchooltestPublication o2) {
//				// TODO Auto-generated method stub
//				if(Double.parseDouble(o1.getScore()) == Double.parseDouble(o2.getSource())) 
//					return Integer.parseInt(o1.getYear()) - Integer.parseInt(o2.getYear());
//				else if (Double.parseDouble(o1.get)) {
//					
//				} else {
//					
//				}
//					
//			}
//		
//		});
//		
//		
//		
//		return input;
//	}

}
