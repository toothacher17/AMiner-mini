package SchoolSearch.services.utils.dataUpdateTools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import SchoolSearch.services.utils.dataUpdateTools.basic.manipulate.General;
import SchoolSearch.services.utils.dataUpdateTools.basic.read.ReadFromDataBase;
import SchoolSearch.services.utils.dataUpdateTools.basic.validate.AntiReduplicationCheck;
import SchoolSearch.services.utils.dataUpdateTools.model.ColumnStructure;

public class DepartmentImportTool {
	private String targetDBName = "cache";
	private List<String> keyList;
	private Map<String, ColumnStructure> key2Index;
	private List<List<Object>> dataCache;
	
	
	public DepartmentImportTool(){
		this.keyList = new ArrayList<String>();
		this.key2Index = new HashMap<String, ColumnStructure>();
		this.dataCache = new ArrayList<List<Object>>();
	}
	
	public DepartmentImportTool(String dbName){
		this.targetDBName = dbName;
		this.keyList = new ArrayList<String>();
		this.key2Index = new HashMap<String, ColumnStructure>();
		this.dataCache = new ArrayList<List<Object>>();
	}
	
	public void loadSourceData(String sourceDbName, String sourceTableName){
		ReadFromDataBase rd = ReadFromDataBase.getIndependentInstance(sourceDbName, sourceTableName);
		rd.getAllData();
		
		this.keyList.addAll(rd.getKeyList());
		this.key2Index.putAll(rd.getKey2Index());
		
		for(List<Object> objListI:rd.getDataCache()){
			List<Object> objList = new ArrayList<Object>();
			objList.addAll(objListI);
			this.dataCache.add(objList);
		}
	}
	public void refreshSchool(String targetTableName){
		General gl = General.getIndependentInstance(keyList, key2Index, dataCache);
		gl.extract("institute_name");
		gl.changeKey("institute_name", "schoolname");
		AntiReduplicationCheck.prepareDB(targetDBName, targetTableName);
		AntiReduplicationCheck.setMatchKeyListToInitHashValue("schoolname", true);
		
		AntiReduplicationCheck ac = AntiReduplicationCheck.getIndependentInstance(gl.getKeyList(), gl.getKey2Index(), gl.getDataCache());
		ac.checkBatch();
		AntiReduplicationCheck.insertWithConstruct(false);
	}
	
	public void refreshDepartment(String targetTableName){
		General gl = General.getIndependentInstance(keyList, key2Index, dataCache);
		gl.extract("institute_name");
		gl.changeKey("institute_name", "schoolname");
		
		//准备好参照表
		General glsch = General.getIndependentInstance(targetDBName, "school");
		
		List<Object> schoolIdList = glsch.reflect("schoolname", "id", gl.getObjList(), true);
		
		//准备拼接
		gl = General.getIndependentInstance(keyList, key2Index, dataCache);
		gl.extract(Arrays.asList("institute_id","department_name"));
		gl.changeKey("institute_id", "department_key");
		gl.insertColumnAtTailInteger("school_id", schoolIdList, true);
		AntiReduplicationCheck.prepareDB(targetDBName, targetTableName);
		AntiReduplicationCheck.setMatchKeyListToInitHashValue("department_key", true);
//		AntiReduplicationCheck.setReplaceKeyList(Arrays.asList("school_id","department_key"));
		
		AntiReduplicationCheck ac = AntiReduplicationCheck.getIndependentInstance(gl.getKeyList(), gl.getKey2Index(), gl.getDataCache());
		ac.checkBatch();
//		AntiReduplicationCheck.insertAndUpdateWithConstruct(false, "department_name");
		AntiReduplicationCheck.insertWithConstruct(false);
	}
	public void process(String sourceDbName, String sourceTableName){
		this.keyList.clear();
		this.key2Index.clear();
		this.dataCache.clear();
		loadSourceData(sourceDbName, sourceTableName);
		refreshSchool("school");
		refreshDepartment("department");
	}
	public static void main(String[] args) {
//		DepartmentImportTool dtt = new DepartmentImportTool();
//		dtt.loadSourceData("import2", "departments_tbl");
//		dtt.refreshSchool("school");
//		dtt.refreshDepartment("department");
	}
}
