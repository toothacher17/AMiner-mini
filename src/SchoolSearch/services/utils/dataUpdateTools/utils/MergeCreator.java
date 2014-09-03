package SchoolSearch.services.utils.dataUpdateTools.utils;
/**
 * @author CX
 */
import java.util.ArrayList;
import java.util.List;

import SchoolSearch.services.utils.dataUpdateTools.model.MergedGroup;

public class MergeCreator {
	private static MergeCreator instance;
	
	private MergedGroup mg;
	
	public static MergeCreator getInstance(List<String> strList, String replacement){
		if(null == instance)
			instance = new MergeCreator(strList, replacement);
		return instance;
	}
	
	public static MergeCreator getIndependentInstance(List<String> strList, String replacement){
		return new MergeCreator(strList, replacement);
	}
	
	public MergeCreator(List<String> strList, String replacement){
		mg = new MergedGroup();
		mg.mergedElements = new ArrayList<String>();
		for(String key:strList){
			mg.mergedElements.add(key.toLowerCase());
		}
		mg.replacementType = "char";
		mg.replacementValue = replacement.toLowerCase();
		mg.priority = new ArrayList<Integer>();
		mg.priority.add(1);
		mg.priority.add(0);
	}
	
	public MergedGroup getMg(){
		return mg;
	}
	
}
