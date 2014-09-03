package SchoolSearch.services.dao.publication;

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

import SchoolSearch.services.dao.publication.model.Publication;
import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.utils.db.ConnectionPool;
import SchoolSearch.services.utils.db.DBConnection;

public class PublicationDao {

	String dbName = ConsistanceService.get("db.defaultDatabase");
	static ConnectionPool pool = ConnectionPool.getInstance();

	private static PublicationDao instance;

	public static PublicationDao getInstance() {
		if (null == instance) {
			instance = new PublicationDao();
		}
		return instance;
	}

	public static PublicationDao getIndependentInstance(String dbName) {
		return new PublicationDao(dbName);
	}

	public PublicationDao() {
	}

	private PublicationDao(String dbName) {
		this.dbName = dbName;
	}

	public void insert(Publication pub) {
		String sql = "INSERT INTO " + dbName + ".publication(" + "collection, author_affiliations, authors, " + //
				"date_conferencedate_en, date_issuedMD_en, year, " + //
				"abstract, description_reference_en, description_sponsorship, " + //
				"format_page_en, identifier_beginpage_en, identifier_doi_en, " + //
				"identifier_endpage_en, identifier_inspeclocal_en, identifier_isbn, " + //
				"identifier_isilocal_en, identifier_issn_en, identifier_issue, " + //
				"identifier_page, url, " + //
				"identifier_volume_en, language, publisher_city_en, " + //
				"publisher_location_en, publisher, relation_conferencelocation, " + //
				"relation_conferencename, relation_conferencesponsorship_zh, relation_isIndexedBy_en, " + //
				"jconf, subject_clc_en, subject_discipline_en, keywords, " + //
				"title_alternative, title, " + //
				"type, institute_key)" + //
				"VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			_construct(ps, pub);
			ps.executeUpdate();
		} catch (SQLException e) {
			// System.out.println(">>"+pub.dc_contributor_author);
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}

