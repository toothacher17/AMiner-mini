package SchoolSearch.services;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.jpa.JpaSymbols;

import SchoolSearch.module.DAOModule;
import SchoolSearch.module.SearchModule;
import SchoolSearch.module.ServicesModule;


@SubModule ({
	DAOModule.class,//
	ServicesModule.class,//
	SearchModule.class
})
public class SchoolSearchModule {
	public static String deploy = "local";
	
	public static void contributeApplicationDefaults(MappedConfiguration<String, String> configuration) {
		configuration.add(JpaSymbols.EARLY_START_UP,"true");
		
		System.out.println("[SchoolSearch]");
		if ("amazon".equals(deploy)) {
			configuration.add(SymbolConstants.PRODUCTION_MODE, "true");
			configuration.add(SymbolConstants.COMBINE_SCRIPTS, "true");
			configuration.add(SymbolConstants.COMPACT_JSON, "true");
			configuration.add(SymbolConstants.MINIFICATION_ENABLED, "true");
			configuration.add(SymbolConstants.COMPRESS_WHITESPACE, "true");
			configuration.add("tapestry.file-check-interval", "10 m");
			// configuration.add(SymbolConstants.HOSTNAME, "arnetminer.com");
		}
		/** production mode */
		if ("aminer".equals(deploy)) {
			configuration.add(SymbolConstants.PRODUCTION_MODE, "true");
			configuration.add(SymbolConstants.COMBINE_SCRIPTS, "true");
			configuration.add(SymbolConstants.COMPACT_JSON, "true");
			configuration.add(SymbolConstants.MINIFICATION_ENABLED, "true");
			configuration.add(SymbolConstants.COMPRESS_WHITESPACE, "true");
			configuration.add("tapestry.file-check-interval", "10 m");
			// configuration.add(SymbolConstants.HOSTNAME, "arnetminer.org");
		}
	
		/** debugging mode */
		if ("local".equals(deploy)) {
			configuration.add(SymbolConstants.PRODUCTION_MODE, "false");
			configuration.add(SymbolConstants.COMBINE_SCRIPTS, "false");
			configuration.add(SymbolConstants.COMPACT_JSON, "true");
			configuration.add(SymbolConstants.MINIFICATION_ENABLED, "false");
			configuration.add(SymbolConstants.COMPRESS_WHITESPACE, "true");
		}
		configuration.add(SymbolConstants.OMIT_GENERATOR_META, "true");
		configuration.add(SymbolConstants.APPLICATION_CATALOG, "true");
		
		configuration.add(SymbolConstants.SUPPORTED_LOCALES, "en_US");
	}
}
