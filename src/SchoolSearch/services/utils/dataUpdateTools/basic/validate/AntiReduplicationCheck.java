package SchoolSearch.services.utils.dataUpdateTools.basic.validate;

/**实现去重功能,一次性将数据库的数据load进内存,然后将输入数据源数据按matchKeyList进行重复检验,按mergekeyList进行合并,最后生成数据库格式的数据
 * @author CX
 *
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import SchoolSearch.services.utils.dataUpdateTools.basic.manipulate.General;
import SchoolSearch.services.utils.dataUpdateTools.basic.read.ReadFromDataBase;
import SchoolSearch.services.utils.dataUpdateTools.basic.write.InsertIntoDataBase;
import SchoolSearch.services.utils.dataUpdateTools.basic.write.UpdateToDataBase;
import SchoolSearch.services.utils.dataUpdateTools.model.ColumnStructure;

public class AntiReduplicationCheck {

	// 数据库的信息
	private List<String> keyListData;
	private Map<String, ColumnStructure> key2IndexData;
	private List<List<Object>> dataCache;

	
	
	private static String dbName;
	private static String tableName;
	// 静态类型，共享，以防多次读取数据库
	private static List<String> keyListDB = new ArrayList<String>();
	private static Map<String, ColumnStructure> key2IndexDB = new HashMap<String, ColumnStructure>();
	private static List<List<Object>> DBCache = new ArrayList<List<Object>>();
	private static List<Integer> hashValue = new ArrayList<Integer>();
	//0代表数据库原始存在且无需更新的,1代表待插入数据库的,2代表已经存在但是需要更新的
	private static List<Integer> stateList = new ArrayList<Integer>();
	private static List<String> matchKeyList = new ArrayList<String>();
	private static List<String> mergeKeyList = new ArrayList<String>();
	private static List<String> replaceKeyList = new ArrayList<String>();
	
	private static Integer updateTotal;
	private static Integer insertTotal;
	private static Integer dumpTotal;
	private static Integer comeTotoal;
	//存储insert和update的record
	private static List<List<Object>> insertObjList = new ArrayList<List<Object>>();
	private static List<List<Object>> updateObjList = new ArrayList<List<Object>>();
	
	private static AntiReduplicationCheck instance;


	public static AntiReduplicationCheck getInstance(List<String> keyListData,
			Map<String, ColumnStructure> key2IndexData,
			List<List<Object>> DataCache) {
		if (null == instance) {
			instance = new AntiReduplicationCheck(keyListData, key2IndexData,
					DataCache);
		}
		return instance;
	}
/**
 * 
 * @param keyListData 待比较数据的keyList
 * @param key2IndexData 待比较数据的key2Index
 * @param DataCache 待比较数据的DataCache
 * @return
 */
	public static AntiReduplicationCheck getIndependentInstance(
			List<String> keyListData,
			Map<String, ColumnStructure> key2IndexData,
			List<List<Object>> DataCache) {
		return new AntiReduplicationCheck(keyListData, key2IndexData,
				DataCache);
	}

	

	public AntiReduplicationCheck(List<String> keyListData,
			Map<String, ColumnStructure> key2IndexData,
			List<List<Object>> DataCache) {
		this.keyListData = new ArrayList<String>();
		this.key2IndexData = new HashMap<String, ColumnStructure>();
		this.dataCache = new ArrayList<List<Object>>();

		

//		// 构造keyList
//		for (String key : keyListData) {
//			this.keyListData.add(key.toLowerCase().trim());
//		}
//		
//
//		// 构造key2Index
//		for (String key : key2IndexData.keySet()) {
//			ColumnStructure cs = new ColumnStructure();
//			cs.index = key2IndexData.get(key).index;
//			cs.type = key2IndexData.get(key).type;
//			this.key2IndexData.put(key.toLowerCase().trim(), cs);
//		}
		this.keyListData.addAll(keyListData);
		this.key2IndexData.putAll(key2IndexData);

		// 构造cache
		for (List<Object> objList : DataCache) {
			List<Object> objListTmp = new ArrayList<Object>();
			objListTmp.addAll(objList);
			this.dataCache.add(objListTmp);
		}
	}
	public static void refreshDBCache(){
		DBCache.clear();
		//更新insertObjectList至DBCache
		for(List<Object> objList:insertObjList){
			List<Object> objListTmp = new ArrayList<Object>();
			objListTmp.addAll(objList);
			DBCache.add(objListTmp);
		}
		//更新updateObjectList至DBCache
		for(List<Object> objList:updateObjList){
			List<Object> objListTmp = new ArrayList<Object>();
			objListTmp.addAll(objList);
			DBCache.add(objListTmp);
		}
	}
	public static void fillInsertObjectListId(boolean isVerbose){
		ReadFromDataBase rd = ReadFromDataBase.getIndependentInstance(dbName, tableName);
		Integer begin = rd.getLastRecordId();
		Integer count = insertObjList.size();
		//填充id
		List<Object> indexList = new ArrayList<Object>();
		for(int i=begin+1; i< begin+count+1; i++){
			indexList.add(i);
		}
		
		General gl = General.getIndependentInstance(keyListDB, key2IndexDB, insertObjList);
		gl.replace(0, "id", indexList);
		insertObjList.clear();
		for(List<Object> objL:gl.getDataCache()){
			List<Object> objTmp = new ArrayList<Object>();
			objTmp.addAll(objL);
			insertObjList.add(objTmp);
		}
		
	}
	public static void prepareDB(String dbName, String tableName){
		updateTotal = 0;
		insertTotal = 0;
		dumpTotal = 0;
		comeTotoal = 0;
		matchKeyList.clear();
		stateList.clear();
		hashValue.clear();
		replaceKeyList.clear();
		mergeKeyList.clear();
		ReadFromDataBase rd =  new ReadFromDataBase(dbName,tableName);
		rd.getAllData();
		AntiReduplicationCheck.setKeyListDB(rd.getKeyList());
		AntiReduplicationCheck.setKey2IndexDB(rd.getKey2Index());
		AntiReduplicationCheck.setDBCache(rd.getDataCache());
		AntiReduplicationCheck.dbName = dbName;
		AntiReduplicationCheck.tableName = tableName;
	}
	
	public static boolean setMatchKeyListToInitHashValue(boolean isVerbose) {
		String key = null;
		return setMatchKeyListToInitHashValue(key, isVerbose);
	}
	public static boolean setMatchKeyListToInitHashValue(String matchKey, boolean isVerbose){
		List<String> matchKeyList = new ArrayList<String>();
		if(null != matchKey){
			matchKeyList.add(matchKey);
		}
		return setMatchKeyListToInitHashValue(matchKeyList, isVerbose);
	}
	public static boolean setMatchKeyListToInitHashValue(List<String> matchKeyList, boolean isVerbose) {
		boolean result = true;
		//先清空
//		AntiReduplicationCheck.hashValue.clear();
//		AntiReduplicationCheck.matchKeyList.clear();
//		AntiReduplicationCheck.stateList.clear();
		List<String> matchKeyListNew = General.inputKeyListFilterString(matchKeyList, keyListDB, isVerbose);
		if (matchKeyListNew.isEmpty()) {
			//打印
			System.out.println();
			//去掉id
			List<String> keyListDBNew = new ArrayList<String>();
			keyListDBNew.addAll(keyListDB);
			if(keyListDBNew.contains("id")){
				keyListDBNew.remove("id");
			}
			AntiReduplicationCheck.matchKeyList.addAll(keyListDBNew);
			System.out.print("[InitHashValueInfo]NO matchKeyList IS DETECTED, USE ALL KEYS TO MATCH:");
			for(String key:keyListDBNew){
				System.out.print(key + " ");
			}
		} else {
			System.out.print("[InitHashValueInfo]matchKeyList IS DETECTED, USE FOLLOWING KEYS TO MATCH:");
			for(String key:matchKeyListNew){
				System.out.print(key + " ");
			}
			System.out.println();
			for(String key:matchKeyListNew){
				AntiReduplicationCheck.matchKeyList.add(key);
			}
		}
		//构造hashValue
		if (null != DBCache && !DBCache.isEmpty()) {
			for (List<Object> objList : DBCache) {
				//初始时都为0状态
				StringBuilder sb = new StringBuilder();
				for (String key : AntiReduplicationCheck.matchKeyList) {
					// 在数据库表字段中无此matcheKey,则生成hash值无意义，报错
					if (null == key2IndexDB.get(key)) {
						System.out.println("[InitHashValueErrorInfo]KEY "+ key + " IS NOT FOUND IN THE key2IndexDB!PLEASE CHECK!");
						result = false;
						break;
					} else if (null != objList.get(key2IndexDB.get(key).index)) {
						sb.append(objList.get(key2IndexDB.get(key).index).toString().trim());
					}
				}
				stateList.add(0);
				hashValue.add(sb.toString().hashCode());
			}
		} else {
			System.out.println("[InitHashValueErrorInfo]DBCache IS EMPTY!SKIP iniHashValue STEP!");
		}
		return result;
	}

	public static void constructInsertObjList(){
		AntiReduplicationCheck.insertObjList.clear();
		for(int i=0; i<stateList.size(); i++){
			List<Object> objTmp = new ArrayList<Object>();
			if(1 == stateList.get(i)){
				objTmp.addAll(DBCache.get(i));
				insertObjList.add(objTmp);
			}
		}
	}
	public static void constructUpdateObjList(){
		AntiReduplicationCheck.updateObjList.clear();
		for(int i=0; i<stateList.size(); i++){
			List<Object> objTmp = new ArrayList<Object>();
			if(2 == stateList.get(i)){
				objTmp.addAll(DBCache.get(i));
				updateObjList.add(objTmp);
			}
		}
	}
