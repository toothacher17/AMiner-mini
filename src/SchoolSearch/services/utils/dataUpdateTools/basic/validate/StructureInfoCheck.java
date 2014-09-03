package SchoolSearch.services.utils.dataUpdateTools.basic.validate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import SchoolSearch.services.utils.dataUpdateTools.basic.manipulate.General;
import SchoolSearch.services.utils.dataUpdateTools.basic.read.ReadFromDataBase;
import SchoolSearch.services.utils.dataUpdateTools.model.ColumnStructure;
import SchoolSearch.services.utils.db.ConnectionPool;

public class StructureInfoCheck {
	private String dbName;
	private String tableName;
	
	//待插入的数据Cache信息
	private List<String> keyList;
	private Map<String, ColumnStructure> key2Index;
	private List<List<Object>> dataCache;

	private static StructureInfoCheck instance;
	static ConnectionPool pool = ConnectionPool.getInstance();

	public static StructureInfoCheck getInstance(String dbName, String tableName, List<String> keyList, Map<String,ColumnStructure> key2Index, List<List<Object>> dataCache){
		if (null == instance) {
			instance = new StructureInfoCheck(dbName, tableName, keyList, key2Index, dataCache);
		}
		return instance;
	}
	
	public static StructureInfoCheck getIndependentInstance(String dbName, String tableName, List<String> keyList, Map<String,ColumnStructure> key2Index, List<List<Object>> dataCache){
		return new StructureInfoCheck(dbName, tableName, keyList, key2Index, dataCache);
	}
	
