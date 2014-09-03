package SchoolSearch.services.lucene;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.CommonTermsQuery;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.xml.builders.BoostingQueryBuilder;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.org.wltea.analyzer.lucene.IKAnalyzer;

public class LuceneTest {
	public static IndexSearcher prepareSearcher() {
		File indexDir = new File(ConsistanceService.get("lucene.index"));
		IndexReader reader = null;
		try {
			reader = DirectoryReader.open(FSDirectory.open(indexDir));
		} catch (IOException e) {
			e.printStackTrace();
		}
		IndexSearcher searcher = new IndexSearcher(reader);
		return searcher;
	}

	public static void main1(String[] args) {
		IndexSearcher searcher = prepareSearcher();
		Query query = null;
		CommonTermsQuery ctq = new CommonTermsQuery(Occur.MUST, Occur.SHOULD, 300);
		String q = "���";
		ctq.add(new Term("course.name", q));
		ctq.add(new Term("course.abstract", q));
		ctq.add(new Term("course.teacher", q));
		query = ctq;
		TopDocs sdocs = null;
		try {
			sdocs = searcher.search(query, 30);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (ScoreDoc sdoc : sdocs.scoreDocs) {
			System.out.println(sdoc.doc + "\t" + sdoc.score);
			Document doc = null;
			try {
				doc = searcher.doc(sdoc.doc);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String type = doc.get("type");
			if (type.equals("person")) {
				System.out.println(doc.get("person.name"));
			} else if (type.equals("course")) {
				System.out.println(doc.get("course.id"));
				System.out.println(doc.get("course.name"));
				System.out.println(doc.get("course.teacher"));
				System.out.println(doc.get("course.abstract"));
			}
		}
	}

	public static void fuzzySearchTest(String[] fields, String query) {
		BooleanClause.Occur[] occurs = new BooleanClause.Occur[fields.length];
		for (int i = 0; i < fields.length; i++) {
			occurs[i] = BooleanClause.Occur.SHOULD;
		}
		IndexSearcher searcher = prepareSearcher();
		Query q = null;
		try {
			q = MultiFieldQueryParser.parse(Version.LUCENE_43, query, fields, occurs, new IKAnalyzer());

		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		TopDocs sdocs = null;
		try {
			sdocs = searcher.search(q, 100);
		} catch (IOException e) {
			e.printStackTrace();
		}
		display(sdocs, searcher);
	}

	public static void fixSearchTest(String[] fields, double[] fieldBoost, String query) {
		IndexSearcher searcher = prepareSearcher();
		BooleanQuery q = new BooleanQuery();
		for (int i = 0; i < fieldBoost.length; i++) {
			TermQuery termQuery = new TermQuery(new Term(fields[i], query));
			termQuery.setBoost((float) fieldBoost[i]);
			q.add(new BooleanClause(termQuery, Occur.SHOULD));
		}
		TopDocs sdocs = null;
		try {
			sdocs = searcher.search(q, 100);
		} catch (IOException e) {
			e.printStackTrace();
		}
		display(sdocs, searcher);
	}

	public static void indexTest(Integer documentId) {
		IndexSearcher searcher = prepareSearcher();
		Document doc = null;
		try {
			doc = searcher.doc(documentId);
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<IndexableField> fields = doc.getFields();
		Set<String> fieldNames = new HashSet<String>();
		for (IndexableField f : fields) {
			fieldNames.add(f.name());
		}

		for (String fn : fieldNames) {
			String[] values = doc.getValues(fn);
			for (int i = 0; i < values.length; i++) {
				System.out.println("------[" + fn + "][" + i + "]---------");
				System.out.println("\t" + values[i]);
			}
		}
	}

	private static void display(TopDocs sdocs, IndexSearcher searcher) {
		for (ScoreDoc sdoc : sdocs.scoreDocs) {
			Document doc = null;
			try {
				doc = searcher.doc(sdoc.doc);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.print(sdoc.doc + "\t");
			if ("person".equals(doc.get("type"))) {
				Integer cid = Integer.parseInt(doc.get("person.id"));
				String name = doc.get("person.name");
				System.out.println(cid + "\t" + name + "\t" + sdoc.score);
			} else if ("publication".equals(doc.get("type"))) {
				Integer pid = Integer.parseInt(doc.get("publication.id"));
				String title = doc.get("publication.title");
				String[] values = doc.getValues("publication.author");

				System.out.println(pid + "\t" + title + "\t" + sdoc.score);
				for (String v : values)
					System.out.print(v + "||");
				System.out.println();
			}
		}
	}

	public static void main(String[] args) {
		String[] personFields = { "person.name", "person.position", "person.interest", "person.title", "person.info", "person.publication" };
		double[] personBooster = { 2.0, 1.0, 1.0, 1.0, 0.2, 0.8 };
		String[] publicationFields = { "publication.title", "publication.author", "publication.jconf", "publication.keyword", "publication.abstract" };
		double[] publicationBoosts = { 0.8, 1.5, 1, 1, 0.5 };
		fixSearchTest(personFields, personBooster, "机器学习");
		// personFixSearchTest(personFields, "data mining");
		// personFuzySearchTest("Topic Distributions over Links on Web");
		// fixSearchTest(publicationFields, publicationBoosts, "胡事民");
//		fixSearchTest(personFields, personBooster, "数据挖掘");
		// personFuzySearchTest(publicationFields, "data mining");
		// personFuzySearchTest(publicationFields, "语义网络");
//		indexTest(3985);

	}
}