	public void insertBatch(List<Publication> pubList) {
		String sql = "INSERT INTO " + dbName + ".publication(" + "collection, author_affiliations, authors, " + //
				"date_conferencedate_en, date_issuedMD_en, year, " + //
				"abstract, description_reference_en, description_sponsorship, " + //
				"format_page_en, identifier_beginpage_en, identifier_doi_en, " + //
				"identifier_endpage_en, identifier_inspeclocal_en, identifier_isbn, " + //
				"identifier_isilocal_en, identifier_issn_en, identifier_issue, " + //
				"identifier_page, url, " + //
				"identifier_volume_en, language, publisher_city_en, " + //
				"publisher_location_en, publisher, relation_conferencelocation, " + //
				"relation_conferencename, relation_conferencesponsorship_zh, relation_isIndexedBy_en, " + //
				"jconf, subject_clc_en, subject_discipline_en, keywords, " + //
				"title_alternative, title, " + //
				"type, institute_key)" + //
				"VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for (Publication pub : pubList) {
				_construct(ps, pub);
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

	public List<Publication> walk(int start, int limit) {

		List<Publication> result = new ArrayList<Publication>();

		String sql = "SELECT id, collection, author_affiliations, authors, " + //
				"date_conferencedate_en, date_issuedMD_en, year, " + //
				"abstract, description_reference_en, description_sponsorship, " + //
				"format_page_en, identifier_beginpage_en, identifier_doi_en, " + //
				"identifier_endpage_en, identifier_inspeclocal_en, identifier_isbn, " + //
				"identifier_isilocal_en, identifier_issn_en, identifier_issue, " + //
				"identifier_page, url, " + //
				"identifier_volume_en, language, publisher_city_en, " + //
				"publisher_location_en, publisher, relation_conferencelocation, " + //
				"relation_conferencename, relation_conferencesponsorship_zh, relation_isIndexedBy_en, " + //
				"jconf, subject_clc_en, subject_discipline_en, keywords, " + //
				"title_alternative, title, " + //
				"type, institute_key FROM " + dbName + //
				".publication WHERE id >= ? ORDER BY id LIMIT ?";

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

	public List<Publication> walkAll() {
		List<Publication> result = new ArrayList<Publication>();
		int startid = 0;
		int limit = 500;
		while (true) {
			List<Publication> walk = walk(startid, limit);
			if (null == walk || walk.size() == 0)
				break;
			result.addAll(walk);
			startid = walk.get(walk.size() - 1).id + 1;
		}
		return result;
	}

	public Publication getPublicationById(Integer id) {
		String sql = "SELECT id, collection, author_affiliations, authors, " + //
				"date_conferencedate_en, date_issuedMD_en, year, " + //
				"abstract, description_reference_en, description_sponsorship, " + //
				"format_page_en, identifier_beginpage_en, identifier_doi_en, " + //
				"identifier_endpage_en, identifier_inspeclocal_en, identifier_isbn, " + //
				"identifier_isilocal_en, identifier_issn_en, identifier_issue, " + //
				"identifier_page, url, " + //
				"identifier_volume_en, language, publisher_city_en, " + //
				"publisher_location_en, publisher, relation_conferencelocation, " + //
				"relation_conferencename, relation_conferencesponsorship_zh, relation_isIndexedBy_en, " + //
				"jconf, subject_clc_en, subject_discipline_en, keywords, " + //
				"title_alternative, title, " + //
				"type, institute_key FROM " + dbName + //
				".publication WHERE id = ?";

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Publication result = null;
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

	public List<Publication> getPublicationsByIds(List<Integer> ids) {
		if(null == ids || ids.size() == 0) {
			return new ArrayList<Publication>();
		}
		String sql = "SELECT id, collection, author_affiliations, authors, " + //
				"date_conferencedate_en, date_issuedMD_en, year, " + //
				"abstract, description_reference_en, description_sponsorship, " + //
				"format_page_en, identifier_beginpage_en, identifier_doi_en, " + //
				"identifier_endpage_en, identifier_inspeclocal_en, identifier_isbn, " + //
				"identifier_isilocal_en, identifier_issn_en, identifier_issue, " + //
				"identifier_page, url, " + //
				"identifier_volume_en, language, publisher_city_en, " + //
				"publisher_location_en, publisher, relation_conferencelocation, " + //
				"relation_conferencename, relation_conferencesponsorship_zh, relation_isIndexedBy_en, " + //
				"jconf, subject_clc_en, subject_discipline_en, keywords, " + //
				"title_alternative, title, " + //
				"type, institute_key FROM " + dbName + ".publication WHERE id IN ";

		List<Publication> result = new ArrayList<Publication>();
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
//			System.out.println(ps);
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

	public List<Publication> getPublicationByPublicationName(String pubName) {
		List<Publication> result = new ArrayList<Publication>();

		String sql = "SELECT id, collection, author_affiliations, authors, " + //
				"date_conferencedate_en, date_issuedMD_en, year, " + //
				"abstract, description_reference_en, description_sponsorship, " + //
				"format_page_en, identifier_beginpage_en, identifier_doi_en, " + //
				"identifier_endpage_en, identifier_inspeclocal_en, identifier_isbn, " + //
				"identifier_isilocal_en, identifier_issn_en, identifier_issue, " + //
				"identifier_page, url, " + //
				"identifier_volume_en, language, publisher_city_en, " + //
				"publisher_location_en, publisher, relation_conferencelocation, " + //
				"relation_conferencename, relation_conferencesponsorship_zh, relation_isIndexedBy_en, " + //
				"jconf, subject_clc_en, subject_discipline_en, keywords, " + //
				"title_alternative, title, " + //
				"type, institute_key FROM " + dbName + //
				".publication WHERE title = ?";

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, pubName);
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

	private Publication _construct(ResultSet rs) throws SQLException {
		Publication result = new Publication();
		result.id = rs.getInt("id");
		result.collection = rs.getString("collection");
		result.author_affiliations = rs.getString("author_affiliations");
		result.authors = rs.getString("authors");
		result.date_conferencedate_en = rs.getString("date_conferencedate_en");
		result.date_issuedMD_en = rs.getString("date_issuedMD_en");
		result.year = rs.getString("year");
		result.abstract_ = rs.getString("abstract");
		result.description_reference_en = rs.getString("description_reference_en");
		result.description_sponsorship = rs.getString("description_sponsorship");
		result.format_page_en = rs.getString("format_page_en");
		result.identifier_beginpage_en = rs.getString("identifier_beginpage_en");
		result.identifier_doi_en = rs.getString("identifier_doi_en");
		result.identifier_endpage_en = rs.getString("identifier_endpage_en");
		result.identifier_inspeclocal_en = rs.getString("identifier_inspeclocal_en");
		result.identifier_isbn = rs.getString("identifier_isbn");
		result.identifier_isilocal_en = rs.getString("identifier_isilocal_en");
		result.identifier_issn_en = rs.getString("identifier_issn_en");
		result.identifier_issue = rs.getString("identifier_issue");
		result.identifier_page = rs.getString("identifier_page");
		result.url = rs.getString("url");
		result.identifier_volume_en = rs.getString("identifier_volume_en");
		result.language = rs.getString("language");
		result.publisher_city_en = rs.getString("publisher_city_en");
		result.publisher_location_en = rs.getString("publisher_location_en");
		result.publisher = rs.getString("publisher");
		result.relation_conferencelocation = rs.getString("relation_conferencelocation");
		result.relation_conferencename = rs.getString("relation_conferencename");
		result.relation_conferencesponsorship_zh = rs.getString("relation_conferencesponsorship_zh");
		result.relation_isIndexedBy_en = rs.getString("relation_isIndexedBy_en");
		result.jconf = rs.getString("jconf");
		result.subject_clc_en = rs.getString("subject_clc_en");
		result.subject_discipline_en = rs.getString("subject_discipline_en");
		result.keywords = rs.getString("keywords");
		result.title_alternative = rs.getString("title_alternative");
		result.title = rs.getString("title");
		result.type = rs.getString("type");
		return result;
	}

	private void _construct(PreparedStatement ps, Publication p) throws SQLException {
		int count = 0;
		__safeSetString(ps, ++count, p.collection);
		__safeSetString(ps, ++count, p.author_affiliations);
		__safeSetString(ps, ++count, p.authors);
		__safeSetString(ps, ++count, p.date_conferencedate_en);
		__safeSetString(ps, ++count, p.date_issuedMD_en);
		__safeSetString(ps, ++count, p.year);
		__safeSetString(ps, ++count, p.abstract_);
		__safeSetString(ps, ++count, p.description_reference_en);
		__safeSetString(ps, ++count, p.description_sponsorship);
		__safeSetString(ps, ++count, p.format_page_en);
		__safeSetString(ps, ++count, p.identifier_beginpage_en);
		__safeSetString(ps, ++count, p.identifier_doi_en);
		__safeSetString(ps, ++count, p.identifier_endpage_en);
		__safeSetString(ps, ++count, p.identifier_inspeclocal_en);
		__safeSetString(ps, ++count, p.identifier_isbn);
		__safeSetString(ps, ++count, p.identifier_isilocal_en);
		__safeSetString(ps, ++count, p.identifier_issn_en);
		__safeSetString(ps, ++count, p.identifier_issue);
		__safeSetString(ps, ++count, p.identifier_page);
		__safeSetString(ps, ++count, p.url);
		__safeSetString(ps, ++count, p.identifier_volume_en);
		__safeSetString(ps, ++count, p.language);
		__safeSetString(ps, ++count, p.publisher_city_en);
		__safeSetString(ps, ++count, p.publisher_location_en);
		__safeSetString(ps, ++count, p.publisher);
		__safeSetString(ps, ++count, p.relation_conferencelocation);
		__safeSetString(ps, ++count, p.relation_conferencename);
		__safeSetString(ps, ++count, p.relation_conferencesponsorship_zh);
		__safeSetString(ps, ++count, p.relation_isIndexedBy_en);
		__safeSetString(ps, ++count, p.jconf);
		__safeSetString(ps, ++count, p.subject_clc_en);
		__safeSetString(ps, ++count, p.subject_discipline_en);
		__safeSetString(ps, ++count, p.keywords);
		__safeSetString(ps, ++count, p.title_alternative);
		__safeSetString(ps, ++count, p.title);
		__safeSetString(ps, ++count, p.type);
		__safeSetString(ps, ++count, p.institute_key);

	}

	private void __safeSetString(PreparedStatement ps, int parameterIndex, String value) throws SQLException {
		if (null == value)
			ps.setNull(parameterIndex, Types.VARCHAR);
		else
			ps.setString(parameterIndex, value);
	}

}
