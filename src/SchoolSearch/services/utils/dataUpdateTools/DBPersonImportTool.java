package SchoolSearch.services.utils.dataUpdateTools;
/**
 * @author CX
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.NameGeneration;

import SchoolSearch.services.utils.dataUpdateTools.basic.manipulate.General;
import SchoolSearch.services.utils.dataUpdateTools.basic.read.ReadFromDataBase;
import SchoolSearch.services.utils.dataUpdateTools.basic.validate.AntiReduplicationCheck;
import SchoolSearch.services.utils.dataUpdateTools.basic.validate.StructureInfoCheck;
import SchoolSearch.services.utils.dataUpdateTools.model.ColumnStructure;
import SchoolSearch.services.utils.dataUpdateTools.model.MergedGroup;
import SchoolSearch.services.utils.dataUpdateTools.utils.AvatarTransferTool;
import SchoolSearch.services.utils.dataUpdateTools.utils.DepartmentMap;
import SchoolSearch.services.utils.dataUpdateTools.utils.MergeCreator;
import SchoolSearch.services.utils.dataUpdateTools.utils.NameCleaner;
import SchoolSearch.services.utils.dataUpdateTools.utils.TitleGenerateTool;

public class DBPersonImportTool {
	
	private String dbName = "cache";
	private List<String> keyList;
	private Map<String, ColumnStructure> key2Index;
	private List<List<Object>> dataCache;
	private List<List<Object>> insertDataCache;
	public DBPersonImportTool() {
		this.keyList = new ArrayList<String>();
		this.key2Index = new HashMap<String, ColumnStructure>();
		this.dataCache = new ArrayList<List<Object>>();
		this.insertDataCache = new ArrayList<List<Object>>();
	}
	public DBPersonImportTool(String dbName) {
		this.dbName = dbName;
		this.keyList = new ArrayList<String>();
		this.key2Index = new HashMap<String, ColumnStructure>();
		this.dataCache = new ArrayList<List<Object>>();
		this.insertDataCache = new ArrayList<List<Object>>();
	}
	//将新来的数据以faculty的表结构导入faculty表中
	public void newDataTransfer(String dbNameSource, String sourceTableName){
		System.out.println("1.将faculty的instituteName更新至Institute表");
		//1.将faculty的instituteName更新至Institute表
		//利用SQL语句只选出Distinct的institute字段
		ReadFromDataBase rd = ReadFromDataBase.getIndependentInstance(dbNameSource, sourceTableName);
		rd.getDistinctList("institute");
		General gl = General.getIndependentInstance(rd.getKeyList(), rd.getKey2Index(), rd.getDataCache());
		//将null或者空串剔除
		gl.shrinkRows("institute", true);
		gl.changeKey("institute", "institute_name");
		AntiReduplicationCheck.prepareDB(dbName, "institute");
		AntiReduplicationCheck.setMatchKeyListToInitHashValue("institute_name", true);
		AntiReduplicationCheck ac = new AntiReduplicationCheck(gl.getKeyList(), gl.getKey2Index(), gl.getDataCache());
		ac.checkBatch();
		AntiReduplicationCheck.insertWithConstruct(false);
		System.out.println("------------------------------------------------------------------------------------------------------------------");
		System.out.println("2.利用Department表将Faculty表institute字段中为null的值用departmentName填充");
		rd = ReadFromDataBase.getIndependentInstance(dbNameSource, sourceTableName);
		rd.getAllData();
		//读入department表，作为参照表
		General gldep = General.getIndependentInstance(dbName,"department");
		//准备好待转换list,抽取index_id。即department_key
		gl = General.getIndependentInstance(rd.getKeyList(), rd.getKey2Index(), rd.getDataCache());
		List<Object> departmentKeyList= gl.extractObject("index_id");
		//开始转换
		List<Object> departmentNameList = gldep.reflect("department_key", "department_name", departmentKeyList, true);
		//将departmentNameList与faculty表中的institute字段合并，填充null条目
		gl = General.getIndependentInstance(rd.getKeyList(), rd.getKey2Index(), rd.getDataCache());
		gl.insertColumnAtTailString("department_name", departmentNameList, true);
		MergedGroup mg =MergeCreator.getIndependentInstance(Arrays.asList("department_name","institute"), "institute").getMg();
		gl.Merge(mg, true);
		//名字过滤
		List<String> nameList = gl.fork("name", true).getStringList();
		List<String> nameListNew = NameCleaner.convert(nameList);
		gl.replaceString("name", nameListNew);
		System.out.println("-----------------------------------------------------------------------------------------------------------------------");
		System.out.println("3.再次更新institute，把部分departmentName的信息更新至Institute表中");
		General gl2 = General.getIndependentInstance(gl.getKeyList(), gl.getKey2Index(), gl.getDataCache());
		gl2.extract("institute");
		gl2.changeKey("institute", "institute_name");
		gl2.shrinkRows(true);
		AntiReduplicationCheck.prepareDB(dbName, "institute");
		AntiReduplicationCheck.setMatchKeyListToInitHashValue("institute_name", true);
		ac = AntiReduplicationCheck.getIndependentInstance(gl2.getKeyList(), gl2.getKey2Index(), gl2.getDataCache());
		ac.checkBatch();
		AntiReduplicationCheck.insertWithConstruct(false);
		System.out.println("-----------------------------------------------------------------------------------------------------------------------");
		System.out.println("4.更新Department2institute表");
		//将更新的Institute表再次从数据库读入，作为参照表以备后用
		General glins = General.getIndependentInstance(dbName, "institute");
		General gl3 = General.getIndependentInstance(gl.getKeyList(), gl.getKey2Index(), gl.getDataCache());
		gl3.extract("index_id");
		List<Object> department_id = gldep.reflect("department_key", "id", gl3.getObjList(), true);
		List<Object> institute_id = glins.reflect("institute_name", "id", gl2.getObjList(), true);
		//生成空general容器
		General gl4 = General.getIndependentInstance();
		gl4.insertColumnAtTailInteger("department_id", department_id, true);
		gl4.insertColumnAtTailInteger("institute_id", institute_id, true);
		gl4.shrinkRows(true);
		//准备写入Department2institute表
		AntiReduplicationCheck.prepareDB(dbName, "department2institute");
		AntiReduplicationCheck.setMatchKeyListToInitHashValue(Arrays.asList("department_id","institute_id"), true);
		ac = new AntiReduplicationCheck(gl4.getKeyList(), gl4.getKey2Index(), gl4.getDataCache());
		ac.checkBatch();
		AntiReduplicationCheck.insertWithConstruct(false);
		System.out.println("-----------------------------------------------------------------------------------------------------------------------");
		System.out.println("5.将新来的Faculty数据填充id字段后插入数据库");
		StructureInfoCheck sic = StructureInfoCheck.getIndependentInstance("sys", "faculty_tbl", gl.getKeyList(), gl.getKey2Index(), gl.getDataCache());
		sic.checkAndTransfer();
		AntiReduplicationCheck.prepareDB("sys", "faculty_tbl");
		AntiReduplicationCheck.setMatchKeyListToInitHashValue(Arrays.asList("index_id","no","name"), true);
		AntiReduplicationCheck.setMergeKeyList(Arrays.asList("url"), true);
		ac = new AntiReduplicationCheck(sic.getKeyList(), sic.getKey2Index(), sic.getDataCache());
		ac.checkBatch();
		AntiReduplicationCheck.constructInsertObjList();
		AntiReduplicationCheck.constructUpdateObjList();
		AntiReduplicationCheck.fillInsertObjectListId(false);
		AntiReduplicationCheck.refreshDBCache();
		AntiReduplicationCheck.insertWithoutConstruct(true);
		AntiReduplicationCheck.updateWithoutConstruct("id");
		System.out.println("-----------------------------------------------------------------------------------------------------------------------");
		System.out.println("6.将新来的填充完id字段的Faculty数据放入内存以备后用");
		this.keyList.addAll(AntiReduplicationCheck.getKeyListDB());
		this.key2Index.putAll(AntiReduplicationCheck.getKey2IndexDB());
		
		for(List<Object> objListI:AntiReduplicationCheck.getDBCache()){
			List<Object> objList = new ArrayList<Object>();
			objList.addAll(objListI);
			this.dataCache.add(objList);
		}
		for(List<Object> objListI:AntiReduplicationCheck.getInsertObjList()){
			List<Object> objList = new ArrayList<Object>();
			objList.addAll(objListI);
			this.insertDataCache.add(objList);
		}
	}
	public void updateAvatar(boolean isVerbose){
		System.out.println("-----------------------------------------------------------------------------------------------------------------------");
		System.out.println("7.新旧照片名变换");
		AvatarTransferTool att = AvatarTransferTool.getIndependentInstance(keyList, key2Index, insertDataCache);
		att.convert(isVerbose);
	}
	public void updateAvatar(String sourceFileDir, String targetFileDir, boolean isVerbose){
		AvatarTransferTool att = AvatarTransferTool.getIndependentInstance(keyList, key2Index, insertDataCache);
		att.convert(sourceFileDir, targetFileDir, isVerbose);
	}
	public void refreshPersonAndPerson2Publication(String personTable, String person2publicationTable){
		System.out.println("-----------------------------------------------------------------------------------------------------------------------");
		System.out.println("8.更新person表和person2publication表");
		//存储找到的person2publication的新记录
		List<List<Object>> newRecords = new ArrayList<List<Object>>();
		//抽出新来person数据中的所有index_id,即department_key
		General gl = General.getIndependentInstance(keyList, key2Index, insertDataCache);
		gl.extract("index_id");
		//找出所有不同的departmentKey
		Set<Object> departmentKeySet = new HashSet<Object>();
		//以departmenKeyt作为最外层循环关键字
		List<Object> departmentKeyList = new ArrayList<Object>();
		departmentKeySet.addAll(gl.getObjList());
		for(Object key:departmentKeySet){
			departmentKeyList.add(key);
		}
		//取出需要的所有person数据
		General glperson = General.getIndependentInstance(keyList, key2Index, insertDataCache);
		glperson.extract(Arrays.asList("id","index_id","name"));
		//取出需要的所有publication数据
		General glpub = General.getIndependentInstance(dbName,"publication");
		glpub.extract(Arrays.asList("id","authors","institute_key"));
		//防止出现publication无作者情况
		glpub.shrinkRows(Arrays.asList("authors"),true);
		//存储index2Alias，方便之后填入perosn表的alias字段中
		Map<Object, List<Object>> index2Alias = new HashMap<Object, List<Object>>();
		for(Object departmentKey:departmentKeyList){
			General gl2 = glperson.fork(Arrays.asList("id","index_id","name"),true);
			//筛选出当前department的所有person
			gl2.filterRowsWithCondition("index_id", departmentKey);
			gl2.shrinkRows(true);
			//取出当前department的所有personName
			List<Object> personNameList = gl2.fork("name",true).extractObject("name");
			//取出当前department的所有personIdList
			List<Object> personIdList = gl2.fork("id",true).extractObject("id");
			//建立index到map的映射表
			Map<Object, Object> personId2Name = new HashMap<Object, Object>();
			for(int i=0; i<personNameList.size(); i++){
				personId2Name.put(personIdList.get(i), ((String)personNameList.get(i)).trim());
			}
			//存储别名到personId的映射关系
			Map<Object, List<Object>> alias2Index = new HashMap<Object, List<Object>>();
			int size = personNameList.size();
			//遍历当前系内的所有新personName
			for(int i=0; i<size; i++){
				//调用函数生成此person可能的所有别名
				String name = (String)personNameList.get(i);
				name = name.replaceAll(" ", "");
				List<Object> alias = new ArrayList<Object>();
				if(name.matches("[^a-zA-Z]*")){
					alias = NameGeneration.generateEnglishNamesObject(name);
				}
				//为每个person初始化Alias
				index2Alias.put(personIdList.get(i), new ArrayList<Object>());
				//先将自己本名加入别名中
				alias2Index.put(name.trim(), new ArrayList<Object>());
				alias2Index.get(name.trim()).add(personIdList.get(i));
				//遍历所有生成的别名
				for(Object o:alias){
					if(!alias2Index.containsKey(((String)o).trim())){
						alias2Index.put(((String)o).trim(), new ArrayList<Object>());
					}
					alias2Index.get(((String)o).trim()).add(personIdList.get(i));
				}
			}
			
			//准备department数据
			List<Object> abbreviation = DepartmentMap.getAbbreviation(departmentKey);
			General glp = null;
			if(null != abbreviation && !abbreviation.isEmpty()){
				glp = glpub.fork(Arrays.asList("id","authors","institute_key"), true);
				glp.fuzzyFilterRowsWithCondition("institute_key", abbreviation);
			}else{
				continue;
			}
			//取出属于当前department的publication中的所有authorName
			List<Object> publicationAuthorList = glp.fork("authors",true).extractObject("authors");
			//取出属于当前department的publication中的所有authorId
			List<Object> publicationIdList = glp.fork("id", true).extractObject("id");
			for(int i=0; i<publicationAuthorList.size(); i++){
				String authors[] = ((String)publicationAuthorList.get(i)).split("\\|\\|");
				for(int j=0; j<authors.length; j++){
					//论文的作者出现在 新person的alias里
					if(alias2Index.containsKey(authors[j].trim())){
						//多个person具有相同alias情况
						for(Object index:alias2Index.get(authors[j].trim())){
							List<Object> objList = new ArrayList<Object>();
							objList.add(index);
							objList.add(publicationIdList.get(i));
							objList.add(j+1);
							//增加一条记录
							newRecords.add(objList);
							//index2Alias添加记录,倘若是本名则不添加,否则添加
							if(!personId2Name.get(index).equals(authors[j].trim())){
								//查看现有的aliasList内是否已经含有当前alias，没有才添加
								if(!index2Alias.get(index).contains(authors[j].trim())){
									index2Alias.get(index).add(authors[j].trim());
								}
							}
						}
					}
				}
			}
		}
		//重组index2Alias
		List<Object> indexList =glperson.fork("id", true).getObjList();
		List<Object> nameAlias = new ArrayList<Object>();
		for(Object index:indexList){
			List<Object> alias = index2Alias.get(index);
			StringBuilder sb = new StringBuilder();
			for(Object o:alias){
				sb.append(o.toString());
				sb.append("||");
			}
			if(0 != sb.length()){
				sb.delete(sb.length()-2, sb.length());
				nameAlias.add(sb.toString());
			}else{
				nameAlias.add(null);
			}
		}
		General gl2 = glperson.fork(Arrays.asList("id","name"), true);
		gl2.insertColumnAtTailString("name_alias", nameAlias, true);
		
		//插入person表
		AntiReduplicationCheck.prepareDB(dbName, personTable);
		AntiReduplicationCheck.setMatchKeyListToInitHashValue("id", true);
		AntiReduplicationCheck.setReplaceKeyList(Arrays.asList("name","name_alias"), true);
		AntiReduplicationCheck ac = new AntiReduplicationCheck(gl2.getKeyList(), gl2.getKey2Index(), gl2.getDataCache());
		ac.checkBatch();
		AntiReduplicationCheck.insertAndUpdateWithConstruct(true, "id");
		
		//插入person2publication表
		Map<String ,ColumnStructure> key2Index = new HashMap<String, ColumnStructure>();
		key2Index.put("person_id", General.columnStructureCreator(0, "int"));
		key2Index.put("publication_id", General.columnStructureCreator(1, "int"));
		key2Index.put("position", General.columnStructureCreator(2, "int"));
		gl2 = General.getIndependentInstance(Arrays.asList("person_id","publication_id","position"), key2Index, newRecords);
		AntiReduplicationCheck.prepareDB(dbName, person2publicationTable);
		AntiReduplicationCheck.setMatchKeyListToInitHashValue(Arrays.asList("person_id","publication_id","position"), true);
		ac = new AntiReduplicationCheck(gl2.getKeyList(), gl2.getKey2Index(), gl2.getDataCache());
		ac.checkBatch();
		AntiReduplicationCheck.insertWithConstruct(false);
	}
	
	public void refreshPersonInfo(String targetTableName){
		System.out.println("-----------------------------------------------------------------------------------------------------------------------");
		System.out.println("9.更新personInfo表");
		//准备好新来的数据
		General gl = General.getIndependentInstance(keyList, key2Index, dataCache);
		gl.extract(Arrays.asList("id","education","projects","resume","honor","parttime","fields","summary","achievements","experience"));
		
		//准备好数据库数据
		AntiReduplicationCheck.prepareDB(dbName, targetTableName);
		AntiReduplicationCheck.setMatchKeyListToInitHashValue("id", true);
		AntiReduplicationCheck.setReplaceKeyList(Arrays.asList("education","projects","resume","honor","parttime","fields","summary","achievements","experience"), true);
		AntiReduplicationCheck ac = new AntiReduplicationCheck(gl.getKeyList(), gl.getKey2Index(), gl.getDataCache());
		ac.checkBatch();
		AntiReduplicationCheck.insertAndUpdateWithConstruct(true, "id");
	}
	
	public void refreshPersonProfile(String targetTableName){
		System.out.println("-----------------------------------------------------------------------------------------------------------------------");
		System.out.println("10.更新personProfile表");
		//取出待转化的department_key列
		General gl = General.getIndependentInstance(keyList, key2Index, dataCache);
		gl.extract(Arrays.asList("index_id"));
		
		//准备好转换的列
		General gldep = General.getIndependentInstance(dbName,"department");
		//开始转换
		List<Object> department_id = gldep.reflect("department_key", "id", gl.getObjList(), true);
		
		gl = General.getIndependentInstance(keyList, key2Index, dataCache);
		gl.extract(Arrays.asList("id","no","position","index_id","office","phone","email","url","photo","author_id"));
		//鉴于原始数据homepage基本都为空，url数据较为全面，因此用url替换homepage
		gl.changeKey("url", "homepage");
		gl.changeKey("index_id", "department_key");
		gl.changeKey("office", "location");
		gl.changeKey("photo", "imagelink");
		gl.insertColumnAtTailInteger("department_id", department_id, true);
		
		AntiReduplicationCheck.prepareDB(dbName, "person_profile");
		AntiReduplicationCheck.setMatchKeyListToInitHashValue("id", true);
		AntiReduplicationCheck.setReplaceKeyList(Arrays.asList("no","position","department_id","department_key","location","phone","email","homepage","photo","author_id"));
		AntiReduplicationCheck ac = new AntiReduplicationCheck(gl.getKeyList(), gl.getKey2Index(), gl.getDataCache());
		ac.checkBatch();
		AntiReduplicationCheck.insertAndUpdateWithConstruct(true, "id");
	}
	
	public void refreshPersonTitle(String targetTableName){
		System.out.println("-----------------------------------------------------------------------------------------------------------------------");
		System.out.println("11.更新personExt表");
		TitleGenerateTool tgt = TitleGenerateTool.getIndependentInstance(dbName, targetTableName, keyList, key2Index, insertDataCache);
		tgt.process();
	}
	
	public void refreshPerson2Course(String targetTableName){
		System.out.println("-----------------------------------------------------------------------------------------------------------------------");
		System.out.println("12.更新person2Course表");
		//取新来person的id和name，作为参考表
		General gl = General.getIndependentInstance(keyList, key2Index, insertDataCache);
		gl.extract(Arrays.asList("id","name"));
		
		//取现有数据库中course表的person_name信息
		ReadFromDataBase rd = ReadFromDataBase.getIndependentInstance(dbName, "course");
		rd.getAllData();
		
		General gl2 = General.getIndependentInstance(rd.getKeyList(), rd.getKey2Index(), rd.getDataCache());
		gl2.extract("teacherName");
		
		//进行转换看生成idList
		List<Object> idList = gl.reflect("name", "id", gl2.getObjList(), true);
		
		//加入到course表的的尾端
		gl2 = General.getIndependentInstance(rd.getKeyList(), rd.getKey2Index(), rd.getDataCache());
		gl2.extract("id");
		gl2.changeKey("id", "course_id");
		gl2.insertColumnAtTailInteger("person_id", idList, true);
		
		//对gl2进行shrink，删除出现null的行
		gl2.shrinkRows(true);
		
		//然后准备插入数据库
		AntiReduplicationCheck.prepareDB(dbName, targetTableName);
		AntiReduplicationCheck.setMatchKeyListToInitHashValue(Arrays.asList("person_id,course_id"), true);
		
		AntiReduplicationCheck ac = AntiReduplicationCheck.getIndependentInstance(gl2.getKeyList(), gl2.getKey2Index(), gl2.getDataCache());
		ac.checkBatch();
		AntiReduplicationCheck.insertWithConstruct(false);
	}
	
	public void refreshPersonInterest(String targetTableName){
		System.out.println("-----------------------------------------------------------------------------------------------------------------------");
		System.out.println("13.更新personInterest表");
		//将person_file的数据全部读入作为参考表
		General gl = General.getIndependentInstance(keyList, key2Index, insertDataCache);
		gl.extract(Arrays.asList("id", "author_id"));
		gl.shrinkRows(Arrays.asList("author_id"),true);
		General gl2 = General.getIndependentInstance(dbName, targetTableName);
		gl2.extract("aid");
		List<Object> aidList = gl2.getObjList();
		List<Object> personIdList = gl.reflect("author_id", "id", aidList, true);
		gl2.insertColumnAtTailInteger("person_id", personIdList, true);
		gl2.shrinkRows(Arrays.asList("person_id","aid"),true);
		AntiReduplicationCheck.prepareDB(dbName, targetTableName);
		AntiReduplicationCheck.setMatchKeyListToInitHashValue("aid", true);
		AntiReduplicationCheck.setReplaceKeyList("person_id", true);
		AntiReduplicationCheck ac =AntiReduplicationCheck.getIndependentInstance(gl2.getKeyList(), gl2.getKey2Index(), gl2.getDataCache());
		ac.checkBatch();
		AntiReduplicationCheck.updateWithConstruct("aid");
	}
	
	public void refreshPeson2Institute(String targetTableName){
		System.out.println("-----------------------------------------------------------------------------------------------------------------------");
		System.out.println("14.更新person2Institute");
		General gl = General.getIndependentInstance(keyList, key2Index, insertDataCache);
		gl.extract("institute");
		
		List<Object> instituteNameList = gl.getObjList();
		
		//准备参照表
		General glins = General.getIndependentInstance(dbName, "institute");
		List<Object> instituteIdList = glins.reflect("institute_name", "id", instituteNameList, true);
		
		gl = General.getIndependentInstance(keyList, key2Index, insertDataCache);
		gl.extract("id");
		gl.changeKey("id", "person_id");
		gl.insertColumnAtTailInteger("institute_id", instituteIdList, true);
		
		AntiReduplicationCheck.prepareDB(dbName, targetTableName);
		AntiReduplicationCheck.setMatchKeyListToInitHashValue(Arrays.asList("person_id","institute_id"), true);
		AntiReduplicationCheck ac = AntiReduplicationCheck.getIndependentInstance(gl.getKeyList(), gl.getKey2Index(), gl.getDataCache());
		ac.checkBatch();
		AntiReduplicationCheck.insertWithConstruct(false);
	}
	
	public void refreshPerson2Graduatepublication(String targetTableName){
		System.out.println("-----------------------------------------------------------------------------------------------------------------------");
		System.out.println("15.更新person2Graduatepublication");
		General gl = General.getIndependentInstance(keyList, key2Index, insertDataCache);
		gl.extract("index_id");
		//找出所有不同的departmentKey
		Set<Object> departmentKeySet = new HashSet<Object>();
		List<Object> departmentKeyList = new ArrayList<Object>();
		departmentKeySet.addAll(gl.getObjList());
		for(Object key:departmentKeySet){
			departmentKeyList.add(key);
		}
		
		//准备参照表生成departmentKey对应的departmentId
		gl = General.getIndependentInstance(dbName, "department");
		List<Object> departmentIdList = gl.reflect("department_key", "id", departmentKeyList, true);
		//循环department更新
		for(int i=0; i<departmentIdList.size(); i++){
			Object departmentKey = departmentKeyList.get(i);
			Object departmentId = departmentIdList.get(i);
			
			//准备person数据，作为参照表
			General glper = General.getIndependentInstance(keyList, key2Index, insertDataCache);
			glper.extract(Arrays.asList("id","index_id","name"));
			glper.filterRowsWithCondition("index_id", departmentKey);
			glper.extract(Arrays.asList("id","name"));
			//准备graduatePublication数据
			General gl2 = General.getIndependentInstance(dbName, "graduatepublication");
			gl2.filterRowsWithCondition("department_id", departmentId);
			gl2.extract(Arrays.asList("id","tutor1_name","tutor2_name","author"));
			//先对tutor1_name进行匹配
			General gl3 = gl2.fork("tutor1_name", true);
			List<Object> tutor1IdList = glper.reflect("name", "id", gl3.getObjList(), true);
			General gl4 = General.getIndependentInstance();
			gl4.insertColumnAtTailInteger("person_id", tutor1IdList, true);
			gl4.insertColumnAtTailInteger("graduatepublication_id", gl2.fork("id", true).getObjList(), true);
			gl4.insertColumnAtTail("type", "tutor");
			gl4.insertColumnAtTail("position", 1);
			gl4.shrinkRows(true);
			AntiReduplicationCheck.prepareDB(dbName, targetTableName);
			AntiReduplicationCheck.setMatchKeyListToInitHashValue(Arrays.asList("person_id","graduatepublication_id","type","position"), true);
			AntiReduplicationCheck ac = AntiReduplicationCheck.getIndependentInstance(gl4.getKeyList(), gl4.getKey2Index(), gl4.getDataCache());
			ac.checkBatch();
			//对tutor2_name进行匹配
			gl3 = gl2.fork("tutor2_name", true);
			List<Object> tutor2IdList = glper.reflect("name", "id", gl3.getObjList(), true);
			gl4 = General.getIndependentInstance();
			gl4.insertColumnAtTailInteger("person_id", tutor2IdList, true);
			gl4.insertColumnAtTailInteger("graduatepublication_id", gl2.fork("id", true).getObjList(), true);
			gl4.insertColumnAtTail("type", "tutor");
			gl4.insertColumnAtTail("position", 2);
			gl4.shrinkRows(true);
			ac = AntiReduplicationCheck.getIndependentInstance(gl4.getKeyList(), gl4.getKey2Index(), gl4.getDataCache());
			ac.checkBatch();
			//对author进行匹配
			gl3 = gl2.fork("author", true);
			List<Object> authorIdList = glper.reflect("name", "id", gl3.getObjList(), true);
			gl4 = General.getIndependentInstance();
			gl4.insertColumnAtTailInteger("person_id", authorIdList, true);
			gl4.insertColumnAtTailInteger("graduatepublication_id", gl2.fork("id", true).getObjList(), true);
			gl4.insertColumnAtTail("type", "author");
			gl4.insertColumnAtTail("position", 1);
			gl4.shrinkRows(true);
			ac = AntiReduplicationCheck.getIndependentInstance(gl4.getKeyList(), gl4.getKey2Index(), gl4.getDataCache());
			ac.checkBatch();
			
			AntiReduplicationCheck.insertWithConstruct(false);
		}
	}
	
	public void process(String dbNameSource, String sourceTableName){
		this.keyList.clear();
		this.key2Index.clear();
		this.dataCache.clear();
		this.insertDataCache.clear();
		newDataTransfer(dbNameSource,sourceTableName);
		updateAvatar(false);
		refreshPersonAndPerson2Publication("person","person2publication");
		refreshPersonInfo("person_info");
		refreshPersonProfile("person_profile");
		refreshPersonTitle("person_ext");
//		refreshPerson2Course("person2course");
		refreshPersonInterest("person_interest");
		refreshPeson2Institute("person2institute");
		refreshPerson2Graduatepublication("person2graduatepublication");
	}
	public static void main(String[] args) {
		DBPersonImportTool dbit = new DBPersonImportTool();
		dbit.process("import2", "faculty_tbl");
	}
}
