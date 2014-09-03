package SchoolSearch.services;

import java.util.ArrayList;
import java.util.List;

import daogenerator.JavaModelFileBuilder;
import daogenerator.services.dao.MysqlDao;
import daogenerator.services.dao.model.MysqlColumn;

public class DAOStructureBuilder {
	
	
	
	
	
	public static void main(String[] args) {
		List<DBConfig> configList = new ArrayList<DAOStructureBuilder.DBConfig>();
//		configList.add(new DBConfig("arnet_db1_combine", "aminer_db1_combine", //
//				new String[]{"publication", "na_person", "na_author2pub", "jconf", "publication_ext", "contact_info"}));
	
//		configList.add(new DBConfig("arnet_db1_base", "aminer_db1_base", //
//				new String[]{"publication", "na_person", "na_author2pub", "jconf", "publication_ext", "contact_info"}));
		
//		configList.add(new DBConfig("ppp_refineddata", "ppp_refineddata", //
//				new String[]{"publication", "na_person", "na_author2pub", "jconf", "publication_ext", "contact_info"}));
		
//		configList.add(new DBConfig("nsfc_refineddata_small", "nsfc_refineddata_small", //
//				new String[]{"publication", "na_person", "na_author2pub", "jconf", "publication_ext", "contact_info"}));
//	
//		configList.add(new DBConfig("nsfc_aminer_combine", "nsfc_aminer_combine", //
//				new String[]{"publication", "na_person", "na_author2pub", "jconf", "publication_ext", "contact_info"}));
//	
//		configList.add(new DBConfig("schooltest", "schooltest", new String[]{"person_info"}));
		
//		configList.add(new DBConfig("schooltest", "schooltest", new String[]{"course","course_detail", "department", "department2school", "person", "person_ext","person_info", "person_interest", "person_profile", "person_title", "person2course", "person2department", "person2publication", "personrelation", "publication", "publication_detail", "school"}));
		
//		all the tables in schooltest;
		configList.add(new DBConfig("schooltest", "schooltest", new String[]{"course", "course_detail", "department", "department2school", "person", "person_ext", "person_info", "person_interest", "person_profile", "person_title", "person2course", "person2department", "person2graduatepublication", "person2publication", "personrelation", "publication", "publication_detail", "school", "useredit", "userlog"}));
		
		
//		configList.add(new DBConfig("test", "test", new String[]{"dbtest"}));
//		
//		configList.add(new DBConfig("arnet_db", "aminer", //
//				new String[]{"publication", "na_person", "na_author2pub", "jconf", "publication_ext", "contact_info",//
//					"na_person_organization", "ccfJconfLevel"}));
//		
//		DBConfig nsfcConfig = new DBConfig("nsfc", "nsfc", //
//				new String[]{"cnki_journal", "cnki_conference"});
//		
//		configList.add(new DBConfig("nsfc_refineddata", "nsfc2", //
//				new String[]{"publication", "na_person", "na_author2pub", "jconf", "publication_ext", "contact_info", "publication2"}));
//	
//		DBConfig currentConfig = nsfcConfig;
//		for (String table : currentConfig.tables) {
//			List<MysqlColumn> showColumnsFromTable = MysqlDao.showColumnsFromTable(currentConfig.dbName, table);
//			JavaModelFileBuilder.outputFile(currentConfig.packageName, table, showColumnsFromTable, currentConfig.dbName, currentConfig.packageName);
//		}
//		DBConfig[] allConfig = new DBConfig[] {aminerConfig, nsfcConfig, nsfc2Config};
		
//		JavaModelFileBuilder builder = new JavaModelFileBuilder("D:/Users/jingyuanliu/workspace_for_myeclipse10.0/SchoolSearch3/src/",//
//				"SchoolSearch.services.dao");
		JavaModelFileBuilder builder = new JavaModelFileBuilder().setRootPackagePath("SchoolSearch.services.dao").setWithDefaultConstructor(true);
		for(DBConfig currentConfig : configList) {
			for (String table : currentConfig.tables) {
				List<MysqlColumn> showColumnsFromTable = MysqlDao.showColumnsFromTable(currentConfig.dbName, table);
				builder.outputFile(currentConfig.packageName, table, showColumnsFromTable, currentConfig.dbName, currentConfig.packageName);
			}
		}
	}
	/**
	 * Define DBConfig as a datatype containing dbName, packageName and tables
	 * @author wenqili
	 *
	 */
	public static class DBConfig {
		String dbName; //the name of the source database
		String packageName; //the name of the target package
		String[] tables; //name of all the tables in the current database
		public DBConfig(String dbName, String packageName, String[] tables) {
			super();
			this.dbName = dbName;
			this.packageName = packageName;
			this.tables = tables;
		}
		public String getDbName() {
			return dbName;
		}
		public void setDbName(String dbName) {
			this.dbName = dbName;
		}
		public String getPackageName() {
			return packageName;
		}
		public void setPackageName(String packageName) {
			this.packageName = packageName;
		}
		public String[] getTables() {
			return tables;
		}
		public void setTables(String[] tables) {
			this.tables = tables;
		}
		
		
	}
}

