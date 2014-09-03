package SchoolSearch.services.dao.graduatePublication;

/**
 * @author GCR
 */
import java.sql.BatchUpdateException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import SchoolSearch.services.dao.graduatePublication.model.GraduatePublication;
import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.utils.db.ConnectionPool;
import SchoolSearch.services.utils.db.DBConnection;

public class GraduatePublicationDao {

	String dbName = ConsistanceService.get("db.defaultDatabase");
	static ConnectionPool pool = ConnectionPool.getInstance();

	private static GraduatePublicationDao instance;

	public static GraduatePublicationDao getInstance() {
		if (null == instance) {
			instance = new GraduatePublicationDao();
		}
		return instance;
	}

	public static GraduatePublicationDao getIndependentInstance(String dbName) {
		return new GraduatePublicationDao(dbName);
	}

	public GraduatePublicationDao() {
	}

	private GraduatePublicationDao(String dbName) {
		this.dbName = dbName;
	}

	public void insert(GraduatePublication grapub) {
		String sql = "INSERT INTO " + dbName + ".graduatepublication(" + "department_id, res_id, author, " + //
				"au_id, au_dept, au_major, " + //
				"au_phone, au_email, rights_secret, " + //
				"latest_date, submit_date, degree, " + //
				"title_cn, title_en, tutor1_name, " + //
				"tutor1_address, tutor2_name, tutor2_address, " + //
				"keyword_cn, keyword_en, " + //
				"pagenum, abstract_cn,abstract_en, " + //
				"subject, fulltext_url, freetext_url, " + //
				"ti_spell, au_spell, degree_type, " + //
				"subject_name)" + //
				"VALUES(?,?,?,?,?,?,?,?,?,?, ?,?,?,?,?,?,?,?,?,?, ?,?,?,?,?,?,?,?,?,?)";

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			_construct(ps, grapub);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println(">>" + grapub.author);
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}

