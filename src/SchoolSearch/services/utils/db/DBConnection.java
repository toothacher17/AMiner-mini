package SchoolSearch.services.utils.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DBConnection {
	private Connection conn = null; // 数据库连接句柄
	private Statement stm = null;
	private boolean used = false; // 该链接是否正被使用
	private boolean inPools = true; // 是否在连接池中
	private ArrayList<String> batchSql = null; // 批量处理sql

	public DBConnection() {
	}

	protected DBConnection(String drivername, String url, String username, String password) {
		try {
			Class.forName(drivername);
			conn = DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Description 构造方法
	 * 
	 * @param drivername
	 *            数据库驱动名称
	 * @param username
	 *            用户名
	 * @parma password 密码
	 * @param url
	 *            数据库地址
	 * @param inPools
	 *            是否在缓冲池里
	 */
	protected DBConnection(String drivername, String url, String username, String password, boolean inPools) {
		try {
			Class.forName(drivername).newInstance();
			conn = DriverManager.getConnection(url, username, password);
			this.inPools = inPools;
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.inPools = inPools;
	}

	/**
	 * 关闭连接
	 */
	public void closeConn() {
		try {
			if (conn != null && !conn.isClosed()) {
				try {
					conn.close();
					conn = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the conn
	 */
	public Connection getConnection() {
		return conn;
	}

	/**
	 * FunName setAutoCommit Description 设置自动提交
	 * 
	 * @param bool
	 *            是否自动提交
	 */
	public void setAutoCommit(boolean bool) {
		try {
			if (conn.getAutoCommit() != bool) {
				conn.setAutoCommit(bool);
				closeStm();
				createStatement();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void commit() throws SQLException {
		conn.commit();
	}

	/**
	 * @return the used
	 */
	protected boolean isUsed() {
		return used;
	}

	/**
	 * @param used
	 *            the used to set
	 */
	protected void setUsed(boolean used) {
		this.used = used;
	}

	/**
	 * @return the inPools
	 */
	protected boolean isInPools() {
		return inPools;
	}

	/**
	 * @param inPools
	 *            the inPools to set
	 */
	protected void setInPools(boolean inPools) {
		this.inPools = inPools;
	}
	
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return this.conn.prepareStatement(sql);
	}

	/**
	 * FunName execute Description 执行sql语句
	 */
	public boolean execute(String sql) {
		boolean success = false;
		try {
			createStatement();
			success = stm.execute(sql);
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		}
		return success;
	}

	/**
	 * FunName addBatch Description 添加批量处理sql语句
	 */
	public void addBatch(String sql) {
		if (batchSql == null)
			batchSql = new ArrayList<String>();
		batchSql.add(sql);
	}

	/**
	 * FunName batch Description 批量执行sql语句
	 */
	public boolean batch() {
		try {
			createStatement();
			setAutoCommit(false);
			stm.execute("START TRANSACTION;");
			for (int i = 0; i < batchSql.size(); i++) {
				stm.addBatch(batchSql.get(i));
			}
			stm.executeBatch();
			conn.commit();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception el) {
				el.printStackTrace();
			}
			e.printStackTrace();
		}
		batchSql = null;
		setAutoCommit(true);
		return true;
	}

	/**
	 * FunName query Description 执行查询语句
	 * 
	 * @param sql
	 *            查询语句
	 * @return 查询结果集ResultSet
	 */
	public ResultSet query(String sql) {
		ResultSet rs = null;
		try {
			createStatement();
			rs = stm.executeQuery(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * FunName createStatement Description 创建Statement
	 */
	private void createStatement() throws Exception {
		if (stm == null) {
			stm = conn.createStatement();
		}
	}

	/**
	 * FunName closeStm Description 关闭Statement
	 */
	protected void closeStm() {
		try {
			if (stm != null)
				stm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		stm = null;
	}

	/**
	 * FunName closeRs Description 关闭Result
	 */
	public void closeRs(ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

