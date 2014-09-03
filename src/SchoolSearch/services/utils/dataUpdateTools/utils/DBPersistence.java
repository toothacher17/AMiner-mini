package SchoolSearch.services.utils.dataUpdateTools.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import SchoolSearch.services.utils.dataUpdateTools.basic.read.ReadFromDataBase;
import SchoolSearch.services.utils.dataUpdateTools.model.ColumnStructure;

/**
 * 把数据库的某张表格持久化到本地
 * @author CXLYC
 *
 */
public class DBPersistence {
	private static DBPersistence instance;
	private File dirPath;
	
	public static DBPersistence getInstance(String dirStr){
		if(null == instance)
			instance = new DBPersistence(dirStr);
		return instance;
	}
	
	public static DBPersistence getIndependentInstance(String dirStr){
		return new DBPersistence(dirStr);
	}
	
	public DBPersistence(String dirStr) {
		dirPath = new File(dirStr);
		if(!dirPath.exists()){
			dirPath.mkdirs();
		}
//			System.out.println("[ERROR]FILE " + dirStr + " IS NOT DIR!PLEASE CHECK!");
//		}else if(!dirPath.exists()){
//		}
	}
	
	public void persistDB(String dbName, String tableName){
		if(dirPath.isDirectory()){
			Long  pre = System.currentTimeMillis();
			ReadFromDataBase rd = ReadFromDataBase.getIndependentInstance(dbName, tableName);
			rd.getAllData();
			IOUtils.saveObject(new File(dirPath.toString() + File.separator + "keyList.txt"), rd.getKeyList());
			IOUtils.saveObject(new File(dirPath.toString() + File.separator + "key2Index.txt"), rd.getKey2Index());
			IOUtils.saveObject(new File(dirPath.toString() + File.separator +  "dataCache.txt"), rd.getDataCache());
			Long after = System.currentTimeMillis();
			System.out.println("TOTALLY COST: " + (after-pre)/1000 + " s");
		}
	}
	
	public List<String> getKeyList(){
		if(dirPath.isDirectory()){
			File keyList = new File(dirPath.toString() + File.separator + "keyList.txt");
			if(keyList.exists()){
				return (ArrayList<String>)IOUtils.loadObject(keyList, ArrayList.class);
			}
		}
		return null;
	}
	
	public Map<String, ColumnStructure> getKey2Index(){
		if(dirPath.isDirectory()){
			File key2Index = new File(dirPath.toString() + File.separator + "key2Index.txt");
			if(key2Index.exists()){
				return (HashMap<String,ColumnStructure>)IOUtils.loadObject(key2Index, HashMap.class);
			}
		}
		return null;
	}
	
	public List<List<Object>> getDataCache(){
		if(dirPath.isDirectory()){
			File dataCache = new File(dirPath.toString() + File.separator + "dataCache.txt");
			if(dataCache.exists()){
				return (ArrayList<List<Object>>)IOUtils.loadObject(dataCache, ArrayList.class);
			}
		}
		return null;
	}
}
 