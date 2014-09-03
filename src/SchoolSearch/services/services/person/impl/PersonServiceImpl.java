package SchoolSearch.services.services.person.impl;

/**
 * @author GCR
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.cache.PersonCacheService;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPerson2departmentDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPersonDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPersonExtDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPersonInfoDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPersonInterestDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPersonProfileDAO;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson2department;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonExt;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonInfo;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonInterest;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonProfile;
import SchoolSearch.services.services.auth.Authenticator;
import SchoolSearch.services.services.editLog.EditLogService;
import SchoolSearch.services.services.person.PersonService;
import SchoolSearch.services.utils.Strings;
import SchoolSearch.services.utils.T5RegistryHelper;
import SchoolSearch.services.dao.schooltest.*;



public class PersonServiceImpl implements PersonService {
	@Override
	public SchooltestPerson getPerson(Integer id) {
		return getPerson(id, false);
	}

	@Override
	public SchooltestPerson getPerson(Integer id, boolean forceLoadDB) {
		if (null == id)
			return null;
		SchooltestPerson person = null;
		if(!forceLoadDB) {
			person = personcacheService.getPersonFromCache(id);
		}
		if (null == person) {
			person = personDao.selectById(id);
			personcacheService.setPersonCache(id, person);
		}
		return person;
	}
	
	@Override
	public List<SchooltestPerson> getPersonList(List<Integer> ids) {
		if(null==ids)
			return null;
		if(ids.size() == 0)
			return new ArrayList<SchooltestPerson>();
		List<SchooltestPerson> result = new ArrayList<SchooltestPerson>();
		List<Integer> leftIDs = new ArrayList<Integer>();
		for(Integer id : ids) {
			SchooltestPerson person = personcacheService.getPersonFromCache(id);
			if(null != person) {
				result.add(person);
			} else {
				leftIDs.add(id);
			}
		}
		if(leftIDs.size() > 0) {
			List<SchooltestPerson> persons = personDao.selectByIdList(leftIDs);
			for(SchooltestPerson person : persons) {
				personcacheService.setPersonCache(person.getId(), person);
			}
			result.addAll(persons);
		}
		return result;
	}
	
	@Override
	public List<SchooltestPerson> getPersonList(List<Integer> ids, boolean forceLoadDB) {
		if(forceLoadDB == false)
			return getPersonList(ids);
		if(null==ids)
			return null;
		if(ids.size() == 0)
			return new ArrayList<SchooltestPerson>();
		List<SchooltestPerson> result = personDao.selectByIdList(ids);
		for(SchooltestPerson person : result) {
			personcacheService.setPersonCache(person.getId(), person);
		}
		return result;
	}
	
	@Override
	public void createPerson(Integer id) {
		if(null == id)
			return;
		SchooltestPerson person = personcacheService.getPersonFromCache(id);
		if(null != person) {
			return;
		}
		SchooltestPerson newPerson = new SchooltestPerson();
		newPerson.setId(id);
		personDao.insert(newPerson);
		personcacheService.setPersonCache(id, newPerson);
	}
	
	@Override
	public List<SchooltestPerson> getAllPersons() {
		return personDao.walkAll();
	}
	
	/**
	 *  not cached
	 */
	@Override
	public List<SchooltestPerson> getPersonByName(String name) {	
		List<SchooltestPerson> personByName = personDao.selectByStringField("name", name);
		return personByName;
	}
	
	@Override
	public List<SchooltestPerson> getPersonByNameList(List<String> nameList) {
		List<SchooltestPerson> personByNameList = personDao.selectByMultipleStringField("name", nameList);
		return personByNameList;
	}
	
	
	@Override
	public SchooltestPersonProfile getPersonProfile(Integer id) {
		if (null == id)
			return null;

		SchooltestPersonProfile personprofile = personcacheService.getPersonProfileFromCache(id);
		if (null == personprofile) {
			personprofile = personProfileDao.selectById(id);
			personcacheService.setPersonProfileCache(id, personprofile);
		}
		return personprofile;
	}
	
	@Override
	public List<SchooltestPersonProfile> getPersonProfile(List<Integer> ids) {
		if(null==ids)
			return null;
		if(ids.size() == 0)
			return new ArrayList<SchooltestPersonProfile>();
		List<SchooltestPersonProfile> result = new ArrayList<SchooltestPersonProfile>();
		List<Integer> leftIDs = new ArrayList<Integer>();
		for(Integer id : ids) {
			SchooltestPersonProfile person = personcacheService.getPersonProfileFromCache(id);
			if(null != person) {
				result.add(person);
			} else {
				leftIDs.add(id);
			}
		}
		if(leftIDs.size() > 0) {
			List<SchooltestPersonProfile> persons = personProfileDao.selectByIdList(leftIDs);
			for(SchooltestPersonProfile person : persons) {
				personcacheService.setPersonProfileCache(person.getId(), person);
			}
			result.addAll(persons);
		}
		return result;
	}
	
	@Override
	public void createPersonProfile(Integer id) {
		if(null == id)
			return;
		SchooltestPersonProfile personProfile = personcacheService.getPersonProfileFromCache(id);
		if(null != personProfile) {
			return;
		}
		SchooltestPersonProfile newPersonProfile = new SchooltestPersonProfile();
		newPersonProfile.setId(id);
		personProfileDao.insert(newPersonProfile);
		personcacheService.setPersonProfileCache(id, newPersonProfile);
	}

	@Override
	public SchooltestPersonInfo getPersonInfo(Integer id) {
		if (null == id)
			return null;

		SchooltestPersonInfo personInfo = personcacheService.getPersonInfoFromCache(id);
		if (null == personInfo) {
			personInfo = personInfoDao.selectById(id);
			personcacheService.setPersonInfoCache(id, personInfo);
		}
		return personInfo;
	}

	@Override
	public void createPersonInfo(Integer id) {
		if(null == id)
			return;
		SchooltestPersonInfo personInfo = personcacheService.getPersonInfoFromCache(id);
		if(null != personInfo) {
			return;
		}
		SchooltestPersonInfo newPersonInfo = new SchooltestPersonInfo();
		newPersonInfo.setId(id);
		personInfoDao.insert(newPersonInfo);
		personcacheService.setPersonInfoCache(id, newPersonInfo);
	}
	
	@Override
	public SchooltestPersonExt getPersonExt(Integer id) {
		if (null == id)
			return null;
		
		SchooltestPersonExt personext = personcacheService.getPersonExtFromCache(id);
		
		if (null == personext) {
			personext = personExtDao.selectById(id);
			personcacheService.setPersonExtCache(id, personext);
		}
		return personext;
	}
	
	
	@Override
	public List<SchooltestPersonExt> getPersonExtList(List<Integer> ids) {
		if(null==ids)
			return null;
		if(ids.size() == 0)
			return new ArrayList<SchooltestPersonExt>();
		List<SchooltestPersonExt> result = new ArrayList<SchooltestPersonExt>();
		List<Integer> leftIDs = new ArrayList<Integer>();
		for(Integer id : ids) {
			SchooltestPersonExt personExt = personcacheService.getPersonExtFromCache(id);
			if(null != personExt) {
				result.add(personExt);
			} else {
				leftIDs.add(id);
			}
		}
		if(leftIDs.size() > 0) {
			List<SchooltestPersonExt> personExts = personExtDao.selectByIdList(leftIDs);
			for(SchooltestPersonExt personExt : personExts) {
				personcacheService.setPersonExtCache(personExt.getId(), personExt);
			}
			result.addAll(personExts);
		}
		return result;
	}
	
	@Override
	public void createPersonExt(Integer id) {
		if(null == id)
			return;
		SchooltestPersonExt personExt = personcacheService.getPersonExtFromCache(id);
		if(null != personExt) {
			return;
		}
		SchooltestPersonExt newPersonExt = new SchooltestPersonExt();
		newPersonExt.setId(id);
		personExtDao.insert(newPersonExt);
		personcacheService.setPersonExtCache(id, newPersonExt);
	}
	
	@Override
	public List<Integer> getAllPersonExtIds() {
		List<SchooltestPersonExt> personExtList = personExtDao.walkAll();
		List<Integer> AllPersonExtIds = new ArrayList<Integer>();
		for(SchooltestPersonExt pe : personExtList) {
			AllPersonExtIds.add(pe.getId());
		}
		return AllPersonExtIds;
	}

	
	
	@Override
	public Map<String, List<Integer>> getTitleClustedPersons(List<Integer> ids) {
		List<SchooltestPersonExt> personExtList = getPersonExtList(ids);
		Map<String, List<Integer>> result = new HashMap<String, List<Integer>>();
		for(SchooltestPersonExt pe : personExtList) {
			if(null==pe.getTitle())
				continue;
//			
			
			String[] split = pe.getTitle().split("[;，；,\n]");
			for(String title : split) {
				title = title.trim();
				if(!result.containsKey(title)) 
					result.put(title, new ArrayList<Integer>());
				result.get(title).add(pe.getId());
			}
		}
		return result;
	}
	
	@Override
	public Map<String, List<Integer>> getTitleClustedPersons() {
		List<SchooltestPersonExt> walkAll = personExtDao.walkAll();
		Map<String, List<Integer>> result = new HashMap<String, List<Integer>>();
		for(SchooltestPersonExt pe : walkAll) {
			personcacheService.setPersonExtCache(pe.getId(), pe);
			if(null==pe.getTitle())
				continue;
			
			String[] split = pe.getTitle().split("[;，；,\n]");
			for(String title : split) {
				title = title.trim();
				if(!result.containsKey(title)) 
					result.put(title, new ArrayList<Integer>());
				result.get(title).add(pe.getId());
			}
		}
		return result;
	}
	
	
	@Override
	public SchooltestPersonInterest getPersonInterestByPersonId(Integer person_id) {
//		SchooltestPersonInterest result = personInterestDao.selectById(person_id);
		return personInterestDao.selectSingleByIntegerField("person_id", person_id);
	}

	@Override
	public List<SchooltestPersonInterest> getPersonInterestByPersonIds(List<Integer> person_id) {
		List<SchooltestPersonInterest> result = personInterestDao.selectByIdList(person_id);
		return result;
	}

