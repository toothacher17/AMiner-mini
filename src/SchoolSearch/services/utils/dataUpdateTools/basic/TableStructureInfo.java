package SchoolSearch.services.utils.dataUpdateTools.basic;
/**
 * @author CX
 */
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import SchoolSearch.services.utils.db.ConnectionPool;
import SchoolSearch.services.utils.db.DBConnection;

import SchoolSearch.services.utils.dataUpdateTools.model.TableStructure;

public class TableStructureInfo {
	private String dbName;
	private String tableName;
	private List<TableStructure> tableStuctureList;

	private static TableStructureInfo instance;
	static ConnectionPool pool = ConnectionPool.getInstance();

	/*
	public static TableStructureInfo getInstance(String tableName) {
		if (null == instance) {
			instance = new TableStructureInfo(tableName);
		}
		return instance;
	}*/

	public static TableStructureInfo getInstance(String dbName, String tableName) {
		if (null == instance) {
			instance = new TableStructureInfo(dbName, tableName);
		}
		return instance;
	}
	
	/*
	public static TableStructureInfo getIndependentInstance(String tableName) {
		return new TableStructureInfo(tableName);
	}*/
	
	
	public static TableStructureInfo getIndependentInstance(String dbName,
			String tableName) {
		return new TableStructureInfo(dbName, tableName);
	}

	/*
	private TableStructureInfo(String tableName) {
		this.dbName = "schoolsearch";
		this.tableName = tableName;
	}*/

	public TableStructureInfo(String dbName, String tableName) {
		this.dbName = dbName;
		this.tableName = tableName;
	}

	public void run() {
		tableStuctureList = new ArrayList<TableStructure>();
		
		String sql = "SHOW COLUMNS FROM " + dbName + "." + tableName + ";";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				tableStuctureList.add(_construct(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
	}

	public List<TableStructure> getTableStructureList() {
		run();
		return tableStuctureList;
	}

	public void printAll() {
		System.out
				.println("Field      Type       Null       Key      Default       Extra");
		if (null != tableStuctureList) {
			for (TableStructure ts : tableStuctureList) {
				System.out.println(ts.field + "     "+ ts.type +  "     " +ts.isNull + "      " + ts.isKey
						+ "      " + ts.defaultValue + "      " + ts.extra);
			}
		} else {
			System.out.println("nothing return;");
		}
	}

	private TableStructure _construct(ResultSet rs) throws SQLException {
		TableStructure result = new TableStructure();
		result.field = rs.getString("field").toLowerCase();
		result.type = typeConvert(rs.getString("Type")).toLowerCase();
		result.isNull = rs.getString("null").toLowerCase().contains("yes") ? true:false;
		result.isKey = rs.getString("key").toLowerCase().contains("pri") ? true:false;
		result.defaultValue = (null == rs.getString("default"))?null:rs.getString("default").toLowerCase();
		result.extra = (null == rs.getString("extra"))?null:rs.getString("extra").toLowerCase();
		return result;
	}

	public String typeConvert(String str){
		String strLower = str.toLowerCase();
		if(strLower.matches("int\\(\\d+\\).*") || strLower.contains("int"))
			return "int";
		else if(strLower.matches("varchar\\(\\d+\\).*") || strLower.contains("char"))
			return "char";
		else if (strLower.matches("timestamp"))
			return "timestamp";
		else 
			return "text";
	}
	
	public static void main(String[] args) {
		TableStructureInfo tb = new TableStructureInfo("cache","editlog");
		tb.run();
		tb.printAll();
	}

}
