package SchoolSearch.services.admin.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.admin.MergePersonService;
import SchoolSearch.services.cache.PersonCacheService;
import SchoolSearch.services.dao.course.Person2CourseDao;
import SchoolSearch.services.dao.course.model.Person2Course;
import SchoolSearch.services.dao.graduatePublication.Person2GraduatePublicationDao;
import SchoolSearch.services.dao.graduatePublication.model.Person2GraduatePublication;
import SchoolSearch.services.dao.oranizationLevels.Person2InstituteDao;
import SchoolSearch.services.dao.oranizationLevels.model.Person2Institute;
import SchoolSearch.services.dao.person.PersonDao;
import SchoolSearch.services.dao.person.PersonExtDao;
import SchoolSearch.services.dao.person.PersonInfoDao;
import SchoolSearch.services.dao.person.PersonProfileDao;
import SchoolSearch.services.dao.person.PersonRelationDao;
import SchoolSearch.services.dao.person.model.PersonExt;
import SchoolSearch.services.dao.person.model.PersonInfo;
import SchoolSearch.services.dao.person.model.PersonProfile;
import SchoolSearch.services.dao.publication.Person2PublicationDao;
import SchoolSearch.services.dao.publication.model.Person2Publication;
import SchoolSearch.services.utils.T5RegistryHelper;

public class MergePersonServiceImpl implements MergePersonService {

