package SchoolSearch.services.utils.dataUpdateTools.backup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import SchoolSearch.services.utils.dataUpdateTools.basic.manipulate.General;
import SchoolSearch.services.utils.dataUpdateTools.basic.read.ReadFromDataBase;
import SchoolSearch.services.utils.dataUpdateTools.utils.IOUtils;

public class outputData {
 public static void main(String[] args) {
//	get graduatePublication
//	 ReadFromDataBase rd1 =ReadFromDataBase.getIndependentInstance("schoolSearch2", "graduatepublication");
//	rd1.getAllData();
//	General gl1=General.getIndependentInstance(rd1.getKeyList(), rd1.getKey2Index(), rd1.getDataCache());
//	gl1.extract(Arrays.asList("id","abstract_cn","keyword_cn","title_cn"));
//	List<List<Object>> dataCache1 = gl1.getDataCache();
//	for(List<Object> objList:dataCache1){
//		String tmp = (String)objList.get(1);
//		objList.set(1,tmp.replaceAll("[\\r\\n]", ""));
//	}
//	for(List<Object> objList:dataCache1){
//		String str1=String.valueOf(objList.get(0));
//		String str2=(String)objList.get(3);
//		String str3=(String)objList.get(1);
//		String str4=(String)objList.get(2);
//		StringBuilder sb = new StringBuilder();
//		sb.append(str1);
//		sb.append("\t");
//		sb.append(str2);
//		sb.append("\t");
//		sb.append(str3);
//		sb.append(",");
//		sb.append(str4);
//		IOUtils.appendToFile("D:/Users/jingyuanliu/data/grapub.txt", sb.toString());
//	}
	
	
//	get course description
//	ReadFromDataBase rd2 =ReadFromDataBase.getIndependentInstance("schoolSearch2", "course");
//	rd2.getAllData();
//	General gl2=General.getIndependentInstance(rd2.getKeyList(), rd2.getKey2Index(), rd2.getDataCache());
//	gl2.extract(Arrays.asList("id","courseName","courseDescription"));
//	List<List<Object>> dataCache2 = gl2.getDataCache();
//	for(List<Object> objList:dataCache2){
//		String tmp = (String)objList.get(2);
//		if(null!=tmp){
//			objList.set(2,tmp.replaceAll("[\\r\\n]", ""));
//		}
//	}
//	for(List<Object> objList:dataCache2){
//		String str1=String.valueOf(objList.get(0));
//		String str2=(String)objList.get(1);
//		String str3=(String)objList.get(2);
//		if(null!= str3&&!(str3.equals("null"))){
//			StringBuilder sb = new StringBuilder();
//			sb.append(str1);
//			sb.append("\t");
//			sb.append(str2);
//			sb.append("\t");
//			sb.append(str3);
//			IOUtils.appendToFile("D:/Users/jingyuanliu/data/course.txt", sb.toString());
//		}
//	}
	
//	get person_info 
	ReadFromDataBase rd3 =ReadFromDataBase.getIndependentInstance("schoolSearch2", "person_info");
	rd3.getAllData();
	General gl3=General.getIndependentInstance(rd3.getKeyList(), rd3.getKey2Index(), rd3.getDataCache());
	gl3.extract(Arrays.asList("id","fields"));
	List<List<Object>> dataCache3 = gl3.getDataCache();
	for(List<Object> objList:dataCache3){
		String tmp = (String)objList.get(1);
			objList.set(1,tmp.replaceAll("[\\r\\n]|\\s", ""));
	}
	for(List<Object> objList:dataCache3){
		String str1=String.valueOf(objList.get(0));
		String str2=(String)objList.get(1);
		if(null!= str2 && !str2.equalsIgnoreCase("")){
			StringBuilder sb = new StringBuilder();
			sb.append(str1);
			sb.append("\t");
			sb.append(str2);
			IOUtils.appendToFile("D:/Users/jingyuanliu/data/person_info4.txt", sb.toString());
		}
		
	}
	
//	get person interest
//	ReadFromDataBase rd4 =ReadFromDataBase.getIndependentInstance("schoolSearch2", "person_interest");
//	rd4.getAllData();
//	General gl4=General.getIndependentInstance(rd4.getKeyList(), rd4.getKey2Index(), rd4.getDataCache());
//	gl4.extract(Arrays.asList("id","interest"));
//	List<List<Object>> dataCache4 = gl4.getDataCache();
//	for(List<Object> objList:dataCache4){
//		String str1=String.valueOf(objList.get(0));
//		String str2=(String)objList.get(1);
//		if(null!= str2&&!(str2.equals("null"))){
//			StringBuilder sb = new StringBuilder();
//			sb.append(str1);
//			sb.append("\t");
//			sb.append(str2);
//			IOUtils.appendToFile("D:/Users/jingyuanliu/data/person_interest.txt", sb.toString());
//		}
//		
//	}
	
//	get publication
//	 ReadFromDataBase rd5 =ReadFromDataBase.getIndependentInstance("schoolSearch2", "publication");
//		rd5.getAllData();
//		General gl5=General.getIndependentInstance(rd5.getKeyList(), rd5.getKey2Index(), rd5.getDataCache());
//		gl5.extract(Arrays.asList("id","abstract","keywords","title"));
//		List<List<Object>> dataCache1 = gl5.getDataCache();
//		for(List<Object> objList:dataCache1){
//			String tmp = (String)objList.get(1);
//			objList.set(1,tmp.replaceAll("[\\r\\n]", ""));
//		}
//		for(List<Object> objList:dataCache1){
//			String str1=String.valueOf(objList.get(0));
//			String str2=(String)objList.get(3);
//			String str3=(String)objList.get(1);
//			String str4=(String)objList.get(2);
//			if(null!=str2&&null!=str3){
//				StringBuilder sb = new StringBuilder();
//				sb.append(str1);
//				sb.append("\t");
//				sb.append(str3);
//				sb.append("\t");
//				sb.append(str2);
//				sb.append(",");
//				sb.append(str4);
//				IOUtils.appendToFile("D:/Users/jingyuanliu/data/pub.txt", sb.toString());
//			}
//		}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
}