	public void insertBatch(List<GraduatePublication> grapubList) {
		String sql = "INSERT INTO " + dbName + ".graduatepublication(" + "department_id, res_id, author, " + //
				"au_id, au_dept, au_major, " + //
				"au_phone, au_email, rights_secret, " + //
				"latest_date, submit_date, degree, " + //
				"title_cn, title_en, tutor1_name, " + //
				"tutor1_address, tutor2_name, tutor2_address, " + //
				"keyword_cn, keyword_en, " + //
				"pagenum, abstract_cn,abstract_en, " + //
				"subject, fulltext_url, freetext_url, " + //
				"ti_spell, au_spell, degree_type, " + //
				"subject_name)" + //
				"VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for (GraduatePublication grapub : grapubList) {
				_construct(ps, grapub);
				ps.addBatch();
			}
			ps.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
		} catch (BatchUpdateException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			ConnectionPool.close(conn, ps);
		}
	}

	public List<GraduatePublication> walk(int start, int limit) {
		List<GraduatePublication> result = new ArrayList<GraduatePublication>();
		String sql = "SELECT id, department_id, res_id, author, " + //
				"au_id, au_dept, au_major, " + //
				"au_phone, au_email, rights_secret, " + //
				"latest_date, submit_date, degree, " + //
				"title_cn, title_en, tutor1_name, " + //
				"tutor1_address, tutor2_name, tutor2_address, " + //
				"keyword_cn, keyword_en, " + //
				"pagenum, abstract_cn,abstract_en, " + //
				"subject, fulltext_url, freetext_url, " + //
				"ti_spell, au_spell, degree_type, " + //
				"subject_name FROM " + dbName + ".graduatepublication WHERE id >= ? ORDER BY id LIMIT ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			// System.out.println(ps);
			ps.setInt(1, start);
			ps.setInt(2, limit);
			rs = ps.executeQuery();
			while (rs.next()) {
				result.add(_construct(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		return result;
	}

	public List<GraduatePublication> walkAll() {
		List<GraduatePublication> result = new ArrayList<GraduatePublication>();
		int startid = 0;
		int limit = 500;
		while (true) {
			List<GraduatePublication> walk = walk(startid, limit);
			if (null == walk || walk.size() == 0)
				break;
			result.addAll(walk);
			startid = walk.get(walk.size() - 1).id + 1;
		}
		return result;
	}

	public GraduatePublication getGraduatePublicationById(int id) {

		String sql = "SELECT id, department_id, res_id, author, " + //
				"au_id, au_dept, au_major, " + //
				"au_phone, au_email, rights_secret, " + //
				"latest_date, submit_date, degree, " + //
				"title_cn, title_en, tutor1_name, " + //
				"tutor1_address, tutor2_name, tutor2_address, " + //
				"keyword_cn, keyword_en, " + //
				"pagenum, abstract_cn,abstract_en, " + //
				"subject, fulltext_url, freetext_url, " + //
				"ti_spell, au_spell, degree_type, " + //
				"subject_name FROM " + dbName + ".graduatepublication WHERE id = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		GraduatePublication result = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				result = _construct(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		return result;
	}

	public List<GraduatePublication> getGraduatePublicationsByIds(List<Integer> ids) {

		String sql = "SELECT id, department_id, res_id, author, " + //
				"au_id, au_dept, au_major, " + //
				"au_phone, au_email, rights_secret, " + //
				"latest_date, submit_date, degree, " + //
				"title_cn, title_en, tutor1_name, " + //
				"tutor1_address, tutor2_name, tutor2_address, " + //
				"keyword_cn, keyword_en, " + //
				"pagenum, abstract_cn,abstract_en, " + //
				"subject, fulltext_url, freetext_url, " + //
				"ti_spell, au_spell, degree_type, " + //
				"subject_name FROM " + dbName + ".graduatepublication WHERE id IN ";

		List<GraduatePublication> result = new ArrayList<GraduatePublication>();
		StringBuilder sb = new StringBuilder();
		sb.append(sql);
		sb.append("(");
		for (int i = 0; i < ids.size(); i++) {
			sb.append("?");
			if (i < ids.size() - 1) {
				sb.append(",");
			}
		}
		sb.append(")");

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sb.toString());
			for (int i = 0; i < ids.size(); i++) {
				ps.setInt(i + 1, ids.get(i));
			}
			System.out.println("~" + ps);
			rs = ps.executeQuery();
			while (rs.next()) {
				result.add(_construct(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		return result;
	}

	public List<GraduatePublication> getGraduatePublicationByName(String name) {

		List<GraduatePublication> result = new ArrayList<GraduatePublication>();

		String sql = "SELECT id, department_id, res_id, author, " + //
				"au_id, au_dept, au_major, " + //
				"au_phone, au_email, rights_secret, " + //
				"latest_date, submit_date, degree, " + //
				"title_cn, title_en, tutor1_name, " + //
				"tutor1_address, tutor2_name, tutor2_address, " + //
				"keyword_cn, keyword_en, " + //
				"pagenum, abstract_cn,abstract_en, " + //
				"subject, fulltext_url, freetext_url, " + //
				"ti_spell, au_spell, degree_type, " + //
				"subject_name FROM " + dbName + ".graduatepublication WHERE author = ? OR tutor1_name = ? OR tutor2_name = ?";

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, name);
			ps.setString(2, name);
			ps.setString(3, name);
			rs = ps.executeQuery();
			while (rs.next()) {
				result.add(_construct(rs));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		return result;
	}

	private GraduatePublication _construct(ResultSet rs) throws SQLException {
		GraduatePublication result = new GraduatePublication();
		result.id = rs.getInt("id");
		result.department_id = rs.getInt("department_id");
		result.res_id = rs.getString("res_id");
		result.author = rs.getString("author");
		result.au_id = rs.getString("au_id");
		result.au_dept = rs.getString("au_dept");
		result.au_major = rs.getString("au_major");
		result.au_phone = rs.getString("au_phone");
		result.au_email = rs.getString("au_email");
		result.rights_secret = rs.getString("rights_secret");
		result.latest_date = rs.getString("latest_date");
		result.submit_date = rs.getString("submit_date");
		result.degree = rs.getString("degree");
		result.title_cn = rs.getString("title_cn");
		result.title_en = rs.getString("title_en");
		result.tutor1_name = rs.getString("tutor1_name");
		result.tutor1_address = rs.getString("tutor1_address");
		result.tutor2_name = rs.getString("tutor2_name");
		result.tutor2_address = rs.getString("tutor2_address");
		result.keyword_cn = rs.getString("keyword_cn");
		result.keyword_en = rs.getString("keyword_en");
		result.pagenum = rs.getInt("pagenum");
		result.abstract_cn = rs.getString("abstract_cn");
		result.abstract_en = rs.getString("abstract_en");
		result.subject = rs.getString("subject");
		result.fulltext_url = rs.getString("fulltext_url");
		result.freetext_url = rs.getString("freetext_url");
		result.ti_spell = rs.getString("ti_spell");
		result.au_spell = rs.getString("au_spell");
		result.degree_type = rs.getString("degree_type");
		result.subject_name = rs.getString("subject_name");
		return result;
	}

	private void _construct(PreparedStatement ps, GraduatePublication p) throws SQLException {
		int count = 0;
		__safeSetInt(ps, ++count, p.department_id);
		__safeSetString(ps, ++count, p.res_id);
		__safeSetString(ps, ++count, p.author);
		__safeSetString(ps, ++count, p.au_id);
		__safeSetString(ps, ++count, p.au_dept);
		__safeSetString(ps, ++count, p.au_major);
		__safeSetString(ps, ++count, p.au_phone);
		__safeSetString(ps, ++count, p.au_email);
		__safeSetString(ps, ++count, p.rights_secret);
		__safeSetString(ps, ++count, p.latest_date);
		__safeSetString(ps, ++count, p.submit_date);
		__safeSetString(ps, ++count, p.degree);
		__safeSetString(ps, ++count, p.title_cn);
		__safeSetString(ps, ++count, p.title_en);
		__safeSetString(ps, ++count, p.tutor1_name);
		__safeSetString(ps, ++count, p.tutor1_address);
		__safeSetString(ps, ++count, p.tutor2_name);
		__safeSetString(ps, ++count, p.tutor2_address);
		__safeSetString(ps, ++count, p.keyword_cn);
		__safeSetString(ps, ++count, p.keyword_en);
		__safeSetInt(ps, ++count, p.pagenum);
		__safeSetString(ps, ++count, p.abstract_cn);
		__safeSetString(ps, ++count, p.abstract_en);
		__safeSetString(ps, ++count, p.subject);
		__safeSetString(ps, ++count, p.fulltext_url);
		__safeSetString(ps, ++count, p.freetext_url);
		__safeSetString(ps, ++count, p.ti_spell);
		__safeSetString(ps, ++count, p.au_spell);
		__safeSetString(ps, ++count, p.degree_type);
		__safeSetString(ps, ++count, p.subject_name);
	}

	private void __safeSetString(PreparedStatement ps, int parameterIndex, String value) throws SQLException {
		if (null == value)
			ps.setNull(parameterIndex, Types.VARCHAR);
		else
			ps.setString(parameterIndex, value);
	}

	private void __safeSetInt(PreparedStatement ps, int parameterIndex, Integer value) throws SQLException {
		if (value == null)
			ps.setNull(parameterIndex, Types.INTEGER);
		else
			ps.setInt(parameterIndex, value);
	}

}
