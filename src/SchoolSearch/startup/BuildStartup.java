package SchoolSearch.startup;

import java.io.IOException;

import SchoolSearch.services.lucene.LuceneIndexBuilder;
import SchoolSearch.services.suggestion.TrieHandle;
import SchoolSearch.services.utils.T5RegistryHelper;

public class BuildStartup {
	public static void main(String[] args) {
		TrieHandle trie = T5RegistryHelper.getService(TrieHandle.class);
		trie.rebuild();
		System.out.println("[trie rebuild finished]");
		
		LuceneIndexBuilder lib = new LuceneIndexBuilder();
		try {
			lib.makeLuceneIndex();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("[lucene index rebuild finished]");
		T5RegistryHelper.shutdown();
	}
}
