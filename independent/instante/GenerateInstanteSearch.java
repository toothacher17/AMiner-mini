package instante;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import SchoolSearch.services.dao.person.PersonDao;
import SchoolSearch.services.dao.person.model.Person;
import SchoolSearch.services.suggestion.TrieHandle;
import SchoolSearch.services.suggestion.impl.TrieHandleImpl;
import SchoolSearch.services.suggestion.model.TrieNodeContent;
import SchoolSearch.services.utils.T5RegistryHelper;

public class GenerateInstanteSearch {
	static PersonDao persondao = PersonDao.getInstance();
	
	public static void main1(String[] args) {
		
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		TrieHandle tr = T5RegistryHelper.getService(TrieHandle.class);
		tr.rebuild();
		
		List<String> keys = generateUserInput("li daokui");
		keys.addAll(generateUserInput("李稻葵"));
//		keys.addAll(generateUserInput("唐杰"));
		for(String key : keys) {
			key = key.trim().toLowerCase();
			System.out.println("[key]\t" + key);
			List<TrieNodeContent> findNodeContent = tr.findNodeContent(key);
			for(TrieNodeContent node : findNodeContent) {
				System.out.print(node.getText() + "|");
			}
			System.out.println();
		}
	}
	
	public static List<String> generateUserInput(String s) {
		List<String> result = new ArrayList<String>();
		char[] charArray = s.toCharArray();
		StringBuilder sb = new StringBuilder();
		for(int i=0 ;i<charArray.length; i++) {
			sb.append(charArray[i]);
			result.add(sb.toString());
		}
		return result;
	}
}
