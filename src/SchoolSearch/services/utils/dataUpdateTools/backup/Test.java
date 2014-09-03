package SchoolSearch.services.utils.dataUpdateTools.backup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.NameGeneration;

import SchoolSearch.services.dao.oranizationLevels.model.Institute;
import SchoolSearch.services.dao.publication.model.Publication;
import SchoolSearch.services.utils.dataUpdateTools.basic.manipulate.General;
import SchoolSearch.services.utils.dataUpdateTools.basic.read.ReadFromCSV;
import SchoolSearch.services.utils.dataUpdateTools.basic.read.ReadFromDataBase;
import SchoolSearch.services.utils.dataUpdateTools.basic.validate.StructureInfoCheck;
import SchoolSearch.services.utils.dataUpdateTools.basic.validate.AntiReduplicationCheck;
import SchoolSearch.services.utils.dataUpdateTools.basic.write.UpdateToDataBase;
import SchoolSearch.services.utils.dataUpdateTools.basic.write.InsertIntoDataBase;
import SchoolSearch.services.utils.dataUpdateTools.model.ColumnStructure;
import SchoolSearch.services.utils.dataUpdateTools.model.MergedGroup;

public class Test {
public static void main(String[] args) {
	List<Object> nameList = NameGeneration.generateEnglishNamesObject("李俊明");
	for(Object obj:nameList){
		System.out.print(obj+"||");
	}
	//StructureInfoCheck工具测试
//	ReadFromDataBase rd = ReadFromDataBase.getIndependentInstance("testpublication", "faculty_tbl_new");
//	rd.getWithIdLimited(null, 1, 20);
//	rd.printStructure();
//	StructureInfoCheck ib= StructureInfoCheck.getIndependentInstance("tsinghuaprofessordata", "faculty_tbl", rd.getKeyList(), rd.getKey2Index(), rd.getDataCache());
//	ib.printStructure();
//	ib.checkAndTransfer(null);
//	ib.printStructure();
	
	
	
//	ReadFromDataBase rd2 = ReadFromDataBase.getIndependentInstance("testpublication", "faculty_tbl_old");
//	rd2.getWithIdLimited(null, 1, 20);
//	rd2.printStructure();
//	rd.printdataCache();
	
//	ReadFromDataBase rd2 = ReadFromDataBase.getIndependentInstance("test", "faculty_tbl_old");
//	rd2.getWithIdLimited(null, 1,-1);
//
//	Validation vd = Validation.getIndependentInstance(keyList, key2IndexDB, DBCache, key2IndexData, matchKeyList)
//	ib.printStructure();
//	ib.insertBatchRecord();
//	ib.printAll();
//	ib.insertBatchRecord();
	
//	Institute ins = new Institute();
//	ins.id = 1;
//	ins.institute_name = "tongxingongcheng";
//	InsertIntoDataBase iidb = new InsertIntoDataBase("cache", "institute", ins);
//	iidb.printStructure();
//	iidb.printdataCache();
//	iidb.insertBatchRecord();
//	ReadFromDataBase rdb = ReadFromDataBase.getIndependentInstance("cache", "institute");
//	List<String> intList= rdb.getSelectedColumn("institute_name", 2, 5, String.class);
//	for(String id:intList){
//		System.out.println(id);
//	}
////	rdb.printCount();
//	rdb.printStructure();
//	rdb.printdataCache();
//	ReadFromDataBase rd =  new ReadFromDataBase("schoolsearch", "publication");
//	rd.getAllData(1,10);
//	General general = General.getIndependentInstance(rd.getKeyList(), rd.getKey2Index(), rd.getDataCache());
//	if(general.extract("authors",true)){
//		List<String> newName = new ArrayList<String>();
//		List<String> tmpList = general.getStringList();
//		int length = tmpList.size();
//		for(int i=0; i<length; i++){
//			String name = tmpList.get(i);
//			if(null != name){
//				StringBuilder sb = new StringBuilder();
//				String[] names = name.trim().split("\\|\\|");
//				for(int j=0; j<names.length; j++){
//					if(names[j].contains(",")){
//						String[] tmp = names[j].trim().split(",");
//						sb.append(tmp[1]);
//						sb.append(" ");
//						sb.append(tmp[0]);
//					}else{
//						sb.append(names[j]);
//					}
//					sb.append("||");
//				}
//				sb.delete(sb.length()-2, sb.length());
//				System.out.println(name + "----->" + sb.toString());
//				newName.add(sb.toString());
//			}
//		}
//		
//	}
	
}
}
