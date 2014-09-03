package SchoolSearch.services.dao.person;
/**
 * @author GCR
 */
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.dao.person.model.PersonInfo;
import SchoolSearch.services.services.editLog.EditLogService;
import SchoolSearch.services.utils.Strings;
import SchoolSearch.services.utils.db.ConnectionPool;
import SchoolSearch.services.utils.db.DBConnection;

public class PersonInfoDao {
	String dbName = ConsistanceService.get("db.defaultDatabase");
	static ConnectionPool pool = ConnectionPool.getInstance();
	
	private static PersonInfoDao instance;
	
	public static PersonInfoDao getInstance(){
		if(null == instance){
			instance = new PersonInfoDao();
		}
		return instance;
	}
	
	public static PersonInfoDao getIndependentInstance(String dbName) {
		return new PersonInfoDao(dbName);
	}

	public PersonInfoDao() {
	}

	private PersonInfoDao(String dbName) {
		this.dbName = dbName;
	}
	
	public  void insert(PersonInfo personInfo) {
		String sql = "INSERT INTO " + dbName
				+ ".person_info (id, education, projects, resume, honor, parttime, fields, summary, achievements, experience) "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			_construct(ps, personInfo);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}

	public  void insertBatch(List<PersonInfo> personInfoList) {
		String sql = "INSERT INTO " + dbName
				+ ".person_info (id, education, projects, resume, honor, parttime, fields, summary, achievements, experience) "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?);";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			for (PersonInfo personInfo : personInfoList) {
				_construct(ps, personInfo);
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
	