	@Override
	public void mergePersons(List<Integer> personIds) {
		int personFinalId = personIds.get(0);
//		List<Integer> restPersonIds = personIds.subList(1, personIds.size());
		//step1, merge Relation
		List<Person2Institute> person2InstituteByPersonIds = p2iDao.getPerson2InstituteByPersonIds(personIds);
		Set<Integer> institutionIdSet = new HashSet<Integer>();
		List<Person2Institute> p2iupdatePrepare = new ArrayList<Person2Institute>();
		List<Integer> p2ideletePrepare = new ArrayList<Integer>();
		for(Person2Institute p2i : person2InstituteByPersonIds) {
			if(institutionIdSet.contains(p2i.institute_id)) {
				p2ideletePrepare.add(p2i.id);
			} else {
				institutionIdSet.add(p2i.institute_id);
				if(p2i.person_id != personFinalId) {
					p2i.person_id = personFinalId;
					p2iupdatePrepare.add(p2i);
				}
			}
		}
		p2iDao.updateBatch(p2iupdatePrepare);
		p2iDao.deleteBatch(p2ideletePrepare);
		
		List<Person2Publication> person2PublicationByPersonIds = p2pDao.getPerson2PublicationByPersonIds(personIds);
		Set<Integer> publicationIdSet = new HashSet<Integer>();
		List<Person2Publication> p2pupdatePrepare = new ArrayList<Person2Publication>();
		List<Integer> p2pdeletePrepare = new ArrayList<Integer>();
		for(Person2Publication p2p : person2PublicationByPersonIds) {
			if(publicationIdSet.contains(p2p.publication_id)) {
				p2pdeletePrepare.add(p2p.id);
			} else {
				publicationIdSet.add(p2p.publication_id);
				if(p2p.person_id != personFinalId) {
					p2p.person_id = personFinalId;
					p2pupdatePrepare.add(p2p);
				}
			}
		}
		p2pDao.updateBatch(p2pupdatePrepare);
		p2pDao.deleteBatch(p2pdeletePrepare);
		
		List<Person2GraduatePublication> person2GraduatePublicationByPersonIds = p2gDao.getPerson2GraduatePublicationByPersonIds(personIds);
		Set<Integer> graduatepublicationIdSet = new HashSet<Integer>();
		List<Person2GraduatePublication> p2gupdatePrepare = new ArrayList<Person2GraduatePublication>();
		List<Integer> p2gdeletePrepare = new ArrayList<Integer>();
		for(Person2GraduatePublication p2g : person2GraduatePublicationByPersonIds) {
			if(graduatepublicationIdSet.contains(p2g.graduatePublication_id)) {
				p2gdeletePrepare.add(p2g.id);
			} else {
				graduatepublicationIdSet.add(p2g.graduatePublication_id);
				if(p2g.person_id != personFinalId) {
					p2g.person_id = personFinalId;
					p2gupdatePrepare.add(p2g);
				}
			}
		}
		p2gDao.updateBatch(p2gupdatePrepare);
		p2gDao.deleteBatch(p2gdeletePrepare);
		
		List<Person2Course> person2CourseByPersonIds = p2cDao.getPerson2CourseByPersonIds(personIds);
		Set<Integer> courseIdSet = new HashSet<Integer>();
		List<Person2Course> p2cupdatePrepare = new ArrayList<Person2Course>();
		List<Integer> p2cdeletePrepare = new ArrayList<Integer>();
		for(Person2Course p2c : person2CourseByPersonIds) {
			if(courseIdSet.contains(p2c.course_id)) {
				p2cdeletePrepare.add(p2c.id);
			} else {
				courseIdSet.add(p2c.course_id);
				if(p2c.person_id != personFinalId) {
					p2c.person_id = personFinalId;
					p2cupdatePrepare.add(p2c);
				}
			}
		}
		p2cDao.updateBatch(p2cupdatePrepare);
		p2cDao.deleteBatch(p2cdeletePrepare);

		//step2, merge Profile
		
		List<PersonProfile> personProfileByPersonIds = ppDao.getPersonProfileByPersonIds(personIds);
		List<Integer> ppdeletePrepare = new ArrayList<Integer>();
		PersonProfile personProfile = ppDao.getPersonProfileById(personFinalId);
		for(PersonProfile pp : personProfileByPersonIds) {
			if(pp.id != personFinalId) {
				ppdeletePrepare.add(pp.id);
				if(!pp.position.equals(personProfile.position)) {
					personProfile.position += " " + pp.position;
				} 
				
				if(!pp.location.equals(personProfile.location)) {
					personProfile.location += "||" + pp.location;
				} 
				
				if(!pp.phone.equals(personProfile.phone)) {
					personProfile.phone += ", " + pp.phone;
				} 

				if(!pp.email.equals(personProfile.email)) {
					personProfile.email += ", " + pp.email;
				}
				if(!pp.homepage.equals(personProfile.homepage)) {
					personProfile.homepage += ", " + pp.homepage;
				}
			}
		}		
				
		List<PersonInfo> personInfoByPersonIds = piDao.getPersonInfoByPersonIds(personIds);
		List<Integer> pideletePrepare = new ArrayList<Integer>();
		PersonInfo personInfo = piDao.getPersonInfoById(personFinalId);
		for(PersonInfo pi : personInfoByPersonIds) {
			if(pi.id != personFinalId) {
				pideletePrepare.add(pi.id);
				if(!pi.education.equals(personInfo.education)) {
					personInfo.education += "||" + pi.education;
				}
				if(!pi.projects.equals(personInfo.projects)) {
					personInfo.projects += "||" + pi.projects;
				}
				if(!pi.resume.equals(personInfo.resume)) {
					personInfo.resume += "||" + pi.resume;
				}
				if(!pi.honor.equals(personInfo.honor)) {
					personInfo.honor += "||" + pi.honor;
				}
				if(!pi.parttime.equals(personInfo.parttime)) {
					personInfo.parttime += "||" + pi.parttime;
				}
				if(!pi.fields.equals(personInfo.fields)) {
					personInfo.fields += "||" + pi.fields;
				}
				if(!pi.summary.equals(personInfo.summary)) {
					personInfo.summary += "||" + pi.summary;
				}
				if(!pi.achievements.equals(personInfo.achievements)) {
					personInfo.achievements += "||" + pi.achievements;
				}
				if(!pi.experience.equals(personInfo.experience)) {
					personInfo.experience += "||" + pi.experience;
				}
			}
		}
		
		List<PersonExt> personExtByPersonIds = peDao.getPersonExtsByIdList(personIds);
		List<Integer> pedeletePrepare = new ArrayList<Integer>();
		PersonExt personExt = peDao.getPersonExtById(personFinalId);
		for(PersonExt pe : personExtByPersonIds) {
			if(pe.id != personFinalId) {
				pedeletePrepare.add(pe.id);
				if(!pe.title.equals(personExt.title)) {
					personExt.title += " " + pe.title;
				} 
			}
		}		
		
		ppDao.updatePersonProfile(personProfile.id, personProfile.position, personProfile.location, personProfile.phone, personProfile.email, personProfile.homepage, personProfile.author_id, personProfile);
		piDao.updatePersonInfo(personInfo.id, personInfo.education, personInfo.projects, personInfo.resume, personInfo.honor, personInfo.parttime, personInfo.fields, personInfo.summary, personInfo.achievements, personInfo.experience, personInfo);
		if(null!=personExt)
			peDao.updatePersonExt(personExt.id, "title", personExt.title, personExt);
		ppDao.deleteBatch(ppdeletePrepare);
		piDao.deleteBatch(pideletePrepare);
		peDao.deleteBatch(pedeletePrepare);
		pDao.deleteBatch(ppdeletePrepare);
		
		for(Integer personId : personIds) {
			personCacheService.removePerson(personId);
		}
	}
	
	@Inject 
	PersonExtDao peDao;
	
	@Inject 
	PersonCacheService personCacheService;
	
	@Inject
	PersonInfoDao piDao;
	
	@Inject
	PersonDao pDao;
	
	@Inject
	PersonProfileDao ppDao;
	
	@Inject
	Person2PublicationDao p2pDao;
	
	@Inject
	Person2GraduatePublicationDao p2gDao;
	
	@Inject
	Person2CourseDao p2cDao;
	
	@Inject
	Person2InstituteDao p2iDao;
	
	@Inject
	PersonRelationDao relationDao;
	
	public static void main(String args[]) {
		MergePersonService mps = T5RegistryHelper.getService(MergePersonService.class);
		List<Integer> personIds = new ArrayList<Integer>();
		personIds.add(2301);
		personIds.add(2336);
		mps.mergePersons(personIds);
	}

}
