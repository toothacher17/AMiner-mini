package SchoolSearch.services.utils.dataUpdateTools.basic.write;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import SchoolSearch.services.utils.dataUpdateTools.basic.read.ReadFromDataBase;
import SchoolSearch.services.utils.dataUpdateTools.model.ColumnStructure;
import SchoolSearch.services.utils.db.ConnectionPool;
import SchoolSearch.services.utils.db.DBConnection;

public class UpdateToDataBase {
	private static UpdateToDataBase instance;
	
	private String dbName;
	private String tableName;
	
	private List<String> keyList;
	private Map<String, ColumnStructure> key2Index;
	
	static ConnectionPool pool = ConnectionPool.getInstance();
	public static UpdateToDataBase getInstance(String dbName, String tableName){
		if(null == instance)
			instance = new UpdateToDataBase(dbName, tableName);
		return instance;
	}
	
	public static UpdateToDataBase getIndependentInstance(String dbName, String tableName){
		return new UpdateToDataBase(dbName, tableName);
	}
	
	public UpdateToDataBase(String dbName, String tableName){
		this.dbName = dbName;
		this.tableName = tableName;
		
		this.keyList = new ArrayList<String>();
		this.key2Index = new HashMap<String, ColumnStructure>();
		
		ReadFromDataBase rd = ReadFromDataBase.getIndependentInstance(dbName, tableName);
		rd.getWithIdLimited(null, 1, 1);
		this.keyList.addAll(rd.getKeyList());
		this.key2Index.putAll(rd.getKey2Index());
		
	}
	
	
	public boolean updateSingle(Map<String, ? extends Object> newValue, Map<String, ? extends List<? extends Object>> condition){
		boolean isSuccess = true;
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE " + dbName + "." + tableName + " SET ");
		//记录newValueKey出现顺序
		List<String> newValueKeyList = new ArrayList<String>();
		for(String key:newValue.keySet()){
			sql.append(_safeSurroundings(key) + "=?, ");
			//转化为小写
			newValueKeyList.add(key.toLowerCase());
		}
		sql.delete(sql.length()-2, sql.length());
		sql.append(" WHERE ");
		//记录condition出现顺序
		List<String> conditionKeyList = new ArrayList<String>();
		for (String key : condition.keySet()) {
			//转化为小写
			conditionKeyList.add(key.toLowerCase());
			sql.append(_safeSurroundings(key) + " IN(");
			for (int i = 0; i < condition.get(key).size(); i++) {
				sql.append("?,");
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(") AND ");
		}
		
		sql = sql.delete(sql.length() - 5, sql.length());
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		
		try {
			int count = 1;
			ps = conn.prepareStatement(sql.toString());
			
			for(String key:newValueKeyList){
				String type = key2Index.get(key).type;
				if(type.equalsIgnoreCase("int")){
					if(Integer.class.isInstance(newValue.get(key)))
						ps.setInt(count++, (Integer) newValue.get(key));
					else{
						ps.setInt(count++, Integer.valueOf((String)(newValue.get(key))));
					}
				}else{
					if(Integer.class.isInstance(newValue.get(key)))
						ps.setString(count++, ((Integer) newValue.get(key)).toString());
					else{
						ps.setString(count++, (String)(newValue.get(key)));
					}
				}
			}
			
			for(String key:conditionKeyList){
				String type = key2Index.get(key).type;
				if(type.equalsIgnoreCase("int")){
					for(int i=0; i<condition.get(key).size(); i++){
						if(Integer.class.isInstance(condition.get(key).get(i)))
							ps.setInt(count++, (Integer) condition.get(key).get(i));
						else{
							ps.setInt(count++, Integer.valueOf((String)(condition.get(key).get(i))));
						}
					}
				}else{
					for(int i=0; i<condition.get(key).size(); i++){
						if(Integer.class.isInstance(condition.get(key).get(i)))
							ps.setString(count++, ((Integer)condition.get(key).get(i)).toString());
						else{
							ps.setString(count++, (String)(condition.get(key).get(i)));
						}
					}
				}
			}
			
			System.out.println(ps.toString());
			if(!ps.execute()){
				isSuccess =false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
		return isSuccess;
	}
	
	public boolean updateSingel(String keyName, Object value, String conditionKey, Object condtionValue){
		Map<String ,Object> newValue = new HashMap<String, Object>();
		newValue.put(keyName, value);
		Map<String ,List<Object>> condition = new HashMap<String, List<Object>>();
		List<Object> objList = new ArrayList<Object>();
		objList.add(condtionValue);
		condition.put(conditionKey, objList);
		return updateSingle(newValue, condition);
	}
	
	//根据id来更新
	public boolean updateBatch(List<String> keyListData, Map<String, ColumnStructure> key2IndexData, List<List<Object>> DataCache, String accordingKey){
		boolean isSuccess = true;
		if(DataCache.isEmpty() || null == accordingKey || accordingKey.isEmpty()){
			System.out.println("[UpdateBatchInfo]dataCache IS EMPTY!DO NOTHING!");
		}else{
			Map<String,List<Object>> newValueBatch = new HashMap<String, List<Object>>();
			Map<String,List<Object>> conditionBatch = new HashMap<String, List<Object>>();
			for(String key:keyListData){
				if(key.equalsIgnoreCase(accordingKey)){
					List<Object> ObjList= new ArrayList<Object>();
					for(int i=0; i<DataCache.size(); i++){
						ObjList.add(DataCache.get(i).get(key2IndexData.get(key).index));
					}
					conditionBatch.put(key, ObjList);
				}else{
					List<Object> ObjList= new ArrayList<Object>();
					for(int i=0; i<DataCache.size(); i++){
						ObjList.add(DataCache.get(i).get(key2IndexData.get(key).index));
					}
					newValueBatch.put(key, ObjList);
				}
			}
			isSuccess = updateBatch(newValueBatch, conditionBatch);
		}
		return isSuccess;
	}
	
	public boolean updateBatch(Map<String, ? extends List<? extends Object>> newValueBatch, Map<String, ? extends List<? extends Object>> conditionBatch){
		boolean isSuccess = true;
		List<String> keyList = new ArrayList<String>();
		for(String key:newValueBatch.keySet()){
			keyList.add(key.toLowerCase());
		}
		
		for(int i=0; i<newValueBatch.get(keyList.get(0)).size(); i++){
			Map<String, Object>  newValue = new HashMap<String, Object>();
			
			for(String key:newValueBatch.keySet()){
				newValue.put(key.toLowerCase(), newValueBatch.get(key).get(i));
			}
			
			Map<String, List<Object>> condition = new HashMap<String, List<Object>>();
			for(String key:conditionBatch.keySet()){
				List<Object> objList= new ArrayList<Object>();
				objList.add(conditionBatch.get(key).get(i));
				condition.put(key.toLowerCase(), objList);
			}
			if(!updateSingle(newValue, condition)){
				isSuccess = false;
			}
		}
		return isSuccess;
	}
	private String _safeSurroundings(String str) {
		return "`" + str + "`";
	}
}
