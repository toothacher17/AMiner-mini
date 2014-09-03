package SchoolSearch.services.lucene;

import java.io.*;
import java.util.*;

import SchoolSearch.services.dao.schooltest.dao.SchooltestPersonInfoDAO;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonInfo;

public class InsertPersoninfoScore {

	public static void main(String[] args) {
		String rootpath = "D:\\Users\\jingyuanliu\\aminiNA";
		List<SchooltestPersonInfo> all = SchooltestPersonInfoDAO.getInstance().walkAll();
		Map<Integer, Double> result = new HashMap<Integer, Double>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(rootpath + "\\rerankresult.txt"));
			String readline;
			while((readline = in.readLine())!= null) {
				String [] words = readline.split("\t");
				result.put(Integer.parseInt(words[0]), Double.parseDouble(words[1]));
				System.out.println(Integer.parseInt(words[0]) + "\t" + Double.parseDouble(words[1]));
			}
			in.close();
			System.out.println(" the result size is "+ result.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(SchooltestPersonInfo pi : all) {
			pi.setScore(result.get(pi.getId()));
			System.out.println("---------------------------------");
			System.out.println(pi.getId() + "\t" + result.get(pi.getId()));
			System.out.println("---------------------------------");
		}
		
		SchooltestPersonInfoDAO.getInstance().updateBatch(all);
	}
	
	
	
	
	
}
