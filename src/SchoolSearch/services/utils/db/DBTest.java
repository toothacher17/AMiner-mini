package SchoolSearch.services.utils.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBTest {
	public static void main(String args[]) {
		ConnectionPool pool = ConnectionPool.getInstance();
		DBConnection conn = pool.getConnection();
		
		
		String sql = "SELECT * FROM publication LIMIT 100";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			while(rs.next()) {
				System.out.println(rs.getString("title"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		
		
		
	}
}
