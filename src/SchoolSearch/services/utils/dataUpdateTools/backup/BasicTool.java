package SchoolSearch.services.utils.dataUpdateTools.backup;
/**
 * @author CX
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import SchoolSearch.services.utils.dataUpdateTools.model.ColumnStructure;

public class BasicTool {
	private static BasicTool instance;

	private List<String> keyList;
	private Map<String, ColumnStructure> key2Index;
	private List<List<Object>> tableCache;


	public static BasicTool getInstance() {
		if (null == instance) {
			instance = new BasicTool();
		}
		return instance;
	}

	public static BasicTool getIndependentInstance() {
		return new BasicTool();
	}

	private BasicTool() {
		tableCache = new ArrayList<List<Object>>();
		keyList = new ArrayList<String>();
		key2Index = new HashMap<String, ColumnStructure>();
	}

	private void appendFrom(List<List<Object>> tableCache2,List<String> keyList2, Map<String, ColumnStructure> key2Index2) {
		if (tableCache.isEmpty() && keyList.isEmpty() && key2Index.isEmpty()) {
			keyList.addAll(keyList2);
			tableCache.addAll(tableCache2);
			key2Index.putAll(key2Index2);
		} else {
			//构造keyList
			keyList.addAll(keyList2);
			
			//构造Key2Index
			int size = key2Index.size();
			for (int i = size; i < keyList.size(); i++) {
				ColumnStructure cs = new ColumnStructure();
				cs.index = i;
				cs.type = key2Index2.get(keyList.get(i)).type;
				key2Index.put(keyList.get(i), cs);
			}
			
			//构造tableCache
			for (int i = 0; i < tableCache.size(); i++) {
				tableCache.get(i).addAll(tableCache2.get(i));
			}
		}
	}

	public List<List<Object>> getTableCache() {
		return tableCache;
	}

	public void setTableCache(List<List<Object>> tableCache) {
		this.tableCache = tableCache;
	}


	public void extractSelectedColumnsFromSourceTableWithIdLimited(String dbName, String tableName, List<String> keyList, int startId, int endId) {
		CommonDao commonDao = CommonDao.getIndependentInstance(dbName, tableName, keyList);
		List<List<Object>> tableCacheTmp = commonDao.selectByKeyListWithIdLimited(startId, endId);
		List<String> keyListTmp = commonDao.getKeyList();
		Map<String, ColumnStructure> key2IndexTmp = commonDao.getKey2IndexSelectedMap();
		appendFrom(tableCacheTmp, keyListTmp, key2IndexTmp);
	}

	public void insertInto(String dbName, String tableName){
		CommonDao commonDao = CommonDao.getIndependentInstance(dbName, tableName, keyList, key2Index, tableCache);
		commonDao.insertBatchRecord();
	}

	public void modifySelectedColumnsWithReferenceTable() {

	}

	public void printTableCache() {
		if (null != tableCache) {
			for (List<Object> objList : tableCache) {
				for (Object str : objList) {
					System.out.print(str);
					System.out.print("   ");
				}
				System.out.println();
			}
		} else {
			System.out.println("dataList is empty");
		}
	}

	public void printKey2IndexMap() {
		if (null != key2Index) {
			for (String str : key2Index.keySet()) {
				System.out.println(key2Index.get(str).index + " "
						+ key2Index.get(str).type);
			}
		} else {
			System.out.println("key2Index is empty");
		}
	}

	public void printKeyList() {
		if (null != keyList) {
			for (String key : keyList) {
				System.out.print(key + " ");
			}
			System.out.println();
		} else {
			System.out.println("keyList is empty!");
		}
	}

	public void printAll() {
		printKeyList();
		printKey2IndexMap();
		printTableCache();
	}

	public static void main(String[] args) {
		BasicTool basicTool = BasicTool.getInstance();
		basicTool.extractSelectedColumnsFromSourceTableWithIdLimited("schoolsearch","department", new ArrayList<String>(Arrays.asList("id", "school_id","department_name")), 1, 20);
		basicTool.extractSelectedColumnsFromSourceTableWithIdLimited("schoolsearch", "school", new ArrayList<String>(Arrays.asList("schoolname")), 1, 20);
		basicTool.printKeyList();
		basicTool.printKey2IndexMap();
		basicTool.printTableCache();
		basicTool.insertInto("schoolsearch", "test");
	}
}