//	public static void insertConstructlyWithoutNullValue(boolean isFillId){
//		constructInsertObjList();
//		InsertIntoDataBase idb = InsertIntoDataBase.getIndependentInstance(dbName, tableName, keyListDB, key2IndexDB, insertObjList, isFillId);
//		idb.insertBatchRecordWithoutNullValue();
//	}
	public static void insertWithConstruct(boolean isFillId){
		constructInsertObjList();
		insertWithoutConstruct(isFillId);
	}
	public static void updateWithConstruct(String accordingKey){
		constructUpdateObjList();
		updateWithoutConstruct(accordingKey);
	}
	public static void insertWithoutConstruct(boolean isFillId){
		InsertIntoDataBase idb = InsertIntoDataBase.getIndependentInstance(dbName, tableName, keyListDB, key2IndexDB, insertObjList, isFillId);
		idb.insertBatchRecord();
	}
	public static void updateWithoutConstruct(String accordingKey){
		UpdateToDataBase udb = UpdateToDataBase.getIndependentInstance(dbName, tableName);
		udb.updateBatch(keyListDB, key2IndexDB, updateObjList, accordingKey);
	}
	
	public static void insertAndUpdateWithConstruct(String dbName, String tableName, boolean isFillId, String accordingKey){
		constructUpdateObjList();
		UpdateToDataBase udb = UpdateToDataBase.getIndependentInstance(dbName, tableName);
		udb.updateBatch(keyListDB, key2IndexDB, updateObjList, accordingKey);
		
		constructInsertObjList();
		InsertIntoDataBase idb = InsertIntoDataBase.getIndependentInstance(dbName, tableName, keyListDB, key2IndexDB, insertObjList, isFillId);
		idb.insertBatchRecord();
		
		
	}
	public static void insertAndUpdateWithConstruct(boolean isFillId, String accordingKey){
		insertWithConstruct(isFillId);
		updateWithConstruct(accordingKey);
		
	}
	public void  checkBatch() {
		checkBatch(false);
	}

	public void checkBatch(boolean isVerbose) {
		//先打印数据库现有条目信息
		
		int existCount = DBCache.size();
		int newCount = dataCache.size();
		int insertCountNew = 0;
		int updateCountNew = 0;
		//处理过滤
		for (int i = 0; i < newCount; i++) {
			checkSingle(dataCache.get(i), matchKeyList, mergeKeyList, replaceKeyList, isVerbose);
		}
		for(int i=0; i<stateList.size(); i++){
			if(1 == stateList.get(i)){
				insertCountNew++;
			}else if(2 == stateList.get(i)){
				updateCountNew++;
			}
		}
		int insertCount = insertCountNew - insertTotal;
		int updateCount = updateCountNew - updateTotal;
		int totalCount = DBCache.size();
		int reduplicationCount = newCount - updateCount - insertCount;
		insertTotal = insertCountNew;
		updateTotal= updateCountNew;
		dumpTotal += reduplicationCount;
		comeTotoal += newCount;
		System.out.println("[CheckAndAppendBatchInfo]RECORDS ORIGINAL--->" + existCount);
		System.out.println("[CheckAndAppendBatchInfo]RECORDS COMES--->" + newCount);
		System.out.println("[CheckAndAppendBatchInfo]INSERTED RECORDS--->" + insertCount);
		System.out.println("[CheckAndAppendBatchInfo]UPDATED RECORDS--->" + updateCount);
		System.out.println("[CheckAndAppendBatchInfo]REDUPLICATION RECORDS--->" + reduplicationCount);
		System.out.println("[CheckAndAppendBatchInfo]RECORD NOW--->" + totalCount);
	}
