package SchoolSearch.services.utils.dataUpdateTools;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import SchoolSearch.services.utils.dataUpdateTools.basic.manipulate.General;
import SchoolSearch.services.utils.dataUpdateTools.basic.read.ReadFromCSV;
import SchoolSearch.services.utils.dataUpdateTools.basic.read.ReadFromDataBase;
import SchoolSearch.services.utils.dataUpdateTools.basic.write.InsertIntoDataBase;
import SchoolSearch.services.utils.dataUpdateTools.utils.IOUtils;
import SchoolSearch.services.utils.dataUpdateTools.utils.NLPUtils;

public class Temp {
	public static void main(String[] args) {
//		CSVPublicationImportTool csvtool  = CSVPublicationImportTool.getInstance("cache", "allpeople", "C:/data/tt.csv");
//		csvtool.extractInfoFromCSV();
//		InsertIntoDataBase rd = InsertIntoDataBase.getIndependentInstance("cache", "allpeople", csvtool.getKeyList(), csvtool.getKey2Index(), csvtool.getDataCache());
//		rd.insertBatchRecord();
//	}
		
		String sourcePath = "Z:/Tencent_Retweet_Data/20111001/";
		String targetPath = "z:/xichen/retweet.txt";
		File ff = new File(sourcePath);
		int count=1;
		IOUtils.split(ff.listFiles()[0].toString(), targetPath, 3, null);
		Scanner sc = new Scanner(System.in);
		sc.next();
		IOUtils.split(ff.listFiles()[1].toString(), targetPath, 3, null);
//		for(File f:ff.listFiles()){
//			System.out.println("process"+f.toString()+"("+(count++)+"of"+ff.listFiles().length+")");
//			IOUtils.split(f.toString(), targetPath, 3, null);
//		}
		
		
//		Set<String> departments = new HashSet<String>();
//		General gl = General.getIndependentInstance("cache", "department");
//		departments.addAll(gl.extractString("department_name"));
//		gl = General.getIndependentInstance("cache", "institute");
//		departments.addAll(gl.extractString("institute_name"));
//		ReadFromDataBase rd = ReadFromDataBase.getIndependentInstance("cache", "allpeople");
//		List<String> lst =rd.getDistinctStringList("department");
//		int count=0;
//		for(String key:lst){
//			boolean isMatch = false;
//			for(String ref:departments){
//				if(NLPUtils.isSubSequence(ref.trim(),key.trim())){
////					System.out.println(key.trim()+"-->"+ref.trim());
//					isMatch = true;
//					break;
//				}
//			}
//			if(!isMatch){
//				count++;
//				if(key.trim().endsWith("院")){
//					System.out.println(key.trim());
//				}
//			}
//		}
//		System.out.println(count);
	}
}