//	use the author id from original data as the query key
	@Override
	public SchooltestPersonInterest getPersonInterestByAid(Long aid) {
//		SchooltestPersonInterest result = personInterestDao.selectById(aid);
//		TODO use the author id as the query key
		return new SchooltestPersonInterest();
	}

	@Override
	public List<SchooltestPersonInterest> getPersonInterestByAids(List<Long> aids) {
//		List<SchooltestPersonInterest> result = personInterestDao.getPersonInterestByAids(aids);
//		TODO use the aid list as the query key
		List<SchooltestPersonInterest> result = new ArrayList<SchooltestPersonInterest>();
		return result;
	}

	@Override
	public void updatePerson(Integer id, String name, String name_alias,
			SchooltestPerson op) {
		personDao.update(op);	
	}

	@Override
	public void updatePersonProfile(Integer id, String position,
			String location, String phone, String email, String homepage,
			Integer author_id, SchooltestPersonProfile op) {
		personProfileDao.update(op);
		personcacheService.setPersonProfileCache(id, op);
	}
	
	@Override
	public void updatePersonProfileAvatar(Integer id, String avatar, SchooltestPersonProfile op) {
//		personProfileDao.updateImageInPersonProfile(id, avatar, op);
		String oldvalue = op.getImagelink();
		op.setImagelink(avatar);
		personProfileDao.update(op);
		boolean dolog = false;
		if(null == oldvalue) {
			if(null != avatar) {
				dolog = true;
			}
		}else if (!oldvalue.equals(avatar)) {
			dolog = true;
		}
		if(dolog) 
			logService.log("person_profile", op.getId(), "imagelink", oldvalue, avatar);
	}

	@Override
	public void updatePersonInfo(Integer id, String field_name,
			String new_value, SchooltestPersonInfo op) {
		personInfoDao.update(op);	
	}

	@Override
	public void updatePersonExt(Integer id, String field_name,
			String new_value, SchooltestPersonExt op) {
		personExtDao.update(op);
	}
	
