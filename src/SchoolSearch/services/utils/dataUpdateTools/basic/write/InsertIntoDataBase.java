package SchoolSearch.services.utils.dataUpdateTools.basic.write;

/**
 * @author CX
 *
 */
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import SchoolSearch.services.utils.db.ConnectionPool;
import SchoolSearch.services.utils.db.DBConnection;
import SchoolSearch.services.utils.dataUpdateTools.model.ColumnStructure;

public class InsertIntoDataBase {

	private String dbName;
	private String tableName;
	
	//待插入的数据Cache信息
	private List<String> keyList;
	private Map<String, ColumnStructure> key2Index;
	private List<List<Object>> dataCache;
	private boolean isFillId;
	private static InsertIntoDataBase instance;
	static ConnectionPool pool = ConnectionPool.getInstance();

	public static InsertIntoDataBase getInstance(String dbName, String tableName, List<String> keyList, Map<String,ColumnStructure> key2Index, List<List<Object>> dataCache){
		if (null == instance) {
			instance = new InsertIntoDataBase(dbName, tableName, keyList, key2Index, dataCache);
		}
		return instance;
	}
	
	public static InsertIntoDataBase getInstance(String dbName, String tableName, String columnName, List<String> valueList){
		if (null == instance) {
			instance = new InsertIntoDataBase(dbName, tableName, columnName, valueList);
		}
		return instance;
	}
	public static InsertIntoDataBase getIndependentInstance(String dbName, String tableName, List<String> keyList, Map<String,ColumnStructure> key2Index, List<List<Object>> dataCache, boolean isFillId){
		return new InsertIntoDataBase(dbName, tableName, keyList, key2Index, dataCache, isFillId);
	}
	public static InsertIntoDataBase getIndependentInstance(String dbName, String tableName, List<String> keyList, Map<String,ColumnStructure> key2Index, List<List<Object>> dataCache){
		return new InsertIntoDataBase(dbName, tableName, keyList, key2Index, dataCache);
	}
	
