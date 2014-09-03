package SchoolSearch.services.utils.dataUpdateTools.backup;

/**
 * @author CX
 *
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import SchoolSearch.services.utils.db.ConnectionPool;
import SchoolSearch.services.utils.dataUpdateTools.model.ColumnStructure;

public class PreWriteToDataBase {
	private List<List<Object>> dataCache;
	private List<List<Object>> DBCache;
	
	private static PreWriteToDataBase instance;
	
	static ConnectionPool pool = ConnectionPool.getInstance();

	public static PreWriteToDataBase getInstance(List<List<Object>> dataCache, List<List<Object>> DBCache){
		if (null == instance) {
			instance = new PreWriteToDataBase(dataCache, DBCache);
		}
		return instance;
	}
	
	public static PreWriteToDataBase getIndependentInstance(List<List<Object>> dataCache, List<List<Object>> DBCache){
		return new PreWriteToDataBase(dataCache, DBCache);
	}
	
	public PreWriteToDataBase(List<List<Object>> dataCache, List<List<Object>> DBCache){
		
		this.dataCache = new ArrayList<List<Object>>();
		this.DBCache = new ArrayList<List<Object>>();
		
		for(List<Object> objListI:dataCache){
			List<Object> objList = new ArrayList<Object>();
			objList.addAll(objListI);
			this.dataCache.add(objList);
		}
		
		for(List<Object> objListI:DBCache){
			List<Object> objList = new ArrayList<Object>();
			objList.addAll(objListI);
			this.DBCache.add(objList);
		}
	}
	
	public boolean validation(List<String> matchList, Map<String,ColumnStructure> key2IndexData, Map<String,ColumnStructure> key2IndexDB){
		boolean result = true;
		for(String match:matchList){
			//将DBCache中的match字段的列取出来
			List<Object> objL = new ArrayList<Object>();
			for(List<Object> objList:DBCache){
				objL.add(objList.get(key2IndexDB.get(match).index));
			}
			//检查是否有字段已经存在于DBCache的match字段中
			for(List<Object> objList:dataCache){
				if(key2IndexData.keySet().contains(match) && objL.contains(objList.get(key2IndexData.get(match).index))){
					result =false;
					break;
				}
			}
		}
//		}
		return result;
	}

	
	public static void main(String[] args) {
	}
}

