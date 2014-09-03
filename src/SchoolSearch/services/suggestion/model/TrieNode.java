package SchoolSearch.services.suggestion.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TrieNode implements Serializable{
	private static final long serialVersionUID = -1153662399474620079L;
	public TrieNode(int contentSize) {
		super();
		this.contentIndex = new int[contentSize];
		this.subNodes = new HashMap<Character, TrieNode>();
	}
	public int endContent;
	int[] contentIndex;
	HashMap<Character, TrieNode> subNodes;

	public int[] getContentIndex() {
		return contentIndex;
	}
	public void setContentIndex(int[] contentIndex) {
		this.contentIndex = contentIndex;
	}
	public HashMap<Character, TrieNode> getSubNodes() {
		return subNodes;
	}
	public void setSubNodes(HashMap<Character, TrieNode> subNodes) {
		this.subNodes = subNodes;
	}
	public void clear() {
		for(Character c : subNodes.keySet()) {
			TrieNode trieNode = subNodes.get(c);
			if(null != trieNode) {
				trieNode.clear();
			}
		}
		subNodes.clear();
	}
	public void constructContentNodeIndexSet(Set<Integer> contentNodeIndexSet) {
		for(int index : contentIndex) {
			if(index == 0)
				break;
			contentNodeIndexSet.add(index);
		}
		for(Character c : subNodes.keySet()) {
			TrieNode trieNode = subNodes.get(c);
			if(null != trieNode) {
				trieNode.constructContentNodeIndexSet(contentNodeIndexSet);
			}
		}
	}
	
}