	public List<PersonInfo> walk(int start, int limit) {
		List<PersonInfo> result = new ArrayList<PersonInfo>();

		String sql = "SELECT id, education, projects, resume, honor, parttime, fields, summary, achievements, experience FROM "//
				+ dbName + ".person_info WHERE id >= ? ORDER BY id LIMIT ?";

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(sql);
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
	
	public void deleteBatch(List<Integer> idList) {
		if(idList.size() == 0)
			return;
		String sql = "DELETE FROM " + dbName + ".person_info WHERE id IN ";
		StringBuilder sb = new StringBuilder();
		sb.append(sql);
		sb.append("(");
		for(int i=0; i<idList.size(); i++) {
			sb.append("?");
			if(i < idList.size()-1)
				sb.append(",");
		}
		sb.append(")");
		
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		
		try {
			ps = conn.prepareStatement(sb.toString());
			for(int i=0; i<idList.size(); i++) {
				ps.setInt(i+1, idList.get(i));
			}
			System.out.println(">>[pi]>>" + ps);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}


	public List<PersonInfo> walkAll() {
		List<PersonInfo> result = new ArrayList<PersonInfo>();
		int startid = 0;
		int limit = 500;
		while (true) {
			List<PersonInfo> walk = walk(startid, limit);
			if (null == walk || walk.size() == 0)
				break;
			result.addAll(walk);
			startid = walk.get(walk.size() - 1).id + 1;
		}
		return result;
	}
	
	@Inject
	EditLogService logService;
	public void updatePersonInfo(Integer id, String field_name, String new_value, PersonInfo op) {
		String sql = "UPDATE " + dbName + ".person_info SET `" + field_name + "` = ? WHERE id = ?";
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;

		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, new_value);
			ps.setInt(2, id);
			int affectedRows = ps.executeUpdate();
			if (affectedRows > 0) {
				String old_value = null;

				Field field = PersonInfo.class.getField(field_name);
				old_value = (String) field.get(op);
				boolean doLog = true;
				if(null == old_value) {
					if(null != new_value)
						doLog = true;
				} else if(!old_value.equals(new_value)) {
					doLog = true;
				}
				if(doLog)
					logService.log("person_info", id, field_name, old_value,
							new_value);		
			}
		}catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}
	}
	
	public void updatePersonInfo(Integer id, String education,
			String projects, String resume, String honor, String parttime,
			String fields, String summary, String achievements,
			String experience, PersonInfo op) {
		String sql = "UPDATE " + dbName
				+ ".person_info SET education = ?, projects = ?, resume = ?, " //
				+ "honor = ?, parttime = ?, fields = ?, summary = ?, achievements = ?, experience = ? WHERE id = ?";

		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;

		try {
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(sql);
			ps.setString(1, education);
			ps.setString(2, projects);
			ps.setString(3, resume);
			ps.setString(4, honor);
			ps.setString(5, parttime);
			ps.setString(6, fields);
			ps.setString(7, summary);
			ps.setString(8, achievements);
			ps.setString(9, experience);
			ps.setInt(10, id);
			System.out.println(ps.toString());
			ps.addBatch();
			ps.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
						
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps);
		}

	}

	public PersonInfo getPersonInfoById(int id)
	{
		PersonInfo result = null;
		String sql = "SELECT id, education, projects, resume, honor, parttime, fields, summary, achievements, experience FROM "//
				+ dbName + ".person_info WHERE id = " + id;
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
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
	
	public List<PersonInfo> getPersonInfoByPersonIds(List<Integer> personIdList) {
		if(personIdList.size() == 0)
			return new ArrayList<PersonInfo>();
		List<PersonInfo> result = new ArrayList<PersonInfo>();
		String sql = "SELECT id, education, projects, resume, honor, parttime, fields, summary, achievements, experience FROM "
				+ dbName + ".person_info WHERE id IN ";
		StringBuilder sb = new StringBuilder();
		sb.append(sql);
		sb.append("(");
		for(int i=0; i<personIdList.size(); i++) {
			sb.append("?");
			if(i < personIdList.size()-1)
				sb.append(",");
		}
		sb.append(")");
		
		DBConnection conn = pool.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			ps = conn.prepareStatement(sb.toString());
			for(int i=0; i<personIdList.size(); i++) {
				ps.setInt(i+1, personIdList.get(i));
			}
			rs = ps.executeQuery();
			while(rs.next()) {
				result.add(_construct(rs));	
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			ConnectionPool.close(conn, ps, rs);
		}
		return result;
	}
	
	public void WordsSub() {
		List<PersonInfo> results = walkAll();
		List<PersonInfo> educationSub = new ArrayList<PersonInfo>();
		for(PersonInfo result : results) {
			if(Strings.isEmpty(result.education))
				continue;
			educationSub.add(result);
		}
		
		for(PersonInfo pi : educationSub) {
			if(pi.education.contains("就读于")) {
				pi.education = pi.education.replace("就读于", " ");
			}
			if(pi.education.contains("毕业于")) {
				pi.education = pi.education.replace("毕业于", " ");
			}
			if(pi.education.contains("于")) {
				pi.education = pi.education.replace("于", "");
			}
		}
		
		for(PersonInfo pi : educationSub) {
			updatePersonInfo(pi.id, "education", pi.education, pi);
		}
	}
	

	private PersonInfo _construct(ResultSet rs) throws SQLException {
		PersonInfo result = new PersonInfo();
		result.id = rs.getInt("id");
		result.education = rs.getString("education");
		result.projects = rs.getString("projects");
		result.resume = rs.getString("resume");
		result.honor = rs.getString("honor");
		result.parttime = rs.getString("parttime");
		result.fields = rs.getString("fields");
		result.summary = rs.getString("summary");
		result.achievements = rs.getString("achievements");
		result.experience = rs.getString("experience");
		return result;
	}
	
	private void _construct(PreparedStatement ps, PersonInfo pi) throws SQLException {
		int count = 0;
		ps.setInt(++count, pi.id);
		__safeSetString(ps, ++count, pi.education);
		__safeSetString(ps, ++count, pi.projects);
		__safeSetString(ps, ++count, pi.resume);
		__safeSetString(ps, ++count, pi.honor);
		__safeSetString(ps, ++count, pi.parttime);
		__safeSetString(ps, ++count, pi.fields);
		__safeSetString(ps, ++count, pi.summary);
		__safeSetString(ps, ++count, pi.achievements);
		__safeSetString(ps, ++count, pi.experience);
	}
	private void __safeSetString(PreparedStatement ps, int parameterIndex, String value) throws SQLException {
		if (null == value)
			ps.setNull(parameterIndex, Types.VARCHAR);
		else
			ps.setString(parameterIndex, value);
	}
	
//	public static void main(String args[]) {
//		PersonInfoDao piDao = T5RegistryHelper.getService(PersonInfoDao.class);
//		PersonInfo pi = piDao.getPersonInfoById(1);
//		piDao.updatePersonInfo(1, "大学:1983  清华大学；硕士:1986  清华大学；博士:2007  中国科学技术大学；", " 国家“核高基”科技重大专项: 先进EDA工具平台开发 (2008-2010); 国家自然科学基金海外青年合作: 考虑工艺参数变化的IC设计优化理论与关键技术 (2009-2010); 国家自然科学基金: 极大规模集成电路片上供电网络仿真及优化 (2008-2010); 国家自然科学基金: 纳米工艺下集成电路自动布线算法研究 (2010-2012).", "none	", "教育部科技进步二等奖――超大规模集成电路物理级优化和验证问题基础研究 (2006);DAC 2009: 最佳论文提名奖 (2009); ICCAD2006: 最佳论文提名奖 (2006); GLVLSI 2008: 最佳论文奖 (2008); 清华大学: 教学优秀奖 (2000).", "none", " 集成电路计算机辅助设计； 集成电路； 微电子", "我的主要研究兴趣为微电子与计算机交叉领域中的IC设计优化算法与大规模数值计算分析，已经从事EDA领域研究工作20多年。近年来，伴随着Moore定律的发展，我对IC设计中的互连线时延和噪声分析优化、大规模IC供电网络并行分析、低功耗物理设计优化、以及纳米工艺下面向可制造性（DFM）设计优化等一些国际前沿性问题进行了深入的研究。先后参加或主持了国家“核高基”科技重大专项、973、863、国家自然科学基金重大国际合作、国家自然科学基金等多项国际科研或合作项目。 主要工作分为以下三方面：1. 对IC片上供电网络的分析和优化进行了系统和深入的研究：提出了基于GPU的P/G网络并行分析快速泊松方法，获得DAC 2009最佳论文提名奖，这是大陆学者首次获得该项荣誉；提出了基于三维模型的大规模P/G网络快速分析方法，成果发表在国际IC物理设计年会ISPD 2006及国际期刊Trans. On CAD上；提出了Dcap电容优化与布局结合等一系列P/G网络优化方法，成果发表在国际会议ICCAD 2009及国际期刊Trans. On CAS-II国际期刊上。2. 对IC低功耗与时序物理设计优化进行了研究：提出了性能驱动的功耗关断物理优化方法，获得以功耗优化为主题的国际年会SGLVLSI 2008最佳论文奖，这是大陆学者首次获得该项荣誉；提出了基于电压岛多供电功耗优化方法、时钟关断功耗优化方法等，成果发表在国际IC物理设计年会ISPD 2008及国际期刊Trans. On VLSI上。3. 对纳米工艺下工艺参数变化及可制造性（DFM）问题进行深入研究：与Synopsys合作提出了基于区域模型匹配的OPC热点探测方法，获得ICCAD 2006最佳论文提名奖；提出了一系列面向DFM的布线和优化算法，成果发表在国际会议及Trans. On VLSI等国际期刊上。", "", "none", pi);
//	}
}