/**
 * 
 * @param matchObjList 待比较的List<Object>，
 * @param matchKeyList 去重依据的字段List
 * @param mergeKeyList 发现重复后合并依据的字段List
 * @param isVerbose 是否显示运行状态信息
 * @return
 */
	//mergeKeyList字段必须为字符串型
	private void checkSingle(List<Object> matchObjList, List<String> matchKeyList, List<String> mergeKeyList, List<String> replaceKeyList, boolean isVerbose) {
		boolean isTheSame = true;
		List<String> iterator = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		if (null == matchKeyList) {
			iterator.addAll(keyListDB);
		} else {
			iterator.addAll(matchKeyList);
		}
		//计算hash值
		for (String key : iterator) {
			if (null != key2IndexData.get(key) && null != matchObjList.get(key2IndexData.get(key).index)) {
				sb.append(matchObjList.get(key2IndexData.get(key).index).toString().trim());
			}
		}
		Integer code = sb.toString().hashCode();
		//hashValue发现一致
		if (hashValue.contains(code)) {
			// 进一步验证是否相同
			if (null != DBCache.get(hashValue.indexOf(code))) {
				List<Object> existObjList = DBCache.get(hashValue.indexOf(code));
				for (String matchKey : iterator) {
					//data中无key，DB中有key，但是值为null，表示相同
					if(!key2IndexData.containsKey(matchKey)&& key2IndexDB.containsKey(matchKey) && null == existObjList.get(key2IndexDB.get(matchKey).index)){
						isTheSame = true;
					// 字段都存在，比较值
					}else if (key2IndexData.containsKey(matchKey)&& key2IndexDB.containsKey(matchKey)) {
						Object fieldData = matchObjList.get(key2IndexData.get(matchKey).index);
						Object fieldDB = existObjList.get(key2IndexDB.get(matchKey).index);
						if((null == fieldData || (String.class.isInstance(fieldData) && fieldData.toString().equalsIgnoreCase("")))&& 
							(null == fieldDB || (String.class.isInstance(fieldDB) && fieldDB.toString().equalsIgnoreCase(""))))
						{
							isTheSame = true;
						}else if(null != fieldData && null != fieldDB){
							if(String.class.isInstance(fieldData) && String.class.isInstance(fieldDB)){
								fieldData = fieldData.toString().trim();
								fieldDB = fieldDB.toString().trim();
							}
							if(!fieldData.equals(fieldDB)){
								isTheSame = false;
								break;
							}else{
								isTheSame = true;
							}
							
						}else{
							isTheSame = false;
							break;
						}
					} else{
						// 否则一定不同
						isTheSame = false;
						break;
					}
				}
			} else {
				System.out
						.println("[CheckAndAppendErrorInfo]THE SPECIFIC dataList IS NOT FOUND!FAILED!");
				return;
			}
		}else{
			isTheSame = false;
		}
		
		//如果的确相同，则要合并
		if(isTheSame){
			Integer ind = hashValue.indexOf(code);
			List<Object> existObjList = DBCache.get(ind);
			List<Object> originalObjList = new ArrayList<Object>();
			originalObjList.addAll(existObjList);
			//如果merge功能开启
			if(null != mergeKeyList && !mergeKeyList.isEmpty()){
				if(isVerbose){
					System.out.println("[MergeInfo]REDUPLICATION FOUND!MERGE FUNCTION IS ON!TRY TO MERGE!");
				}
				for(String mergeKey:mergeKeyList){
					Object fieldData = matchObjList.get(key2IndexData.get(mergeKey).index);
					Object fieldDB = existObjList.get(key2IndexDB.get(mergeKey).index);
					//如果fieldData不为空，则合并进来
					if(null != fieldData){
						String fieldDataStr = fieldData.toString().toLowerCase().trim();
						//如果filedDB为空，直接赋值
						if(null == fieldDB){
							existObjList.set(key2IndexDB.get(mergeKey).index, fieldDataStr);
							if(isVerbose){
								System.out.println("[MergeInfo]MERGE SUCCESSFULLY:COVER " + fieldDataStr + " TO EMPTY!");
							}
							//更新stateList,若为0则改为2,否则不变
							stateList.set(ind, (0==stateList.get(ind))?2:stateList.get(ind));
							//否则判断两则值是否一致，若不一致则加到尾部
						}else {
							String fieldDBStr= fieldDB.toString().toLowerCase().trim();
							if(!fieldDBStr.contains(fieldDataStr)){
								String o =fieldDataStr + "||" + fieldDBStr;
								existObjList.set(key2IndexDB.get(mergeKey).index, o);
								//更新stateList,若为0则改为2,否则不变
								stateList.set(ind, (0==stateList.get(ind))?2:stateList.get(ind));
								if(isVerbose){
									System.out.println("[MergeInfo]MERGE SUCCESSFULLY:MERGE " + fieldDataStr + " AND " + fieldDBStr + " ----> " + o);
								}
							}else{
								if(isVerbose){
									System.out.println("[MergeInfo]NO NEED TO MERGE!SAME VALUE:" + fieldDataStr);
								}
							}
						}
						//否则没必要合并
					}else{
						if(isVerbose){
							System.out.println("[MergeInfo]NO NEED TO MERGE");
						}
					}
				}
			}else if(isVerbose){
				System.out.println("[MergeInfo]REDUPLICATION FOUND!MERGE FUNCTION IS OFF!");
			}
			//------------------------------------------------------
			//若replace功能开启
			if(null != replaceKeyList && !replaceKeyList.isEmpty()){
				if(isVerbose){
					System.out.println("[ReplaceInfo]REDUPLICATION FOUND!REPLACE FUNCTION IS ON!TRY TO REPLACE!");
				}
				for(String replaceKey:replaceKeyList){
					Object fieldData = matchObjList.get(key2IndexData.get(replaceKey).index);
					Object fieldDB = existObjList.get(key2IndexDB.get(replaceKey).index);
					//如果新来的数据d=fieldData不为空
					if(null != fieldData){
						//如果fieldDB为null，或者为空串，或者与fieldData不等的时候
						if(null == fieldDB || fieldDB.equals("") || !fieldData.equals(fieldDB)){
							existObjList.set(key2IndexDB.get(replaceKey).index, fieldData);
							//更新stateList,若为0则改为2,否则不变
							stateList.set(ind, (0==stateList.get(ind))?2:stateList.get(ind));
							if(isVerbose){
								System.out.println("[ReplaceInfo]REPLACE SUCCESSFULLY:REPLACE " + fieldDB + " WITH " + fieldData);
							}
						}else{
							if(isVerbose){
								System.out.println("[ReplaceInfo]DB: " + fieldDB + " NewData " + fieldData);
								System.out.println("[ReplaceInfo]VALUE IS THE SAME!NO NEED TO REPLACE!");
							}
						}
					}else{
						if(isVerbose){
							System.out.println("[ReplaceInfo]DB: " + fieldDB + " NewData " + fieldData);
							System.out.println("[ReplaceInfo]NEW VALUE IS NULL!CANNOT REPLACE!");
						}
					}
				}
			}else if(isVerbose){
				System.out.println("[ReplaceInfo]REDUPLICATION FOUND!REPLACE FUNCTION IS OFF!");
			}
			//------------------------------------------------------
			//打印操作前后的变化
			if(isVerbose){
			//matchKeyList,replaceKeyList,mergeKeyList合并
			List<String> printKeyList = new ArrayList<String>();
			for(String key:keyListDB){
				if(matchKeyList.contains(key) || replaceKeyList.contains(key) || mergeKeyList.contains(key)){
					printKeyList.add(key);
				}
			}
			for(String key : printKeyList) {
				if (key2IndexData.containsKey(key) && null != matchObjList.get(key2IndexData.get(key).index) ) {
					System.out.print(matchObjList.get(key2IndexData.get(key).index) + " ");
				} else {
					System.out.print("null ");
				}
			}
			System.out.println();
			
			for(String key : printKeyList) {
				if (key2IndexDB.containsKey(key) && null != originalObjList.get(key2IndexDB.get(key).index)) {
					System.out.print(originalObjList.get(key2IndexDB.get(key).index) + " ");
				} else {
					System.out.print("null ");
				}
			}
			System.out.println();
			}
		//如果不一样
		}else{
			// 将其code加进hashValue中
			hashValue.add(code);
			//待插入的数据
			stateList.add(1);
			// 将此matchObjList添加进DBCache
			List<Object> obL = new ArrayList<Object>();
			for (String key : keyListDB) {
				if (key2IndexData.containsKey(key)) {
					obL.add(matchObjList.get(key2IndexData.get(key).index));
				} else {
					Object o = null;
					obL.add(o);
				}
			}
			DBCache.add(obL);
			if(isVerbose) {
				// 打印accept的条目
				if (isVerbose) {
					System.out.println("[CheckInfo]NEW RECORD FOUND!ACCEPT!");
					for (Object ob : obL) {
						System.out.print(ob + " ");
					}
					System.out.println();
				}
			}
		}
	}
	public  static void printDBStructure() {
		if (null != keyListDB && null != key2IndexDB && !key2IndexDB.isEmpty()) {
			for (String key : keyListDB) {
				System.out.print(key + " ");
				System.out.print(key2IndexDB.get(key).index + " ");
				System.out.print(key2IndexDB.get(key).type + " ");
				if (null != DBCache && !DBCache.isEmpty()) {
					Object o = DBCache.get(0).get(keyListDB.indexOf(key));
					if (Integer.class.isInstance(o)) {
						System.out.print("Integer ");
					} else if (String.class.isInstance(o)) {
						System.out.print("String ");
					} else {
						System.out.print("Unknow Type!");
					}
				}
				System.out.println();
			}
		} else {
			System.out.println("[PrintInfo]KeyListDB IS EMPTY!");
		}
	}

	public static void printDBCache() {
		if (null != DBCache && !DBCache.isEmpty()) {
			for (List<Object> objList : DBCache) {
				for(String key:keyListDB){
					if(null != objList.get(key2IndexDB.get(key).index)){
						System.out.print(objList.get(key2IndexDB.get(key).index) + " ");
					}else{
						System.out.print("null ");
					}
				}
				System.out.println();
			}
		} else {
			System.out.println("[PrintInfo]DBCache IS EMPTY!");
		}
	}
	
	public  void printDataStructure() {
		if (null != keyListData && null != key2IndexData && !key2IndexData.isEmpty()) {
			for (String key : keyListData) {
				System.out.print(key + " ");
				System.out.print(key2IndexData.get(key).index + " ");
				System.out.print(key2IndexData.get(key).type + " ");
				if (null != dataCache && !dataCache.isEmpty()) {
					Object o = dataCache.get(0).get(keyListData.indexOf(key));
					if (Integer.class.isInstance(o)) {
						System.out.print("Integer ");
					} else if (String.class.isInstance(o)) {
						System.out.print("String ");
					} else {
						System.out.print("Unknow Type!");
					}
				}
				System.out.println();
			}
		} else {
			System.out.println("[PrintInfo]KeyList IS EMPTY!");
		}
	}

	public void printDataCache() {
		if (null != dataCache && !dataCache.isEmpty()) {
			for (List<Object> objList : dataCache) {
				for(String key:keyListData){
					if(null != objList.get(key2IndexData.get(key).index)){
						System.out.print(objList.get(key2IndexData.get(key).index) + " ");
					}else{
						System.out.print("null ");
					}
				}
				System.out.println();
			}
		} else {
			System.out.println("[PrintInfo]DBCache IS EMPTY!");
		}
	}

	public static List<String> getKeyListDB() {
		return AntiReduplicationCheck.keyListDB;
	}
	public static void setKeyListDB(List<String> keyListDB) {
		if(null != keyListDB && !keyListDB.isEmpty()){
			AntiReduplicationCheck.keyListDB.clear();
			for (String key : keyListDB) {
				AntiReduplicationCheck.keyListDB.add(key.toLowerCase().trim());
			}
		}else{
			System.out.println("[setKeyListDBErrorInfo]NEW keyListDB IS EMPTY!DO NOTHING!");
		}
	}

	public static Map<String, ColumnStructure> getKey2IndexDB() {
		return AntiReduplicationCheck.key2IndexDB;
	}
	public static void setKey2IndexDB(Map<String, ColumnStructure> key2IndexDB) {
		if(null != key2IndexDB && !key2IndexDB.isEmpty()){
			AntiReduplicationCheck.key2IndexDB.clear();
			for (String key : key2IndexDB.keySet()) {
				ColumnStructure cs = new ColumnStructure();
				cs.index = key2IndexDB.get(key).index;
				cs.type = key2IndexDB.get(key).type;
				AntiReduplicationCheck.key2IndexDB.put(key.toLowerCase().trim(), cs);
			}
		}else{
			System.out.println("[setKey2IndexDBErrorInfo]NEW key2IndexDB IS EMPTY!DO NOTHING!");
		}
	}
	
	public  static List<List<Object>> getDBCache() {
		return AntiReduplicationCheck.DBCache;
	}
	public static void setDBCache(List<List<Object>> dbCache) {
		if(null != dbCache){
			AntiReduplicationCheck.DBCache.clear();
			for (List<Object> objList : dbCache) {
				List<Object> objListTmp = new ArrayList<Object>();
				objListTmp.addAll(objList);
				AntiReduplicationCheck.DBCache.add(objListTmp);
			}
		}else{
			System.out.println("[setDBCacheErrorInfo]NEW dbCache IS EMPTY!DO NOTHING!");
		}
	}
	
	public static void setMergeKeyList(boolean isVerbose){
		setMergeKeyList(null, isVerbose);
	}
	
	public static void setMergeKeyList(List<String> mergeKeyList, boolean isVerbose) {
		List<String> mergeKeyListNew = General.inputKeyListFilterString(mergeKeyList, keyListDB, isVerbose);
		AntiReduplicationCheck.mergeKeyList.clear();
		if (mergeKeyListNew.isEmpty()) {
			System.out.println("[InitMergeListInfo]NO mergeKeyList DETECTED, SKIP THIS STEP");
		} else {
			System.out.print("[InitMergeListInfo]mergeKeyList DETECTED, USE FOLLOWING KEYS TO MERGE:");
			for(String key:mergeKeyListNew){
				System.out.print(key + " ");
			}
			System.out.println();
			for (String key : mergeKeyListNew) {
				AntiReduplicationCheck.mergeKeyList.add(key);
			}
		}
	}
	public static void setReplaceKeyList(String replaceKey){
		List<String> replaceKeyList= new ArrayList<String>();
		if(null != replaceKey){
			replaceKeyList.add(replaceKey);
		}
		setReplaceKeyList(replaceKeyList, false);
	}
	public static void setReplaceKeyList(String replaceKey, boolean isVerbose){
		List<String> replaceKeyList= new ArrayList<String>();
		if(null != replaceKey){
			replaceKeyList.add(replaceKey);
		}
		setReplaceKeyList(replaceKeyList, isVerbose);
	}
	public static void setReplaceKeyList(List<String> replaceKeyList){
		setReplaceKeyList(replaceKeyList, false);
	}
	public static void setReplaceKeyList(List<String> replaceKeyList, boolean isVerbose) {
		List<String> replaceKeyListNew = General.inputKeyListFilterString(replaceKeyList, keyListDB, isVerbose);
		AntiReduplicationCheck.replaceKeyList.clear();
		if (replaceKeyListNew.isEmpty()) {
			System.out.println("[InitReplaceListtInfo]NO replaceKeyList DETECTED, SKIP THIS STEP");
		} else {
			System.out.print("[InitReplaceListtInfo]replaceKeyList DETECTED, USE FOLLOWING KEYS TO REPLACE:");
			for(String key:replaceKeyListNew){
				System.out.print(key + " ");
			}
			System.out.println();
			for (String key : replaceKeyListNew) {
				AntiReduplicationCheck.replaceKeyList.add(key);
			}
		}
	}
	
	public static List<List<Object>> getInsertObjList() {
		return insertObjList;
	}
	
	public static List<List<Object>> getUpdateObjList() {
		return updateObjList;
	}
	public List<List<Object>> getDataCache() {
		return dataCache;
	}

	public List<String> getKeyListData() {
		return keyListData;
	}

	public Map<String, ColumnStructure> getKey2IndexData() {
		return key2IndexData;
	}
	
	public static void printStatistics(){
		System.out.println("[StatisticsInfo]TOTAL RECORDS ORIGINAL--->" + (DBCache.size()-insertTotal));
		System.out.println("[StatisticsInfo]TOTAL RECORDS COME--->" + comeTotoal);
		System.out.println("[StatisticsInfo]TOTAL RECORDS INSERT--->" + insertTotal);
		System.out.println("[StatisticsInfo]TOTAL RECORDS UPDATE--->" + updateTotal);
		System.out.println("[StatisticsInfo]TOTAL RECORDS REDUPLICATE--->" + dumpTotal);
		System.out.println("[StatisticsInfo]TOTAL RECORDS AT PRESENT--->" + DBCache.size());
	}
}
