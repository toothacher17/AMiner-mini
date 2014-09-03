package SchoolSearch.services.services.person;

import java.util.HashMap;
import java.util.Map;

public class PersonTitleUtil {
	static Map<String, Double> titleScoreMap = new HashMap<String, Double>();
	static {
		titleScoreMap.put("清华大学“学术新人奖”获得者", 3.0);
		titleScoreMap.put("国家杰出青年科学基金获得者", 3.0);
		titleScoreMap.put("国家自然科学基金委创新研究群体学术带头人", 4.0);
		titleScoreMap.put("国家级教学名师奖获得者", 4.0);
		titleScoreMap.put("国家“百千万人才工程”入选者", 4.0);
		titleScoreMap.put("何梁何利基金科学与技术进步奖获得者", 4.5);
		titleScoreMap.put("“长江学者奖励计划”讲座教授", 4.5);
		titleScoreMap.put("“长江学者奖励计划”特聘教授", 4.5);
		titleScoreMap.put("中国工程院院士", 5.0);
		titleScoreMap.put("中国科学院院士", 5.0);
	}

	public static double getScore(String title) {
		if (titleScoreMap.containsKey(title))
			return titleScoreMap.get(title);
		else
			return 1.0;
	}
}
