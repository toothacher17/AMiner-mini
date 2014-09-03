package instante;

import instante.model.TrieInfo_;
import instante.model.TrieNode_;
import instante.model.TrieInfo_.TrieInfoType_;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.json.JSONObject;

import util.NameGeneration;

import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.dao.person.model.Person;

public class TrieHandleImpl_ {
	final int indexSize = 10;
	final int trieNodeSize = 28;
	public TrieNode_ root;
	public List<TrieInfo_> trieInfoList;
	public Map<String,Integer> trieTextMap;
	
	private void write() throws IOException{
		String targetFilePathRoot = ConsistanceService.get("th.defaultFilePath1");
		String targetFilePathTrieInfoList = ConsistanceService.get("th.defaultFilePath2");
		if(null != targetFilePathRoot && null != targetFilePathTrieInfoList){
			File file1 = new File(targetFilePathRoot);
			File file2 = new File(targetFilePathTrieInfoList);
			if(!file1.exists()) {
				File parentFile = file1.getParentFile();
				if(null != parentFile)
					parentFile.mkdirs();
			}
			if(!file2.exists()) {
				File parentFile = file2.getParentFile();
				if(null != parentFile)
					parentFile.mkdirs();
			}
			ObjectOutputStream outBuff1 = new ObjectOutputStream(new FileOutputStream(file1));
			ObjectOutputStream outBuff2 = new ObjectOutputStream(new FileOutputStream(file2));
			outBuff1.writeObject(root);
			outBuff2.writeObject(trieInfoList);
			outBuff1.close();
			outBuff2.close();
		}else{
			System.out.println("[WriteErroInfo]INPUT IS NULL, DO NOTHING!");
		}
	}
//	@SuppressWarnings("unchecked")
	private void read() throws IOException, ClassNotFoundException{
		String targetFilePathRoot = ConsistanceService.get("th.defaultFilePath1");
		String targetFilePathTrieInfoList = ConsistanceService.get("th.defaultFilePath2");
		if(null != targetFilePathRoot && null != targetFilePathTrieInfoList){
			File file1 = new File(targetFilePathRoot);
			File file2 = new File(targetFilePathTrieInfoList);
			ObjectInputStream inBuff1 = new ObjectInputStream(new FileInputStream(file1));
			ObjectInputStream inBuff2 = new ObjectInputStream(new FileInputStream(file2));
			root = (TrieNode_)inBuff1.readObject();
			trieInfoList = (List<TrieInfo_>)inBuff2.readObject();
			inBuff1.close();
			inBuff2.close();
		}else{
			System.out.println("[WriteErroInfo]INPUT IS NULL, DO NOTHING!");
		}
	}
	public TrieHandleImpl_() {
		try {
			read();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private TrieHandleImpl_(boolean exist) {
		root = newTrieNode();
		trieInfoList = new ArrayList<TrieInfo_>();
		trieInfoList.add(null);
		trieTextMap = new HashMap<String, Integer>();
	}
	
	private TrieNode_ newTrieNode() {
		return new TrieNode_(indexSize, trieNodeSize);
	}

	private void setupNode(String keytext, String fulltext, Double score, Integer id) {
		int newInfoIndex = -1;
		if(trieTextMap.containsKey(fulltext)) {
			newInfoIndex = trieTextMap.get(fulltext);
		} else {
			newInfoIndex = trieInfoList.size();
			TrieInfo_ ti = new TrieInfo_(fulltext, TrieInfoType_.PERSON, id, score);
			trieInfoList.add(ti);
			trieTextMap.put(fulltext, newInfoIndex);
		}
		System.out.println(keytext + "\t" + fulltext + "\t" + newInfoIndex + "\t" + score);
		
		keytext = keytext.toLowerCase();
		if (null == root) {
			root = newTrieNode();
		}
		char[] charArray = keytext.toCharArray();
		TrieNode_ currentNode = root;
		for (int i = 0; i < charArray.length; i++) {
			int index = charToIndex(charArray[i]);
			int compareStart = currentNode.getEndIndex();
			if(i == charArray.length-1) {
				score += 10;
				currentNode.EndIndexPlus();
				compareStart = 0;
			}
			try {
				if (currentNode.getSubnodes()[index] == null) {
					currentNode.getSubnodes()[index] = newTrieNode();
					currentNode.getSubnodes()[index].getTrieInfoIndexes()[0] = newInfoIndex;
				} else {
					for (int j = compareStart; j < indexSize; j++) {
						if (currentNode.getSubnodes()[index].getTrieInfoIndexes()[j] == 0) {
							currentNode.getSubnodes()[index].getTrieInfoIndexes()[j] = newInfoIndex;
							break;
						} else if(currentNode.getSubnodes()[index].getTrieInfoIndexes()[j] == newInfoIndex) {
							break;
						} else {
							TrieNode_ indexNode = currentNode.getSubnodes()[index];
							double jScore = trieInfoList.get(indexNode.getTrieInfoIndexes()[j]).getScore();
							if(j < currentNode.getEndIndex()) {
								jScore+=10;
							}
							if(score <= jScore)
								continue;
							else {
								for(int k=indexSize-1; k>j; k--) {
									indexNode.getTrieInfoIndexes()[k] =indexNode.getTrieInfoIndexes()[k-1];  
								}
								indexNode.getTrieInfoIndexes()[j] = newInfoIndex;
								break;
							}
						}
					} 
				}
				currentNode = currentNode.getSubnodes()[index];
			} catch (IndexOutOfBoundsException e) {
				System.err.println(keytext + "\t" + fulltext);
				e.printStackTrace();
				System.exit(0);
			}
		}
	}
	
	private int[] findValueIndex(String key) {
		key = key.toLowerCase();
		char[] charArray = key.toCharArray();
		TrieNode_ currentNode = root;
		for(int i=0; i<charArray.length; i++) {
			int index = charToIndex(charArray[i]);
			if(currentNode.getSubnodes()[index]!=null) {
				currentNode = currentNode.getSubnodes()[index];
			} else {
				return null;
			}
		}
		return currentNode.getTrieInfoIndexes();
	}
	
	private void displayTrieInfoText(int[] indexes) {
		if(null == indexes) {
			System.out.println("[not existed in trie]");
		} else {
			for(int i=0; i<indexes.length; i++) {
				if(indexes[i]==0) 
					break;
				else 
					System.out.print(trieInfoList.get(indexes[i]).getText() + " | ");
			}
			System.out.println();
		}
	}
	
	private int charToIndex(char c) {
		int index = c - 'a';
		if(index == -65)	// space
			index = 26;
		if(index == -51)
			index = 27;
		return index;
	}

	public List<String> findContext(String key) {
		int[] findValueIndex = findValueIndex(key);
		if(null == findValueIndex) {
//			System.out.println("[null key]" + key);
			return new ArrayList<String>();
		}
		List<String> result = new ArrayList<String>();
		for(int index : findValueIndex)  {
			if(0 == index)
				break;
			result.add(trieInfoList.get(index).getText());
		}
		return result;
	}
	
	public void rebuildTrie(List<Person> allPerson) {
		long t0 = System.currentTimeMillis();
		TriePersonScore personScorer = new TriePersonScore();
		TrieHandleImpl_ nt = new TrieHandleImpl_(false);
		for(Person p : allPerson) {
			List<String> generateEnglishNames = NameGeneration.generateEnglishNames(p.name, false);
			double score = personScorer.scoreRankByPerson(p.id);
			for(String s : generateEnglishNames) {
				
				s = s.toLowerCase().replaceAll("[^a-z .]", "");
				nt.setupNode(s, p.name, score, p.id);
				String s2 = s.replaceAll("[ .]", "");
				if(!s2.equals(s))
					nt.setupNode(s2, p.name, score, p.id);
			}
		}
		try {
			nt.write();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			read();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("[rebuild Trie costs]\t" + (System.currentTimeMillis() - t0) + "ms");
	}
}
