package SchoolSearch.services.utils.dataUpdateTools;

/**
 * @author CX
 */
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.NameGeneration;

import SchoolSearch.services.utils.dataUpdateTools.basic.manipulate.General;
import SchoolSearch.services.utils.dataUpdateTools.basic.read.ReadFromCSV;
import SchoolSearch.services.utils.dataUpdateTools.basic.read.ReadFromDataBase;
import SchoolSearch.services.utils.dataUpdateTools.basic.validate.AntiReduplicationCheck;
import SchoolSearch.services.utils.dataUpdateTools.basic.validate.StructureInfoCheck;
import SchoolSearch.services.utils.dataUpdateTools.model.ColumnStructure;
import SchoolSearch.services.utils.dataUpdateTools.model.MergedGroup;
import SchoolSearch.services.utils.dataUpdateTools.utils.DepartmentMap;
import SchoolSearch.services.utils.dataUpdateTools.utils.MergeCreator;

public class CSVPublicationImportTool {
	private static CSVPublicationImportTool instance;
	
	//数据库表的field链表
	private List<String> keyList; 
	
	//数据库表的field字段
	private Map<String, ColumnStructure> key2Index;
	
	//将CSV数据转换为中间Java数据结构
	private List<List<Object>> dataCache;
	
	//待合并的字段数组
	private List<MergedGroup> mgList;
	
	//待删除的字段数组
	private List<String> removeKeyList;
	
	private String filePathStr;
	private String dbName;
	private String tableName;
	
	public static CSVPublicationImportTool getInstance(String dbName, String tableName, String filePathStr) {
		if (null == instance) {
			instance = new CSVPublicationImportTool(dbName, tableName, filePathStr);
		}
		return instance;
	}
	
	public static CSVPublicationImportTool getIndependentInstance(String dbName, String tableName, String filePathStr) {
		return new CSVPublicationImportTool(dbName, tableName, filePathStr);
	}
	
	public CSVPublicationImportTool(String dbName, String tableName, String filePathStr) {
		keyList = new ArrayList<String>();
		key2Index = new HashMap<String, ColumnStructure>();
		dataCache = new ArrayList<List<Object>>();
		
		mgList = new ArrayList<MergedGroup>();
		removeKeyList = new ArrayList<String>();
		
		this.dbName = dbName;
		this.tableName = tableName;
		this.filePathStr = filePathStr;
	}
	public void extractInfoFromCSV(){
		ReadFromCSV cs = ReadFromCSV.getIndependentInstance(filePathStr);
		cs.load();
		keyList = cs.getKeyList();
		key2Index = cs.getKey2Index();
		for(List<Object> objListI:cs.getDataCache()){
			List<Object> objList = new ArrayList<Object>();
			objList.addAll(objListI);
			this.dataCache.add(objList);
		}
	}
	
	//对keyList和key2Index的key字段进行RegExp修正，以符合数据库的字段名
	public void modification(){
		//更新keyList的key字段
		List<String> keyListNew = new ArrayList<String>();
		for(String key:keyList){
			key = key.replaceAll("[\\.\\[]", "_").toLowerCase();
			key = key.replaceAll("\\]", "");
			key = key.replaceAll("dc_", "");
			keyListNew.add(key);
		}
		
		keyList.clear();
		keyList.addAll(keyListNew);
		
		//更新key2Index的key字段
		Map<String,ColumnStructure> key2IndexNew = new HashMap<String, ColumnStructure>();
		for(String key:key2Index.keySet()){
			ColumnStructure ccs = new ColumnStructure();
			ccs.index = key2Index.get(key).index;
			ccs.type = key2Index.get(key).type;
			key = key.replaceAll("[\\.\\[]", "_").toLowerCase();
			key = key.replaceAll("\\]", "");
			key = key.replaceAll("dc_", "");
			key2IndexNew.put(key,ccs);
		}
		key2Index.clear();
		key2Index.putAll(key2IndexNew);
	}
	
