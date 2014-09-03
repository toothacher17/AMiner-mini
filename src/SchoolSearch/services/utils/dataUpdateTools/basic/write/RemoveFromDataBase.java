package SchoolSearch.services.utils.dataUpdateTools.basic.write;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import SchoolSearch.services.utils.dataUpdateTools.basic.read.ReadFromDataBase;
import SchoolSearch.services.utils.dataUpdateTools.model.ColumnStructure;
import SchoolSearch.services.utils.db.ConnectionPool;
import SchoolSearch.services.utils.db.DBConnection;

public class RemoveFromDataBase {
private static RemoveFromDataBase instance;
	
	private String dbName;
	private String tableName;
	
	private List<String> keyList;
	private Map<String, ColumnStructure> key2Index;
	
	static ConnectionPool pool = ConnectionPool.getInstance();
	public static RemoveFromDataBase getInstance(String dbName, String tableName){
		if(null == instance)
			instance = new RemoveFromDataBase(dbName, tableName);
		return instance;
	}
	
	public static RemoveFromDataBase getIndependentInstance(String dbName, String tableName){
		return new RemoveFromDataBase(dbName, tableName);
	}
	
	public RemoveFromDataBase(String dbName, String tableName){
		this.dbName = dbName;
		this.tableName = tableName;
		
		this.keyList = new ArrayList<String>();
		this.key2Index = new HashMap<String, ColumnStructure>();
		
		ReadFromDataBase rd = ReadFromDataBase.getIndependentInstance(dbName, tableName);
		rd.getWithIdLimited(null, 1, 1);
		this.keyList.addAll(rd.getKeyList());
		this.key2Index.putAll(rd.getKey2Index());
	}
	
	public void truncate(){
		StringBuilder sql = new StringBuilder();
		sql.append("TRUNCATE TABLE " + dbName + "." + tableName);
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		
		try {
			ps = conn.prepareStatement(sql.toString());
			System.out.println(ps.toString());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			ConnectionPool.close(conn, ps);
		}
	}
	
	
	public static void main(String[] args) {
//		RemoveFromDataBase rm = RemoveFromDataBase.getInstance("test", "publication");
//		rm.truncate();
	}
	
}
