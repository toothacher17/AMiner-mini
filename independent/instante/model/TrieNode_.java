package instante.model;

import java.io.Serializable;

public class TrieNode_ implements Serializable{
	private static final long serialVersionUID = 9195254724620060497L;
	public TrieNode_(int nodeIndexSize, int subnodeSize) {
		trieInfoIndexes = new int[nodeIndexSize];
		subnodes = new TrieNode_[subnodeSize];
		endIndex = 0;
	}	
	
	int[] trieInfoIndexes;
	TrieNode_[] subnodes;
	int endIndex;
	public int[] getTrieInfoIndexes() {
		return trieInfoIndexes;
	}
	public void setTrieInfoIndexes(int[] trieInfoIndexes) {
		this.trieInfoIndexes = trieInfoIndexes;
	}
	public TrieNode_[] getSubnodes() {
		return subnodes;
	}
	public void setSubnodes(TrieNode_[] subnodes) {
		this.subnodes = subnodes;
	}
	public int getEndIndex() {
		return endIndex;
	}
	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}
	public void EndIndexPlus() {
		this.endIndex++;
	}
}