	//生成合并规则,在此修改添加规则
	public void initMgList(){
		mgList.add(MergeCreator.getIndependentInstance(Arrays.asList("contributor_authorInstitution_en","contributor_authorInstitution_zh"), "author_affiliations").getMg());
		mgList.add(MergeCreator.getIndependentInstance(Arrays.asList("contributor_author_en","contributor_author_zh"), "authors").getMg());
		mgList.add(MergeCreator.getIndependentInstance(Arrays.asList("description_abstract_en","description_abstract_zh"), "abstract").getMg());
		mgList.add(MergeCreator.getIndependentInstance(Arrays.asList("description_sponsorship_en","description_sponsorship_zh"), "description_sponsorship").getMg());
		mgList.add(MergeCreator.getIndependentInstance(Arrays.asList("identifier_isbn_en","identifier_isbn_zh"), "identifier_isbn").getMg());
		mgList.add(MergeCreator.getIndependentInstance(Arrays.asList("identifier_issue_en","identifier_issue_zh"), "identifier_issue").getMg());
		mgList.add(MergeCreator.getIndependentInstance(Arrays.asList("identifier_page_en","identifier_page_zh"), "identifier_page").getMg());
		mgList.add(MergeCreator.getIndependentInstance(Arrays.asList("language_en","language_zh"), "language").getMg());
		mgList.add(MergeCreator.getIndependentInstance(Arrays.asList("publisher_en","publisher_zh"), "publisher").getMg());
		mgList.add(MergeCreator.getIndependentInstance(Arrays.asList("relation_conferencelocation_en","relation_conferencelocation_zh"), "relation_conferencelocation").getMg());
		mgList.add(MergeCreator.getIndependentInstance(Arrays.asList("relation_conferencename_en","relation_conferencename_zh"), "relation_conferencename").getMg());
		mgList.add(MergeCreator.getIndependentInstance(Arrays.asList("relation_ispartof_en","relation_ispartof_zh"), "jconf").getMg());
		mgList.add(MergeCreator.getIndependentInstance(Arrays.asList("subject_en","subject_zh"), "keywords").getMg());
		mgList.add(MergeCreator.getIndependentInstance(Arrays.asList("title_alternative_en","title_alternative_zh"), "title_alternative").getMg());
		mgList.add(MergeCreator.getIndependentInstance(Arrays.asList("title_en","title_zh"), "title").getMg());
		mgList.add(MergeCreator.getIndependentInstance(Arrays.asList("type_en","type_local_zh"), "type").getMg());
		mgList.add(MergeCreator.getIndependentInstance(Arrays.asList("identifier_uri",""), "url").getMg());
		mgList.add(MergeCreator.getIndependentInstance(Arrays.asList("date_issued_en","date_issued_zh"), "year").getMg());
//		mgList.add(MergeCreator.getIndependentInstance(Arrays.asList("",""), "").getMg());
//		mgList.add(MergeCreator.getIndependentInstance(Arrays.asList("",""), "").getMg());
//		mgList.add(MergeCreator.getIndependentInstance(Arrays.asList("",""), "").getMg());
//		mgList.add(MergeCreator.getIndependentInstance(Arrays.asList("",""), "").getMg());
	}
	
	
	public void startMerge(){
		General general = General.getIndependentInstance(keyList, key2Index, dataCache);
		general.MergeBatch(mgList);
		
		//将合并后的数据更新至keyList,key2Index和dataCache
		keyList.clear();
		keyList.addAll(general.getKeyList());
		
		key2Index.clear();
		key2Index.putAll(general.getKey2Index());
		
		dataCache.clear();
		for(List<Object> objListI:general.getDataCache()){
			List<Object> objList = new ArrayList<Object>();
			objList.addAll(objListI);
			dataCache.add(objList);
		}
	}
	
	//生成删除列表数组
	public void initRemoveKeyList(){
		ReadFromDataBase db  = ReadFromDataBase.getIndependentInstance(dbName, tableName);
		db.getWithIdLimited(null, 1, 1);
		for(String key:keyList){
			if(!db.getKeyList().contains(key)){
				removeKeyList.add(key);
			}
		}
	}
	
	public void startRemove(){
		General general = General.getIndependentInstance(keyList, key2Index, dataCache);
		general.removeColumnsByKeyList(removeKeyList);
		
		//将合并后的数据更新至keyList,key2Index和dataCache
		keyList.clear();
		keyList.addAll(general.getKeyList());
				
		key2Index.clear();
		key2Index.putAll(general.getKey2Index());
				
		dataCache.clear();
		for(List<Object> objListI:general.getDataCache()){
			List<Object> objList = new ArrayList<Object>();
			objList.addAll(objListI);
			dataCache.add(objList);
		}
	}
	
	public void padding(String keyName, String value){
		General general = General.getIndependentInstance(keyList, key2Index, dataCache);
		general.insertColumnAtTail(keyName, value);
		
		//将合并后的数据更新至keyList,key2Index和dataCache
		keyList.clear();
		keyList.addAll(general.getKeyList());
				
		key2Index.clear();
		key2Index.putAll(general.getKey2Index());
				
		dataCache.clear();
		for(List<Object> objListI:general.getDataCache()){
			List<Object> objList = new ArrayList<Object>();
			objList.addAll(objListI);
			dataCache.add(objList);
		}
	}
	
