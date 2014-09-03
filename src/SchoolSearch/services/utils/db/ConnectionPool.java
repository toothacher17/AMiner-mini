package SchoolSearch.services.utils.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

/*
 * 
 */
public class ConnectionPool {
	private static String url;
	private static String encoding;
	private static String drivername;
	private static String username;
	private static String password;
	private static Vector<DBConnection> pools = new Vector<DBConnection>();
	private static int curCount = 0;
	private static int maxNumber = 50;
	private static int minNumber = 1;
	
	private static ConnectionPool instance = null;
	
	public static ConnectionPool getInstance() {
		if(instance == null) {
			instance = new ConnectionPool();
		}
		return instance;
	}
	
	
	private ConnectionPool() {
		ReadDBProperties readProperties = new ReadDBProperties();
		Properties properties = readProperties.getProperties();
		drivername = properties.getProperty("db.driver"); 
		username = properties.getProperty("db.user"); 
		password = properties.getProperty("db.pass"); 
		url = properties.getProperty("db.url");
		try {
			maxNumber = Integer.parseInt(properties.getProperty("db.pool.max_pool_size")); 
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		try {
			minNumber = Integer.parseInt(properties.getProperty("db.pool.min_pool_size")); // 从资源文件中读取最小连接数
			if (minNumber > maxNumber)
				minNumber = maxNumber;
		} catch (Exception e) {
			e.printStackTrace();
		}
		InitConnections();
	}

	
	
	/**
	 * FunName InitConnections Description 初始化连接池pools
	 */
	private void InitConnections() {
		for (int i = 0; i < minNumber; i++) {
			DBConnection conn = new DBConnection(drivername, url, username, password);
			pools.add(conn);
		}
		curCount = pools.size();
	}

	/**
	 * FunName createConnection Description 创建连接句柄
	 */
	private static void createConnection() {
		if(maxNumber > curCount) {
			DBConnection conn = new DBConnection(drivername, url, username, password);
			pools.add(conn);
			curCount++;
		}
	}

	public DBConnection getConnection() {
		// 遍历连接池，寻找一个可用连接
		Iterator<DBConnection> it = pools.iterator();
		while (it.hasNext()) {
			// 从连接池中得到一个连接
			DBConnection dbConnection = it.next();
			// 判断是否可用
			if (!dbConnection.isUsed()) {
				// 得到连接名柄
				Connection connection = dbConnection.getConnection();
				try {
					// 如果连接名柄可用，从连接池中返回，否则销毁
					if (connection != null && !connection.isClosed()) {
						dbConnection.setUsed(true);
						curCount++;
						return dbConnection;
					} else {
						pools.remove(dbConnection);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// 遍历完连接池也找不到1个可用连接创建新的连接
		// 判断是否连接池中的连接数量是否还可增长，可增长则由连接池创建连接，否则返回额外的连接名柄
		if (curCount <= 0 && pools.size() < maxNumber) {
			createConnection();
		} else if (pools.size() >= maxNumber) {
			return new DBConnection(drivername, url, username, password, false);
		}
		return new DBConnection(drivername, url, username, password, false);
	}

	/**
	 * FunName close Description 连接句柄dbConnection放回连接池
	 * 
	 * @param dbConnection
	 */
	public static void close(DBConnection dbConnection) {
		// 如果是连接池的连接，由连接池回收,否则自动销毁
		if (dbConnection.isInPools()) {
			// 设置连接处于空闲状态
			dbConnection.setUsed(false);
			dbConnection.closeStm();
			curCount++;
		} else
			dbConnection.closeConn();
	}

	public static void close(DBConnection conn, PreparedStatement ps, ResultSet rs) { 
		if(null!=rs) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(null != ps) {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		close(conn);
	}
	
	public static void close(DBConnection conn, PreparedStatement ps) { 
		if(null != ps) {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		close(conn);
	}
	
	/*
	 * Description 关闭连接池
	 */
	public static void closePools() {
		Iterator<DBConnection> it = pools.iterator();
		while (it.hasNext()) {
			DBConnection dbConnection = it.next();
			dbConnection.closeConn();
		}
		pools.removeAllElements();
		if (pools.size() == 0) {
			System.out.println("连接池关闭成功!");
		}
	}

	public static int getCurrentConnectionNumber() {
		return curCount;
	}

	public static int getConnCount() {
		return pools.size();
	}

}