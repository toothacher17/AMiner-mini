package transfer;

import java.util.*;
import java.io.*;

import SchoolSearch.services.dao.schooltest.dao.SchooltestPersonDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPersonInfoDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPersonProfileDAO;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonInfo;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonProfile;

public class TransferData {

	public static void main(String[] args) {
		SchooltestPersonDAO personDAO = SchooltestPersonDAO.getInstance();
		SchooltestPersonProfileDAO personProfileDAO = SchooltestPersonProfileDAO.getInstance();
		SchooltestPersonInfoDAO personInfoDAO = SchooltestPersonInfoDAO.getInstance();
		List<Integer> allPersonId = new ArrayList<Integer>();
		for(SchooltestPerson p : personDAO.walkAll()) {
			allPersonId.add(p.getId());
		}
		System.out.println("all person id size is " + allPersonId.size());
		
		List<Integer> allProfileId  = new ArrayList<Integer>();
		for(SchooltestPersonProfile pp : personProfileDAO.walkAll()) {
			allProfileId.add(pp.getId());
		} 
		System.out.println("all person profile id size is " + allProfileId.size());
		
		List<Integer> allInfoId  = new ArrayList<Integer>();
		for(SchooltestPersonInfo pi : personInfoDAO.walkAll()) {
			allInfoId.add(pi.getId());
		} 
		System.out.println("all person info id size is " + allInfoId.size());
		
		List<SchooltestPersonProfile> otherProfile = new ArrayList<SchooltestPersonProfile>();
		List<SchooltestPersonInfo> otherInfo = new ArrayList<SchooltestPersonInfo>();
		
		for(Integer pid : allPersonId) {
			if(!allProfileId.contains(pid)) {
				SchooltestPersonProfile temp1 = new SchooltestPersonProfile();
				temp1.setId(pid);
				otherProfile.add(temp1);
			}
			if(!allInfoId.contains(pid)) {
				SchooltestPersonInfo temp2 = new SchooltestPersonInfo();
				temp2.setId(pid);
				otherInfo.add(temp2);
			}
		}
		personProfileDAO.insertMultiple(otherProfile);
		personInfoDAO.insertMultiple(otherInfo);
	}
	
	
	
	
	
	
	
	
	
	
	
}
