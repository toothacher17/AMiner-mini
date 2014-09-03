package NAanalysis;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import SchoolSearch.services.dao.schooltest.dao.SchooltestPublicationDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPublicationDetailDAO;
import SchoolSearch.services.dao.schooltest.model.SchooltestPublicationDetail;

public class PubicationAnalysis {

	public static String rootpath = "D:\\Users\\jingyuanliu\\na\\new\\";
	
	
	public static void main(String[] args) {
		SchooltestPublicationDetailDAO pubdetaildao = SchooltestPublicationDetailDAO.getInstance();
		List<SchooltestPublicationDetail> all = pubdetaildao.walkAll();
		Set<String> differentInstitute = new HashSet<String>();
		for(SchooltestPublicationDetail pubdetail : all) {
			differentInstitute.add(pubdetail.getInstituteKey());
		}
				
		try{
			PrintWriter out = new PrintWriter(new File(rootpath,"instituteKey.txt"));
			for(String everyresult : differentInstitute) {
				out.println(everyresult);
				System.out.println(everyresult);
			}
			System.out.println(differentInstitute.size());
			out.close();
			
			
		}catch (IOException e) {
			e.printStackTrace();
		
		}
		
		
		
		
		
	}
}
