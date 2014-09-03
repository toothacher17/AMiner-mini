package SchoolSearch.services.services.suggestion.impl;

import instante.TrieHandleImpl_;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import util.NameGeneration;

import net.sourceforge.pinyin4j.PinyinHelper;

import SchoolSearch.services.services.suggestion.SuggestionService;
import SchoolSearch.services.suggestion.TrieHandle;
import SchoolSearch.services.suggestion.model.TrieNodeContent;

public class SuggestionServiceImpl implements SuggestionService{

	@Inject
	TrieHandle trieHandle;
	
	@Override
	public List<String> getSuggestion(String s) {
		s = s.toLowerCase();
		List<String> result = new ArrayList<String>();
		List<TrieNodeContent> findNodeContent = trieHandle.findNodeContent(s);
		if(null != findNodeContent) {
			for(TrieNodeContent content : findNodeContent) {
				result.add(content.getText());
			}
		}
		return result;
	}
}