	public void modifiyNames(boolean isVerbose){
		General general = General.getIndependentInstance(keyList, key2Index, dataCache);
		if(general.extract("authors",isVerbose)){
			List<String> newName = new ArrayList<String>();
			List<String> tmpList = general.getStringList();
			int length = tmpList.size();
			for(int i=0; i<length; i++){
				String name = tmpList.get(i);
				if(null != name){
					StringBuilder sb = new StringBuilder();
					String[] names = name.trim().split("\\|\\|");
					for(int j=0; j<names.length; j++){
						if(names[j].contains(",")){
							String[] tmp = names[j].trim().split(",");
							if(2 == tmp.length){
								sb.append(tmp[1]);
								sb.append(" ");
								sb.append(tmp[0]);
							}else{
								sb.append(names[j].replaceAll(",", ""));
							}
						}else{
							sb.append(names[j]);
						}
						sb.append("||");
					}
					sb.delete(sb.length()-2, sb.length());
					if(isVerbose){
						System.out.println(name + "----->" + sb.toString());
					}
					newName.add(sb.toString());
				}else{
					newName.add(name);
				}
			}
			general = General.getIndependentInstance(keyList, key2Index, dataCache);
			general.replaceString("authors", newName);
		}
		keyList.clear();
		keyList.addAll(general.getKeyList());
				
		key2Index.clear();
		key2Index.putAll(general.getKey2Index());
				
		dataCache.clear();
		for(List<Object> objListI:general.getDataCache()){
			List<Object> objList = new ArrayList<Object>();
			objList.addAll(objListI);
			dataCache.add(objList);
		}
	}
	public Map<String, ColumnStructure> getKey2Index() {
		return key2Index;
	}
	
	public List<List<Object>> getDataCache() {
		return dataCache;
	}
	
	public List<String> getKeyList() {
		return keyList;
	}
	
	public void printStructure() {
		if (null != keyList && null != key2Index) {
			System.out.println("------------------Structure--------------");
			for (String key : keyList) {
				System.out.print(key + " ");
				System.out.print(key2Index.get(key).index + " ");
				System.out.print(key2Index.get(key).type + " ");
				if (null != dataCache && !dataCache.isEmpty()){
					int index=0;
					Object o=null;
					while(true){
						o = dataCache.get(index).get(keyList.indexOf(key));
						if(null!=o){
							break;
						}
						index++;
					}
					if(Integer.class.isInstance(o)){
						System.out.print("Integer ");
					}else if(String.class.isInstance(o)){
						System.out.print("String ");
					}else{
						System.out.print("Unknow Type!");
					}
				}
				System.out.println();
			}
		} else {
			System.out.println("[PrintInfo]KeyList IS EMPTY!");
		}
	}

	public void printdataCache() {
		if (null != dataCache && !dataCache.isEmpty()) {
			System.out.println("------------------dataCache--------------");
			for (List<Object> objList : dataCache) {
				for (Object str : objList) {
					System.out.print(str);
					System.out.print("   ");
				}
				System.out.println();
			}
		} else {
			System.out.println("[PrintInfo]dataCache IS EMPTY!");
		}
	}
	
	public static String root = "D:\\Users\\jingyuanliu\\NewPub";
	
