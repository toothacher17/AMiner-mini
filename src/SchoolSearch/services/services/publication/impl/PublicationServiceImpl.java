package SchoolSearch.services.services.publication.impl;

/**
 * @author GCR
 */
import java.util.ArrayList;
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
		if(null == publicatoin_ids) {
			List<SchooltestPerson2publication> result = personPublicationDao.selectByIntegerField("person_id", id);
			for(SchooltestPerson2publication everyresult : result) {
//				publicatoin_ids.add(everyresult.getPublicationId());
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
		if(null == result) {
			result = personPublicationDao.selectByIntegerField("person_id", person_id);
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
			result.addAll(resulttmp);
		}
		return result;
	}
	
	@Inject
	PublicationCacheService publicationCacheService;


}
