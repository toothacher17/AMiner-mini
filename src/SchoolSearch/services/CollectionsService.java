package SchoolSearch.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionsService {
	public static class Position{
		static List<String> positionList = new ArrayList<String>();
		static{
			positionList.add("教授");
			positionList.add("副教授");
			positionList.add("讲师");
		}
		public static List<String> getPostionList() {
			return positionList;
		}
	}
	public static class Title {
		static List<String> titleList = new ArrayList<String>();
		static Map<String, String[]> titleAliasMap = new HashMap<String, String[]>();
		static {
			titleList.add("中国科学院院士");
			titleAliasMap.put("中国科学院院士", new String[]{"院士", "两院院士"});
			
			titleList.add("中国工程院院士");
			titleAliasMap.put("中国工程院院士", new String[]{"院士", "两院院士"});
			
			titleList.add("“长江学者奖励计划”特聘教授");
			titleAliasMap.put("“长江学者奖励计划”特聘教授", new String[]{"长江学者"});
			
			titleList.add("“长江学者奖励计划”讲座教授");
			titleAliasMap.put("“长江学者奖励计划”讲座教授", new String[]{"长江学者"});
			
			titleList.add("何梁何利基金科学与技术进步奖获得者");
			titleList.add("国家级教学名师奖获得者");
			titleList.add("国家杰出青年科学基金获得者");
			titleAliasMap.put("国家杰出青年科学基金获得者", new String[]{"杰青","杰出青年"});
			
			titleList.add("国家“百千万人才工程”入选者");
			titleAliasMap.put("国家“百千万人才工程”入选者", new String[]{"千人计划"});
			
			titleList.add("国家自然科学基金委创新研究群体学术带头人");
			titleList.add("清华大学“学术新人奖”获得者");
			titleAliasMap.put("清华大学“学术新人奖”获得者", new String[]{"学术新人奖"});
		}
		
		public static String[] getTitleAlias(String title) {
			return titleAliasMap.get(title);
		}
		
		public static List<String> getTitleList() {
			return titleList;
		}
	}		

}