//	@Override
//	public void updatePersonExt(Integer id, String field_name,
//			String new_value, SchooltestPersonExt op) {
//		personExtDao.updatePersonExt(id, field_name, new_value, op);
//	}
	
//	public static void main(String[] args) {
//		PersonService p = T5RegistryHelper.getService(PersonService.class);
//		PersonExt pe = p.getPersonExt(89);
//		System.out.println(pe.title);
//	}
	@Override
	public List<SchooltestPerson> getPersonListByDepartmentId(Integer id) {
		List<SchooltestPerson2department> tmp = person2departmentDao.selectByIntegerField("department_id", id);
		List<SchooltestPerson> result = new ArrayList<SchooltestPerson>();
		for(SchooltestPerson2department p2d : tmp) {
			result.add(personDao.selectById(p2d.getPersonId()));
		}
		return result;
	}
	
	@Override
	public String getAvatar(Integer id) {
		String path = ConsistanceService.get("avatar.url");
		if(null == id)
			return null;
		SchooltestPersonProfile tmp = personcacheService.getPersonProfileFromCache(id);
		if(null == tmp) {
			tmp = personProfileDao.selectById(id);
			personcacheService.setPersonProfileCache(id, tmp);
		}
		if(Strings.isEmpty(tmp.getImagelink()) || tmp.getImagelink().equals("upload"))
			return path + tmp.getId() + ".jpg"; 
		else
			return tmp.getImagelink();
	}
	@Inject
	SchooltestPersonDAO personDao;
	@Inject
	SchooltestPersonProfileDAO personProfileDao;
	@Inject
	SchooltestPersonExtDAO personExtDao;
	@Inject
	SchooltestPersonInfoDAO personInfoDao;
	@Inject
	SchooltestPersonInterestDAO personInterestDao;
	@Inject
	PersonCacheService personcacheService;
	@Inject
	Authenticator authenticator;
	@Inject
	SchooltestPerson2departmentDAO person2departmentDao;
	@Inject
	EditLogService logService;
}