	public static InsertIntoDataBase getIndependentInstance(String dbName, String tableName, String columnName, List<String> valueList){
		return new InsertIntoDataBase(dbName, tableName, columnName, valueList);
	}
	public static <T> InsertIntoDataBase getIndependentInstance(String dbName, String tableName, T t, boolean isFillId){
		return new InsertIntoDataBase(dbName, tableName, t, isFillId);
	}
	public static <T> InsertIntoDataBase getIndependentInstance(String dbName, String tableName, List<T> tList, boolean isFillId){
		return new InsertIntoDataBase(dbName, tableName, tList, isFillId);
	}
	public  <T> InsertIntoDataBase(String dbName, String tableName, List<T> tList, boolean isFillId){
		this.isFillId = isFillId;
		this.dbName = dbName;
		this.tableName = tableName;
		
		this.keyList = new ArrayList<String>();
		this.key2Index = new HashMap<String, ColumnStructure>();
		this.dataCache = new ArrayList<List<Object>>();
		if(null != tList && !tList.isEmpty()){
			Field [] fields = tList.get(0).getClass().getFields();
			for(T t:tList){
				if(null != t){
					int i=0;
					List<Object> objList = new ArrayList<Object>();
					for(Field f:fields){
						if(!key2Index.containsKey(f.getName())){
							keyList.add(f.getName());
							ColumnStructure cs = new ColumnStructure();
							cs.index =i++;
							cs.type = f.getType().getSimpleName();
							key2Index.put(f.getName(), cs);
						}
						try {
							objList.add(f.get(t));
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
					dataCache.add(objList);
				}
			}
		}
	}
	
	
	public  <T> InsertIntoDataBase(String dbName, String tableName, T t, boolean isFillId){
		this.isFillId = isFillId;
		this.dbName = dbName;
		this.tableName = tableName;
		
		this.keyList = new ArrayList<String>();
		this.key2Index = new HashMap<String, ColumnStructure>();
		this.dataCache = new ArrayList<List<Object>>();
		if(t != null){
			Field [] fields = t.getClass().getFields();
			int i=0;
			List<Object> objList = new ArrayList<Object>();
			for(Field f:fields){
				keyList.add(f.getName());
				ColumnStructure cs = new ColumnStructure();
				cs.index =i++;
				cs.type = f.getType().getSimpleName();
				key2Index.put(f.getName(), cs);
				try {
					objList.add(f.get(t));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			dataCache.add(objList);
		}else{
			System.out.println("[InsertIntoDataBaseErrorInfo]INPUT PARAMETER IS NULL!" );
		}
		
	}
	public InsertIntoDataBase(String dbName, String tableName, List<String> keyList, Map<String,ColumnStructure> key2Index, List<List<Object>> dataCache){
		this.isFillId = false;
		this.dbName = dbName;
		this.tableName = tableName;
		
		this.keyList = new ArrayList<String>();
		this.key2Index = new HashMap<String, ColumnStructure>();
		this.dataCache = new ArrayList<List<Object>>();
		
		//初始化待插入的Cache信息
		this.keyList.addAll(keyList);
		this.key2Index.putAll(key2Index);
		//初始化dataCache,值传递
		for(List<Object> objListI:dataCache){
			List<Object> objList = new ArrayList<Object>();
			objList.addAll(objListI);
			this.dataCache.add(objList);
		}
	}
	
	public InsertIntoDataBase(String dbName, String tableName, List<String> keyList, Map<String,ColumnStructure> key2Index, List<List<Object>> dataCache, boolean isFillId){
		
		this.isFillId = isFillId;
		this.dbName = dbName;
		this.tableName = tableName;
		
		this.keyList = new ArrayList<String>();
		this.key2Index = new HashMap<String, ColumnStructure>();
		this.dataCache = new ArrayList<List<Object>>();
		
		//初始化待插入的Cache信息
		this.keyList.addAll(keyList);
		this.key2Index.putAll(key2Index);
		//初始化dataCache,值传递
		for(List<Object> objListI:dataCache){
			List<Object> objList = new ArrayList<Object>();
			objList.addAll(objListI);
			this.dataCache.add(objList);
		}
		
	}
	public InsertIntoDataBase(String dbName, String tableName, String columnName, List<String> valueList){
		this.isFillId = false;
		this.dbName = dbName;
		this.tableName = tableName;
		
		this.keyList = new ArrayList<String>();
		this.key2Index = new HashMap<String, ColumnStructure>();
		this.dataCache = new ArrayList<List<Object>>();
		
		//初始化待插入的Cache信息
		this.keyList.add(columnName);
		
		ColumnStructure cs = new ColumnStructure();
		cs.index=0;
		cs.type = "char";
		this.key2Index.put(columnName, cs);
		
		//初始化dataCache,值传递
		for(String value:valueList){
			List<Object> objListTmp = new ArrayList<Object>();
			objListTmp.add(value);
			dataCache.add(objListTmp);
		}
	}
	
	public void insertOneRecord(List<Object > objList){
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO " + dbName + "." + tableName);
		sql.append("(");
		//若包含主键id，则跳过
		int i = (keyList.contains("id")&&!isFillId)?1:0;
		while(i<keyList.size()){
			sql.append(_safeSurroundings(keyList.get(i)) + ",");
			i++;
		}
		sql = sql.deleteCharAt(sql.length()-1);
		sql.append(") VALUES(");
		
		i = (keyList.contains("id")&&!isFillId)?1:0;
		while(i<keyList.size()){
			sql.append("?,");
			i++;
		}
		sql = sql.deleteCharAt(sql.length()-1);
		sql.append(");");
		
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql.toString());
			_construct(ps, objList);
			System.out.println(ps.toString());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}
//	public void insertBatchRecordWithoutNullValue(){
//		if(dataCache.isEmpty()){
//			System.out.println("[InsertBatchInfo]dataCache IS EMPTY!DO NOTHING!");
//			return;
//		}else{
//			StringBuilder sql = new StringBuilder();
//			sql.append("INSERT INTO " + dbName + "." + tableName);
//			sql.append("(");
//			//若不填id,则跳过
//			int i = (keyList.contains("id")&&!isFillId)?1:0;
//			while(i<keyList.size()){
//				sql.append(_safeSurroundings(keyList.get(i)) + ",");
//				i++;
//			}
//			sql = sql.deleteCharAt(sql.length()-1);
//			sql.append(") VALUES(");
//			
//			i = (keyList.contains("id")&&!isFillId)?1:0;
//			while(i<keyList.size()){
//				sql.append("?,");
//				i++;
//			}
//			sql = sql.deleteCharAt(sql.length()-1);
//			sql.append(");");
//			
//			DBConnection conn = pool.getConnection();
//			PreparedStatement ps = null;
//			try {
//				conn.setAutoCommit(false);
//				ps = conn.prepareStatement(sql.toString());
//				for (List<Object> objList:dataCache) {
//					if(_constructWithoutNull(ps, objList)){
//						ps.addBatch();
//					}
//				}
//				System.out.println(ps.toString());
//				ps.executeBatch();
//				conn.commit();
//				conn.setAutoCommit(true);
//			} catch (SQLException e) {
//				e.printStackTrace();
//			} finally {
//				ConnectionPool.close(conn, ps);
//			}
//		}
//	}
	public void insertBatchRecord(){
		if(dataCache.isEmpty()){
			System.out.println("[InsertBatchInfo]dataCache IS EMPTY!DO NOTHING!");
			return;
		}else{
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO " + dbName + "." + tableName);
			sql.append("(");
			//若不填id,则跳过
			int i = (keyList.contains("id")&&!isFillId)?1:0;
			while(i<keyList.size()){
				sql.append(_safeSurroundings(keyList.get(i)) + ",");
				i++;
			}
			sql = sql.deleteCharAt(sql.length()-1);
			sql.append(") VALUES(");
			
			i = (keyList.contains("id")&&!isFillId)?1:0;
			while(i<keyList.size()){
				sql.append("?,");
				i++;
			}
			sql = sql.deleteCharAt(sql.length()-1);
			sql.append(");");
			
			DBConnection conn = pool.getConnection();
			PreparedStatement ps = null;
			try {
				conn.setAutoCommit(false);
				ps = conn.prepareStatement(sql.toString());
				for (List<Object> objList:dataCache) {
					_construct(ps, objList);
					ps.addBatch();
				}
				System.out.println(ps.toString());
				ps.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				ConnectionPool.close(conn, ps);
			}
		}
	}
	
	private void _construct(PreparedStatement ps, List<Object> objList) throws SQLException{
		//若不填id,则跳过
		if(keyList.contains("id")&&!isFillId){
			for(int i=1; i<keyList.size(); i++){
				_safeSetValue(ps, i, objList.get(key2Index.get(keyList.get(i)).index), key2Index.get(keyList.get(i)).type);
			}
		}else{
			for(int i=0; i<keyList.size(); i++){
				_safeSetValue(ps, i+1, objList.get(key2Index.get(keyList.get(i)).index), key2Index.get(keyList.get(i)).type);
			}
		}
	}
//	private boolean _constructWithoutNull(PreparedStatement ps, List<Object> objList) throws SQLException{
//		//objList中只要出现null，则跳过
//		boolean hasNull = false;
//		for(String key:keyList){
//			if(!key.equalsIgnoreCase("id") && null == objList.get(key2Index.get(key).index)){
//				hasNull = true;
//				//打印
//				System.out.println("[InserBatchRecordWithoutNull]NULL RECORD FOUND!SKIP THIS!");
//				System.out.println(objList);
//				break;
//			}
//		}
//		if(!hasNull){
//			if(keyList.contains("id")&&!isFillId){
//				for(int i=1; i<keyList.size(); i++){
//					_safeSetValue(ps, i, objList.get(key2Index.get(keyList.get(i)).index), key2Index.get(keyList.get(i)).type);
//				}
//			}else{
//				for(int i=0; i<keyList.size(); i++){
//					_safeSetValue(ps, i+1, objList.get(key2Index.get(keyList.get(i)).index), key2Index.get(keyList.get(i)).type);
//				}
//			}
//		}
//		return !hasNull;
//	}
	private void _safeSetValue(PreparedStatement ps, int parameterIndex, Object value, String type) throws SQLException{
		if(null == value){
			if(String.class.isInstance(value))
				ps.setNull(parameterIndex, Types.VARCHAR);
			else
				ps.setNull(parameterIndex, Types.INTEGER);
		}else{
			if(String.class.isInstance(value))
				ps.setString(parameterIndex, (String)value);
			else
				ps.setInt(parameterIndex, (Integer)value);
		}
	}
	
	private String _safeSurroundings(String str) {
		return "`" + str + "`";
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

	public boolean isFillId() {
		return isFillId;
	}

	public void printStructure() {
		if (null != keyList && null != key2Index) {
			for (String key : keyList) {
				System.out.print(key2Index.get(key).index + " ");
				System.out.print(key + " ");
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

