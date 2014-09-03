package SchoolSearch.services.utils.dataUpdateTools.backup;

/**
 * @author CX
 *
 */
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import SchoolSearch.services.utils.dataUpdateTools.basic.TableStructureInfo;
import SchoolSearch.services.utils.dataUpdateTools.model.ColumnStructure;
import SchoolSearch.services.utils.dataUpdateTools.model.TableStructure;
import SchoolSearch.services.utils.db.ConnectionPool;
import SchoolSearch.services.utils.db.DBConnection;

public class CommonDao {

	private String dbName;
	private String tableName;
	private Map<String, ColumnStructure> key2IndexAll;
	private List<String> keyList;
	private Map<String, ColumnStructure> key2IndexSelected;
	private boolean withId = false;
	private List<List<Object>> dataList;

	private List<TableStructure> tableStructureList;
	private static CommonDao instance;
	static ConnectionPool pool = ConnectionPool.getInstance();

	public static CommonDao getInstance(String dbName, String tableName) {
		if (null == instance) {
			instance = new CommonDao(dbName, tableName);
		}
		return instance;
	}

	public static CommonDao getInstance(String dbName, String tableName, List<String> keyList) {
		if (null == instance) {
			instance = new CommonDao(dbName, tableName, keyList);
		}
		return instance;
	}

	public static CommonDao getInstance(String dbName, String tableName, List<String> keyList, Map<String,ColumnStructure> key2Index, List<List<Object>> dataList){
		if (null == instance) {
			instance = new CommonDao(dbName, tableName, keyList, key2Index, dataList);
		}
		return instance;
	}
	
	public static CommonDao getIndependentInstance(String dbName, String tableName) {
		return new CommonDao(dbName, tableName);
	}

	public static CommonDao getIndependentInstance(String dbName,String tableName, List<String> keyList) {
		return new CommonDao(dbName, tableName, keyList);
	}
	
	public static CommonDao getIndependentInstance(String dbName, String tableName, List<String> keyList, Map<String,ColumnStructure> key2Index, List<List<Object>> dataList){
		return new CommonDao(dbName, tableName, keyList, key2Index, dataList);
	}
	
	//读取所有字段
	public CommonDao(String dbName, String tableName) {
		key2IndexAll = new HashMap<String, ColumnStructure>();
		dataList = new ArrayList<List<Object>>();
		tableStructureList = new ArrayList<TableStructure>();
		this.dbName = dbName;
		this.tableName = tableName;
		this.withId = true;

		// 查询数据库返回表结构
		initalTableStuctureList(dbName, tableName);
		// 默认使用所有的字段
		this.keyList = new ArrayList<String>();
		for (TableStructure ts : tableStructureList) {
			this.keyList.add(ts.field);
		}
		// 初始化表中所有字段
		initalKey2IndexMap();

		// 初始化用户选择字段
		this.key2IndexSelected = this.key2IndexAll;
	}
	
	//读取特定字段
	public CommonDao(String dbName, String tableName, List<String> keyList) {
		key2IndexAll = new HashMap<String, ColumnStructure>();
		key2IndexSelected = new HashMap<String, ColumnStructure>();
		dataList = new ArrayList<List<Object>>();
		tableStructureList = new ArrayList<TableStructure>();
		this.dbName = dbName;
		this.tableName = tableName;
		// 查询数据库返回表结构
		initalTableStuctureList(dbName, tableName);

		// 判断是否含有id，若没有则添加上
		this.keyList = new ArrayList<String>();
		if (!keyList.contains("id")) {
			this.keyList.add("id");
		} else {
			this.withId = true;
		}
		this.keyList.addAll(keyList);

		// 初始化表中所有字段
		initalKey2IndexMap();

		// 初始化用户选择字段
		initalKey2IndexSelectedMap();
	}

	//写入数据库
	public CommonDao(String dbName, String tableName, List<String> keyList, Map<String,ColumnStructure> key2Index, List<List<Object>> dataList){
		this.key2IndexSelected = new HashMap<String, ColumnStructure>();
		this.dataList = new ArrayList<List<Object>>();
		this.keyList = new ArrayList<String>();
		this.dbName = dbName;
		this.tableName = tableName;
		
		this.keyList.addAll(keyList);
		this.key2IndexSelected.putAll(key2Index);
		this.dataList.addAll(dataList);
	}
	