	public static void main(String[] args) throws Exception {
		File fileDir = new File(root);
		if(fileDir.isDirectory()){
			File[] fileList = fileDir.listFiles();
			int count = 1;
			int size = fileList.length;
			String dbName = "jingyuantest";
			String tableName = "allpub_copy";
			AntiReduplicationCheck.prepareDB(dbName, tableName);
			AntiReduplicationCheck.setMatchKeyListToInitHashValue(Arrays.asList("title","relation_conferencename","authors","abstract"), false);
			AntiReduplicationCheck.setMergeKeyList(Arrays.asList("institute_key"), false);

			for(File f:fileList){
				CSVPublicationImportTool csvtool = CSVPublicationImportTool.getIndependentInstance(dbName, tableName, f.toString());
				System.out.println("-------------------LOADING FILE " + f.toString() + "(" + count + " of " + size + ")-------------------");
				//从CSV文件初始化keyList, key2Index和dataCache
				csvtool.extractInfoFromCSV();
				//对keyList和key2Index的key字段进行RegExp修正，以符合数据库的字段名
				csvtool.modification();
				//初始化待合并的数组mgList
				csvtool.initMgList();
				//启动合并
				csvtool.startMerge();
				//初始化待删除的key数组removeKeyList
				csvtool.initRemoveKeyList();
				//启动删除
				csvtool.startRemove();
				//添加institute_key字段
//				int tmp = f.toString().lastIndexOf("\\");
//				String value = f.toString().substring(tmp+1).replaceAll("journal.csv", "").replaceAll("conference.csv", "").trim().toLowerCase();
				
				String []words = f.toString().split("NewPub");
				String value = words[words.length-1].substring(1).replaceAll("journal.csv", "").replaceAll("journal2.csv", "").replaceAll("conference.csv", "").replaceAll("conference2.csv", "").trim().toLowerCase();
				csvtool.padding("institute_key", value);
				csvtool.modifiyNames(false);
				//表结构转换
				StructureInfoCheck sc = StructureInfoCheck.getIndependentInstance(dbName, tableName, csvtool.getKeyList(), csvtool.getKey2Index(), csvtool.getDataCache());
				//如果转换成功
				if(sc.checkAndTransfer(false)){
					//去重
					AntiReduplicationCheck ac = AntiReduplicationCheck.getIndependentInstance(sc.getKeyList(), sc.getKey2Index(), sc.getDataCache());
					ac.checkBatch();
					System.out.println("------------------PROCESS FILE " + f.toString() + "(" + count++ + " of " + size + ") SUCCESSFULLY-------------------");
				}else{
					count++;
					System.out.println("[ErrorInfo]TRANSFER FAILED!");
				}
			}
			//填充id后写入数据库，同时保留一份在内存供后续操作
			AntiReduplicationCheck.constructInsertObjList();
			AntiReduplicationCheck.constructUpdateObjList();
			AntiReduplicationCheck.fillInsertObjectListId(false);
			AntiReduplicationCheck.refreshDBCache();
			AntiReduplicationCheck.insertWithoutConstruct(true);
			AntiReduplicationCheck.updateWithoutConstruct("id");
			
//			//取person表数据
//			General glperson = General.getIndependentInstance("cache", "person");
//			//glperson.shrinkRows(true);
//			
//			//建立index2Alias和alias2Index两张Map
//			Map<Integer, List<String>> index2Alias = new HashMap<Integer, List<String>>();
//			Map<String, List<Integer>> alias2Index = new HashMap<String, List<Integer>>();
//			Map<Integer,String> index2department = new HashMap<Integer, String>();
//			
//			
//			List<Integer> idList = glperson.fork("id", true).getIntegerList();
//			List<String> namelist = glperson.fork("name", true).getStringList();
//			List<String> aliasList = glperson.fork("name_alias",true).getStringList();
//			Integer Ssize = idList.size();
//			for(int i=0;i<Ssize;i++){
//				//更新index2Alias
//				if(!index2Alias.containsKey(idList.get(i))){
//					index2Alias.put(idList.get(i), new ArrayList<String>());
//					index2Alias.get(idList.get(i)).add(namelist.get(i).trim());
//				}
//				if(null !=aliasList.get(i) && !aliasList.get(i).equalsIgnoreCase("")){
//					String[]aliasTmp = aliasList.get(i).trim().split("\\|\\|");
//					for(String tmp:aliasTmp){
//						index2Alias.get(idList.get(i)).add(tmp.trim());
//					}
//				}
//				
//				//更新alias2Index
//				List<Object> alias = new ArrayList<Object>();
//				//先把自己的名字加入alias2Index
//				if(!alias2Index.containsKey(namelist.get(i).trim())){
//					alias2Index.put(namelist.get(i).trim(), new ArrayList<Integer>());
//				}
//				alias2Index.get(namelist.get(i).trim()).add(idList.get(i));
//				//再把别名加入alias2Index
//				if(namelist.get(i).matches("[^a-zA-Z]*")){
//					alias = NameGeneration.generateEnglishNamesObject(namelist.get(i));
//					for(Object o:alias){
//						if(!alias2Index.containsKey(((String)o).trim())){
//							alias2Index.put(((String)o).trim(), new ArrayList<Integer>());
//						}
//						alias2Index.get(((String)o).trim()).add(idList.get(i));
//					}
//				}
//			}
//			
//			//生成index2departmentMap
//			General glpersonProfile = General.getIndependentInstance("cache", "person_profile");
//			List<Integer> iddList = glpersonProfile.fork("id", true).getIntegerList();
//			List<String> departmentList = glpersonProfile.fork("department_key",true).getStringList();
//			for(int i=0;i<iddList.size();i++){
//				index2department.put(iddList.get(i), departmentList.get(i));
//			}
//			
//			//记录新产生的person2Publication条目
//			List<List<Object>> newRecords = new ArrayList<List<Object>>();
//			//准备author数据
//			General glpub = General.getIndependentInstance(AntiReduplicationCheck.getKeyListDB(), AntiReduplicationCheck.getKey2IndexDB(), AntiReduplicationCheck.getDBCache());
//			glpub.shrinkRows("authors", true);
//			List<Integer> pubidList = glpub.fork("id", true).getIntegerList();
//			List<String> authorsList = glpub.fork("authors",true).getStringList();
//			List<String> abbrev = glpub.fork("institute_key",true).getStringList();
//			Ssize = pubidList.size();
//			for(int i=0;i<Ssize;i++){
//				String[] authors = authorsList.get(i).split("\\|\\|");
//				for(int j=0;j<authors.length;j++){
//					String author = authors[j];
//					//如果最大的总集合包含新来的author，进一步判断
//					if(alias2Index.containsKey(author.trim())){
//						//对每一个可能包含新来author的记录
//						for(Integer index:alias2Index.get(author.trim())){
//							boolean isSameDep = false;
//							String currentDepartment = index2department.get(index);
//							List<Object> tmp = DepartmentMap.getDepartment(abbrev.get(index).trim().toLowerCase());
//							if(tmp!=null){
//								for(Object o:tmp){
//									if(((String)o).substring(0,2).equalsIgnoreCase(currentDepartment.substring(0,2))){
//										isSameDep = true;
//										//System.out.println(author+":"+o + "----"+ currentDepartment);
//										break;
//									}
//								}
//							}else{
//								System.out.println("Find Nothing!"+abbrev.get(index).trim().toLowerCase());
//							}
//							if(isSameDep){
//								//无论如何都要新增条目
//								List<Object> objList = new ArrayList<Object>();
//								objList.add(index);
//								objList.add(pubidList.get(i));
//								objList.add(j+1);
//								//增加一条记录
//								newRecords.add(objList);
//								
//								boolean isExist = false;
//								for(String existName:index2Alias.get(index)){
//									if(existName.trim().equalsIgnoreCase(author.trim())){
//										isExist = true;
//										break;
//									}
//								}
//								
//								//如果不存在则需要添加
//								if(!isExist){
//									index2Alias.get(index).add(author);
//								}
//							}else{
//								System.out.println("IS not the same department:" +author+":"+currentDepartment +"-----"+tmp.get(0));
//							}
//						}
//					}
//				}
//			}
//			
//			//更新内存中的person表
//			Ssize = idList.size();
//			for(int i=0;i<Ssize;i++){
//				List<String> alias = index2Alias.get(idList.get(i));			
//				if(alias.size()>1){
//					StringBuilder sb = new StringBuilder();
//					for(int j=1;j<alias.size();j++){
//						sb.append(alias.get(j).trim());
//						sb.append("||");
//					}
//					sb.delete(sb.length()-2, sb.length());
//					glperson.changeValue("id", idList.get(i), "name_alias", sb.toString().trim());
//				}
//			}
//			//将内存中的person表更新至数据库中
//			AntiReduplicationCheck.prepareDB("cache", "person");
//			AntiReduplicationCheck.setMatchKeyListToInitHashValue("id", true);
//			AntiReduplicationCheck.setReplaceKeyList("name_alias");
//			AntiReduplicationCheck ac = AntiReduplicationCheck.getIndependentInstance(glperson.getKeyList(), glperson.getKey2Index(), glperson.getDataCache());
//			ac.checkBatch();
//			AntiReduplicationCheck.updateWithConstruct("id");
//			
//			//将newRecord封装成person2publication表的形式更新至数据库
//			Map<String ,ColumnStructure> key2Index = new HashMap<String, ColumnStructure>();
//			key2Index.put("person_id", General.columnStructureCreator(0, "int"));
//			key2Index.put("publication_id", General.columnStructureCreator(1, "int"));
//			key2Index.put("position", General.columnStructureCreator(2, "int"));
//			General gl2 = General.getIndependentInstance(Arrays.asList("person_id","publication_id","position"), key2Index, newRecords);
//			AntiReduplicationCheck.prepareDB("cache", "person2publication");
//			AntiReduplicationCheck.setMatchKeyListToInitHashValue(Arrays.asList("person_id","publication_id","position"), true);
//			ac = new AntiReduplicationCheck(gl2.getKeyList(), gl2.getKey2Index(), gl2.getDataCache());
//			ac.checkBatch();
//			AntiReduplicationCheck.insertWithConstruct(false);
		}
	}
}