	public StructureInfoCheck(String dbName, String tableName, List<String> keyList, Map<String,ColumnStructure> key2Index, List<List<Object>> dataCache){
		this.dbName = dbName;
		this.tableName = tableName;
		
		
		this.keyList = new ArrayList<String>();
		
		this.key2Index = new HashMap<String, ColumnStructure>();
		
		this.dataCache = new ArrayList<List<Object>>();
		
//		//初始化待插入的Cache信息
//		for(String key:keyList){
//			this.keyList.add(key.toLowerCase().trim());
//		}
//		for(String key:key2Index.keySet()){
//			ColumnStructure cs = new ColumnStructure();
//			cs.index = key2Index.get(key).index;
//			cs.type = key2Index.get(key).type;
//			this.key2Index.put(key.toLowerCase().trim(),cs);
//		}
		this.keyList.addAll(keyList);
		this.key2Index.putAll(key2Index);
		//初始化dataCache,值传递
		for(List<Object> objListI:dataCache){
			List<Object> objList = new ArrayList<Object>();
			objList.addAll(objListI);
			this.dataCache.add(objList);
		}
	}
	public boolean checkAndTransfer(String oldKey, String newKey, boolean isVerbose){
		Map<String, String> transferMap = new HashMap<String, String>();
		transferMap.put(newKey, oldKey);
		return checkAndTransfer(transferMap, isVerbose);
	}
	//启动transfer，倘若无错误，返回true，否则返回false
	public boolean checkAndTransfer(){
		return checkAndTransfer(null,true);
	}
	public boolean checkAndTransfer(Map<String, String> inverseTransferMap){
		return checkAndTransfer(inverseTransferMap,true);
	}
	public boolean checkAndTransfer(boolean isVerbose){
		return checkAndTransfer(null,isVerbose);
	}
	public boolean checkAndTransfer(Map<String, String> inverseTransferMap, boolean isVerbose){
		boolean isSuccess = true;
		//获取待插入表的信息
		ReadFromDataBase rd = ReadFromDataBase.getIndependentInstance(dbName, tableName);
		rd.getWithIdLimited(null, 1, 1);
		List<String> keyListDB = new ArrayList<String>();
		Map<String, ColumnStructure> key2IndexDB = new HashMap<String, ColumnStructure>();
		
		keyListDB.addAll(rd.getKeyList());
		key2IndexDB.putAll(rd.getKey2Index());
		Map<String, ColumnStructure> key2IndexTmp= new HashMap<String, ColumnStructure>();
		key2IndexTmp.putAll(key2Index);
		//从DB的keyList循环
		for(String key:keyListDB){
			//若map为空或者map中不存在key,则表示无变化
			if(null == inverseTransferMap || !inverseTransferMap.containsKey(key)){
				if(key2Index.containsKey(key)){
					ColumnStructure cs = new ColumnStructure();
					//索引号与原表索引号一致
					cs.index = key2Index.get(key).index;
					//类型变成数据库真实类型
					cs.type = key2IndexDB.get(key).type;
					
//					key2IndexNew.put(key, cs);
					key2Index.remove(key);
					key2Index.put(key,cs);
					if(isVerbose){
						System.out.print("[HeaderTransferInfo]KEY DO NOT CHANGE:" + key + "---->" + key);
					}
				}else{
					if(isVerbose){
						System.out.print("[HeaderTransferInfo]FIND NEW KEY:EMPTY---->" + key);
					}
				}
			//map不为空，且map中存在key，原始key2Index的keySet中也存在key，则必然要进行转换
			}else if(key2Index.containsKey(inverseTransferMap.get(key))){
				ColumnStructure cs = new ColumnStructure();
				//索引号与原表索引号一致
				cs.index = key2Index.get(inverseTransferMap.get(key)).index;
				//类型变成数据库真实类型
				cs.type = key2IndexDB.get(key).type;
//				key2IndexNew.put(key, cs);
				key2Index.remove(key);
				key2Index.put(key, cs);
				if(isVerbose){
					System.out.print("[HeaderTransferInfo]KEY CHANGE:" + inverseTransferMap.get(key) + "---->" + key);
				}
				
				//修改keyList
				int ind = keyList.indexOf(inverseTransferMap.get(key));
				keyList.set(ind, key);
			//否则就是map表中的声明转换的key在原始key2Index的keySet中找不到，出错
			}else{
				System.out.println("[HeaderTransferErrorInfo]TRANSFERMAP IS NOT COMPLETED:CAN NOT FIND THE KEY " + key + " IN Key2IndexData");
				isSuccess = false;
				return isSuccess;
			}
			
			//存在转换则更新dataCache
			if(null != key2IndexTmp.get(key)){
				int index = key2IndexTmp.get(key).index;
				//若类型由char变成了int
				if(key2IndexDB.get(key).type.equalsIgnoreCase("int") && !key2IndexTmp.get(key).type.equalsIgnoreCase("int")){
					if(isVerbose){
						System.out.println(" (TYPE CHANGE:" + key2IndexTmp.get(key).type + "---->" + key2IndexDB.get(key).type + ")");
					}
					for(List<Object> objList:dataCache){
						try{
							objList.set(index, Integer.parseInt((String)objList.get(index)));
						}
						catch(NumberFormatException e){
							objList.set(index, 0);
						}
					}
					//如果类型由int变成了char
				}else if(!key2IndexDB.get(key).type.equalsIgnoreCase("int") && key2IndexTmp.get(key).type.equalsIgnoreCase("int")){
					if(isVerbose){
						System.out.println(" (TYPE CHANGE:" + key2IndexTmp.get(key).type + "---->" + key2IndexDB.get(key).type + ")");
					}
					for(List<Object> objList:dataCache){
						objList.set(index, ((Integer)objList.get(index)).toString());
					}
				}else{
					if(isVerbose){
						System.out.println(" (TYPE:" + key2IndexTmp.get(key).type + ")");
					}
				}
			}else{
				if(isVerbose){
					System.out.println(" (TYPE:" + key2IndexDB.get(key).type + ")");
				}
			}
		}
		
		List<String> removeKeyList = new ArrayList<String>();
		for(String key:keyList){
			//查看目的数据表中是否存在key字段
			if(!key2IndexDB.containsKey(key)){
				removeKeyList.add(key);
			}
		}
		
		//调用general中的方法
		General gl = new General(keyList, key2Index, dataCache);
		isSuccess = gl.removeColumnsByKeyList(removeKeyList, isVerbose);
		if(isSuccess){
			keyList.clear();
			keyList.addAll(gl.getKeyList());
			
			key2Index.clear();
			key2Index.putAll(gl.getKey2Index());
			
			dataCache.clear();
			for(List<Object> objListI:gl.getDataCache()){
				List<Object> objList = new ArrayList<Object>();
				objList.addAll(objListI);
				dataCache.add(objList);
			}
		}
		return isSuccess;
	}

	public List<String> getKeyList() {
		return keyList;
	}

	public Map<String, ColumnStructure> getKey2Index() {
		return key2Index;
	}

	public List<List<Object>> getDataCache() {
		return dataCache;
	}
	
	public void printStructure() {
		if (null != keyList && null != key2Index && !key2Index.isEmpty()) {
			for (String key : keyList) {
				System.out.print(key + " ");
				System.out.print(key2Index.get(key).index + " ");
				System.out.print(key2Index.get(key).type + " ");
				if (null != dataCache && !dataCache.isEmpty()){
					Object o = dataCache.get(0).get(keyList.indexOf(key));
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
}