	private void initalTableStuctureList(String dbName, String tableName) {
		this.tableStructureList = TableStructureInfo.getIndependentInstance(dbName,tableName).getTableStructureList();
	}

	private void initalKey2IndexMap() {
		if (null != tableStructureList) {
			int size = tableStructureList.size();
			for (int j = 0; j < size; j++) {
				ColumnStructure cs = new ColumnStructure();
				cs.type = tableStructureList.get(j).type;
				cs.index = j;
				key2IndexAll.put(tableStructureList.get(j).field, cs);
			}
		} else {
			System.out.println("tableStructure is empty!");
		}
	}

	private void initalKey2IndexSelectedMap() {
		int i = 0;
		key2IndexSelected.clear();
		for (String key : keyList) {
			if (key2IndexAll.containsKey(key)) {
				// 重建index
				ColumnStructure cs = new ColumnStructure();
				cs.index = i++;
				cs.type = key2IndexAll.get(key).type;
				key2IndexSelected.put(key, cs);
			} else {
				System.out.println("[keyValue](" + key + ") not found");
			}
		}
	}

	// 根据startId和endId一次性从数据库fetch一批数据
	public List<List<Object>> walk(int startId, int endId) {

		List<List<Object>> dataList = new ArrayList<List<Object>>();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ");
		// 根据keyList决定选择哪些字段
		for (String key : keyList) {
			sql.append(_safeSurroundings(key) + ",");
		}
		sql = sql.deleteCharAt(sql.length() - 1);
		sql.append(" FROM " + dbName + "." + tableName
				+ " WHERE id >= ? AND id <= ? ORDER BY id ");
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sql.toString());
			ps.setInt(1, startId);
			ps.setInt(2, endId);
			rs = ps.executeQuery();
			while (rs.next()) {
				dataList.add(_construct(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		return dataList;
	}

	// 根据keyList按startId和endId选择条目，startId不小于0，endId设为-1表示直到结尾
	public List<List<Object>> selectByKeyListWithIdLimited(int startId, int endId) {
		int limit = 500;
		if (-1 == endId) {
			endId = Integer.MAX_VALUE;
			;
		}
		if (endId < startId) {
			int temp = startId;
			endId = temp;
			startId = endId;
		}
		// 保证startId有意义
		startId = startId < 1 ? 1 : startId;
		int pauseId;
		while (true) {
			pauseId = (endId - startId + 1 < limit) ? endId : startId + limit;
			List<List<Object>> walk = walk(startId, pauseId);
			if (null == walk || walk.size() == 0)
				break;
			dataList.addAll(walk);
			startId = (Integer) walk.get(walk.size() - 1).get(0) + 1;
		}
		if (!withId) {
			List<String> removeList = new ArrayList<String>();
			removeList.add("id");
			removeColumnByKeyList(removeList);
		}
		return getDataList();
	}

	
	public void insertOneRecord(List<Object> objList){
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO " + dbName + "." + tableName);
		sql.append("(");
		//id为第一个字段，为关键字，跳过
		for(int i=1; i<keyList.size();i++){
			sql.append(_safeSurroundings(keyList.get(i)) + ",");
		}
		sql = sql.deleteCharAt(sql.length()-1);
		sql.append(") VALUES(");
		for(int i=1; i<keyList.size();i++){
			sql.append("?,");
		}
		sql = sql.deleteCharAt(sql.length()-1);
		sql.append(");");
		
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql.toString());
			_construct(ps, objList);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}
	
	public void insertBatchRecord(){
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO " + dbName + "." + tableName);
		sql.append("(");
		//id为第一个字段，为关键字，跳过
		for(int i=1; i<keyList.size();i++){
			sql.append(_safeSurroundings(keyList.get(i)) + ",");
		}
		sql = sql.deleteCharAt(sql.length()-1);
		sql.append(") VALUES(");
		for(int i=1; i<keyList.size();i++){
			sql.append("?,");
		}
		sql = sql.deleteCharAt(sql.length()-1);
		sql.append(");");
		
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;

		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql.toString());
			for (List<Object> objList:dataList) {
				_construct(ps, objList);
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}
	
	public List<List<Object>> getDataList() {
		return dataList;
	}

	public Map<String, ColumnStructure> getKey2IndexAllMap() {
		return key2IndexAll;
	}

	public Map<String, ColumnStructure> getKey2IndexSelectedMap() {
		return key2IndexSelected;
	}

	public List<String> getKeyList(){
		return keyList;
	}
	
	private void _construct(PreparedStatement ps, List<Object> objList) throws SQLException{
		for(int i=1; i<keyList.size(); i++){
			_safeSetValue(ps, i, objList.get(i), key2IndexSelected.get(keyList.get(i)).type);
		}
	}
	
	private List<Object> _construct(ResultSet rs) throws SQLException {
		List<Object> result = new ArrayList<Object>();
		for (String key : keyList) {
			ColumnStructure cs = key2IndexSelected.get(key);
			if (null != cs) {
				String type = cs.type;
				if (type.equalsIgnoreCase("int"))
					result.add(rs.getInt(key));
				else
					result.add(rs.getString(key));
			} else {
				System.out.println("[keyValue]" + key
						+ " is not in key2IndexSelectedMap!");
			}
		}
		return result;
	}

	private void _safeSetValue(PreparedStatement ps, int parameterIndex, Object value, String type) throws SQLException{
		if(null == value){
			if(type.equalsIgnoreCase("int"))
				ps.setNull(parameterIndex, Types.INTEGER);
			else
				ps.setNull(parameterIndex, Types.BIGINT);
		}else{
			if(type.equalsIgnoreCase("int"))
				ps.setInt(parameterIndex, (Integer)value);
			else
				ps.setString(parameterIndex, (String)value);
		}
	}
	
	private String _safeSurroundings(String str) {
		return "`" + str + "`";
	}

	private boolean removeColumnByKeyList(List<String> removeKeyList) {
		boolean result = true;
		for(String key:removeKeyList){
			if(!keyList.contains(key)){
				result = false;
				break;
			}
		}
		if (result) {
			keyList.removeAll(removeKeyList);
			for (List<Object> objList : dataList) {
				for (String key : removeKeyList) {
					objList.remove(key2IndexSelected.get(key).index);
				}
			}
			initalKey2IndexSelectedMap();
		} else {
			System.out.println("remove keyList error");
		}
		return result;
	}

	public void printDataList() {
		if (null != dataList) {
			for (List<Object> objList : dataList) {
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
		if (null != key2IndexAll && !key2IndexAll.isEmpty()) {
			for (String str : key2IndexAll.keySet()) {
				System.out.println(key2IndexAll.get(str).index + " "
						+ key2IndexAll.get(str).type);
			}
		} else {
			System.out.println("key2IndexAll is empty");
		}
	}

	public void printKey2IndexSelectedMap() {
		if (null != key2IndexSelected) {
			for (String str : key2IndexSelected.keySet()) {
				System.out.println(key2IndexSelected.get(str).index + " "
						+ key2IndexSelected.get(str).type);
			}
		} else {
			System.out.println("key2IndexSelected is empty");
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
		printKey2IndexSelectedMap();
		printDataList();
	}

	
	public static void main(String[] args) {
		 CommonDao cd1 = new CommonDao("schoolsearch", "publication");
//		CommonDao cd1 = new CommonDao("schoolsearch", "department",new ArrayList<String>(Arrays.asList("id","school_id","department_name")));
		cd1.selectByKeyListWithIdLimited(1,1);
		cd1.printKeyList();
//		cd1.printDataList();
//		
//		CommonDao cd2 = new CommonDao("schoolsearch", "school",new ArrayList<String>(Arrays.asList("id")));
//		cd2.selectByKeyListWithIdLimited(1,20);
//		cd2.printDataList();
//		cd.printDataList();
//		cd.removeColumnByKeyList(new ArrayList<String>(Arrays.asList("department_name")));
//		cd.printDataList();
//		cd.printKeyList();
//		cd.printKey2IndexMap();
//		cd.printKey2IndexSelectedMap();
	}
}
