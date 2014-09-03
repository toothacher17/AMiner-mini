package SchoolSearch.services.lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.FieldType.NumericType;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.hibernate.validator.util.GetDeclaredField;

import SchoolSearch.services.CollectionsService;
import SchoolSearch.services.ConsistanceService;
//import SchoolSearch.services.dao.course.CourseDao;
//import SchoolSearch.services.dao.course.Person2CourseDao;
//import SchoolSearch.services.dao.course.model.Course;
//import SchoolSearch.services.dao.course.model.Person2Course;
//import SchoolSearch.services.dao.person.PersonExtDao;
//import SchoolSearch.services.dao.person.PersonInfoDao;
//import SchoolSearch.services.dao.person.PersonInterestDao;
//import SchoolSearch.services.dao.person.PersonProfileDao;
//import SchoolSearch.services.dao.person.model.Person;
//import SchoolSearch.services.dao.person.model.PersonExt;
//import SchoolSearch.services.dao.person.model.PersonInfo;
//import SchoolSearch.services.dao.person.model.PersonInterest;
//import SchoolSearch.services.dao.person.model.PersonProfile;
//import SchoolSearch.services.dao.publication.Person2PublicationDao;
//import SchoolSearch.services.dao.publication.PublicationDao;
//import SchoolSearch.services.dao.publication.model.Person2Publication;
//import SchoolSearch.services.dao.publication.model.Publication;
import SchoolSearch.services.dao.schooltest.dao.SchooltestCourseDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestCourseDetailDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPerson2courseDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPerson2publicationDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPersonDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPersonExtDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPersonInfoDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPersonInterestDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPersonProfileDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPublicationDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPublicationDetailDAO;
import SchoolSearch.services.dao.schooltest.model.SchooltestCourse;
import SchoolSearch.services.dao.schooltest.model.SchooltestCourseDetail;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson2course;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson2publication;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonExt;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonInfo;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonInterest;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonProfile;
import SchoolSearch.services.dao.schooltest.model.SchooltestPublication;
import SchoolSearch.services.dao.schooltest.model.SchooltestPublicationDetail;
import SchoolSearch.services.org.wltea.analyzer.lucene.IKAnalyzer;
import SchoolSearch.services.utils.Strings;
import SchoolSearch.services.utils.T5RegistryHelper;

/**
 * This class demonstrate the process of creating index with Lucene for text
 * files
 */
public class LuceneIndexBuilder {              

	File indexDir = new File(ConsistanceService.get("lucene.index"));
	// PersonService personservice =
	// T5RegistryHelper.getService(PersonService.class);
	// PublicationService
	protected SchooltestPersonDAO persondao = T5RegistryHelper.getService(SchooltestPersonDAO.class);
	protected SchooltestPersonExtDAO personextdao = T5RegistryHelper.getService(SchooltestPersonExtDAO.class);
	protected SchooltestPersonProfileDAO personprofiledao = T5RegistryHelper.getService(SchooltestPersonProfileDAO.class);
	protected SchooltestPersonInfoDAO personinfodao = T5RegistryHelper.getService(SchooltestPersonInfoDAO.class);
	protected SchooltestPersonInterestDAO personinterestDao = T5RegistryHelper.getService(SchooltestPersonInterestDAO.class);

	protected SchooltestPublicationDAO publicationDao = T5RegistryHelper.getService(SchooltestPublicationDAO.class);
	protected SchooltestPublicationDetailDAO publicationDetailDao = T5RegistryHelper.getService(SchooltestPublicationDetailDAO.class);
	protected SchooltestPerson2publicationDAO person2PublicationDao = T5RegistryHelper.getService(SchooltestPerson2publicationDAO.class);
	
	
	protected SchooltestCourseDAO courseDao = T5RegistryHelper.getService(SchooltestCourseDAO.class);
	protected SchooltestCourseDetailDAO courseDetailDao = T5RegistryHelper.getService(SchooltestCourseDetailDAO.class);
	protected SchooltestPerson2courseDAO personCourseDao = T5RegistryHelper.getService(SchooltestPerson2courseDAO.class);

	Map<Integer, SchooltestPerson> personMap;
	Map<Integer, SchooltestPersonExt> personExtMap;
	Map<Integer, SchooltestPersonInfo> personInfoMap;
	Map<Integer, SchooltestPersonProfile> personProfileMap;
	Map<Integer, SchooltestPersonInterest> personInterestMap;

	Map<Integer, SchooltestPublication> publicationMap;
	Map<Integer, SchooltestPublicationDetail> publicationDetailMap;
	Map<Integer, List<Integer>> person2PublicationMap;
	Map<Integer, List<Integer>> publication2PersonMap;

	Map<Integer, SchooltestCourse> courseMap;
	Map<Integer, SchooltestCourseDetail> courseDetailMap;
	Map<Integer, List<Integer>> person2CourseMap;
	Map<String, List<Integer>> course2PersonMap;
	Map<Integer, List<Integer>> course2TeacherMap;

	List<SchooltestPerson> personAll;
	List<SchooltestPublication> publicationAll;
	List<SchooltestPublicationDetail> publicatitonDetailAll;
	List<SchooltestCourse> courseAll;
	List<SchooltestCourseDetail> courseDetailAll;
	
	private FieldType intStoreNotIndex;
	private FieldType stringStoreNotIndex;

	private void prepareVarible() {
		intStoreNotIndex = new FieldType();
		intStoreNotIndex.setStored(true);
		intStoreNotIndex.setIndexed(false);
		intStoreNotIndex.setTokenized(false);
		intStoreNotIndex.setOmitNorms(false);
		intStoreNotIndex.setNumericType(NumericType.INT);
		intStoreNotIndex.freeze();

		stringStoreNotIndex = new FieldType();
		stringStoreNotIndex.setStored(true);
		stringStoreNotIndex.setIndexed(false);
		stringStoreNotIndex.setTokenized(false);
		stringStoreNotIndex.setNumericType(null);
		stringStoreNotIndex.freeze();
	}

	private void loadCache() {

		long t0 = System.currentTimeMillis();
		System.out.println("[Loading person]\t0%");
		personAll = persondao.walkAll();
		System.out.println("[Loading person]\t20%");
		List<SchooltestPersonExt> personextAll = personextdao.walkAll();
		System.out.println("[Loading person]\t40%");
		List<SchooltestPersonInfo> personinfoAll = personinfodao.walkAll();
		System.out.println("[Loading person]\t60%");
		List<SchooltestPersonProfile> personprofileAll = personprofiledao.walkAll();
		System.out.println("[Loading person]\t80%");
		List<SchooltestPersonInterest> personinterestAll = personinterestDao.walkAll();
		System.out.println("[Loading person]\t100%");
		long t1 = System.currentTimeMillis();
		System.out.println("[Loading person finished]\t" + (t1 - t0) + "ms");

		System.out.println("[Loading publication]\t0%");
		publicationAll = publicationDao.walkAll();
		System.out.println("[Loading publicationDetail]\t25%");
		publicatitonDetailAll = publicationDetailDao.walkAll();
		System.out.println("[Loading publication]\t50%");
		List<SchooltestPerson2publication> p2pAll = person2PublicationDao.walkAll();
		System.out.println("[Loading publication]\t100%");
		long t2 = System.currentTimeMillis();
		System.out.println("[Loading publication finished]\t" + (t2 - t1) + "ms");

		System.out.println("[Loading course]\t0%");
		courseAll = courseDao.walkAll();
		System.out.println("[Loading courseDetail\t25%]");
		courseDetailAll = courseDetailDao.walkAll();
		System.out.println("[Loading couser]\t50%");
		List<SchooltestPerson2course> c2pAll = personCourseDao.walkAll();
		System.out.println("[Loading couser]\t100%");
		long t3 = System.currentTimeMillis();
		System.out.println("[Loading couser finished]\t" + (t3 - t2) + "ms");

		person2PublicationMap = convertToRelationMap(p2pAll, "person_id", "publication_id");
		publication2PersonMap = convertToRelationMap(p2pAll, "publication_id", "person_id");
		course2TeacherMap = convertToRelationMap(c2pAll, "id", "person_id");
		// person2CourseMap = convertToRelationMap(p2cAll, "person_id",
		// "course_Id");
		course2PersonMap = convertToCourseMap(courseAll);

		personMap = convertToMap(personAll, "id");
		personExtMap = convertToMap(personextAll, "id");
		personInfoMap = convertToMap(personinfoAll, "id");
		personProfileMap = convertToMap(personprofileAll, "id");
		personInterestMap = convertToMap(personinterestAll, "person_id");

		publicationMap = convertToMap(publicationAll, "id");
		publicationDetailMap = convertToMap(publicatitonDetailAll, "id");

		courseMap = convertToMap(courseAll, "id");
		courseDetailMap = convertToMap(courseDetailAll, "id");
	}

	private void buildPersonIndex(IndexWriter indexWriter) throws IOException {
		long startTime = new Date().getTime();
		int count = 0;
		for (SchooltestPerson p : personAll) {
			if (++count % 500 == 0) {
				long ctime = new Date().getTime();
				;
				System.out.println(String.format("[building person Index]\t%d/%d\t[total used] %d ms", count, personAll.size(), (ctime - startTime)));
			}
			Document doc = new Document();
			SchooltestPersonProfile pp = personProfileMap.get(p.getId());
			SchooltestPersonExt pe = personExtMap.get(p.getId());
			// Field nf = new Field("person.name", f.name, Field.Store.);
			// doc.add(new Field("type", "person", storeNotIndexString));
			//
			// doc.add(new Field("person.id", f.id, storeNotIndexInt));
			Field tf = new Field("type", "person", stringStoreNotIndex);
			doc.add(tf);
			Field idf = new IntField("person.id", p.getId(), intStoreNotIndex);
			doc.add(idf);
//			Mind: Text field will be splited, while string field is used together as a string 
			doc.add(new TextField("person.name", p.getName(), Field.Store.YES));
			doc.add(new StringField("person.name", p.getName(), Field.Store.YES));
			// TODO: alias
			if (null != pp) {
				if (Strings.isNotEmpty(pp.getPosition())) {
					doc.add(new StringField("person.position", pp.getPosition(), Field.Store.YES));
				}
			}
			// if(Strings.isNotEmpty(pp.in))
			// doc.add(new StringField("person.institute", p.institute,
			// Field.Store.YES));
			if (null != pe && null != pe.getTitle()) {
				String[] titles = pe.getTitle().split("[;；,，\n]");

				for (String title : titles) {
					if (Strings.isNotEmpty(title)) {
						String[] titleAlias = CollectionsService.Title.getTitleAlias(title);
						if(null != titleAlias) {
							for(String titleAlia : titleAlias) {
								doc.add(new StringField("person.title", titleAlia, Field.Store.YES));
							}
						}
						
						doc.add(new TextField("person.title", title, Field.Store.YES));
						doc.add(new StringField("person.title", title, Field.Store.YES));
					}
				}
			}
			if(null !=p.getNameAlias() && null!=p){
				String[] name_aliasStrings= p.getNameAlias().split("\\|\\|");
				for (String name_alias: name_aliasStrings){
					if (Strings.isNotEmpty(name_alias)){
						doc.add(new StringField("person.name_alias", name_alias.toLowerCase(), Field.Store.YES));
					}
				}
				
			}
			if (personInterestMap.containsKey(p.getId())) {
				// if(f.author_id==683894l)
				// System.out.println(aminerAuthorInterestCache.get(f.author_id).interest);
				String[] interests = personInterestMap.get(p.getId()).getInterest().split(",");
				for (String interest : interests) {
					if (!"".equals(interest.trim())) {
						doc.add(new StringField("person.interest", interest, Field.Store.YES));
						doc.add(new TextField("person.interest", interest, Field.Store.YES));
					}
				}
			}
			if (personInfoMap.containsKey(p.getId())) {
				SchooltestPersonInfo pi = personInfoMap.get(p.getId());
				StringBuilder sb = new StringBuilder();
				if (Strings.isNotEmpty(pi.getEducation())) {
					sb.append(pi.getEducation().replaceAll("[;；,，]", " ")).append(" ");
				}
				if (Strings.isNotEmpty(pi.getFields())) {
					sb.append(pi.getFields().replaceAll("[;；,，]", " ")).append(" ");
				}
				if (Strings.isNotEmpty(pi.getResume())) {
					sb.append(pi.getResume()).append(" ");
				}
				// sb.append(p.achievements);
				// sb.append(" ");
				if (Strings.isNotEmpty(pi.getHonor())) {
					sb.append(pi.getHonor()).append(" ");
				}
				if (Strings.isNotEmpty(pi.getProjects())) {
					sb.append(pi.getProjects()).append(" ");
				}
				if (Strings.isNotEmpty(pi.getExperience())) {
					sb.append(pi.getExperience()).append(" ");
				}
				if (Strings.isNotEmpty(pi.getParttime())) {
					sb.append(pi.getParttime()).append(" ");
				}
				// sb.append(p.service);
				doc.add(new TextField("person.info", sb.toString(), Field.Store.YES));
				if (null != pi.getScore() && pi.getScore() != 0) {
					doc.add(new DoubleField("personinfo.score", pi.getScore(), Field.Store.YES));
				}
			}
			if (person2PublicationMap.containsKey(p.getId())) {
				Set<String> coauthorNameSet = new HashSet<String>();
				List<Integer> list = person2PublicationMap.get(p.getId());
				for (Integer pubId : list) {
					if (publicationMap.containsKey(pubId)) {
						SchooltestPublication pub = publicationMap.get(pubId);
						SchooltestPublicationDetail pubDetail = publicationDetailMap.get(pubId);
						StringBuilder sb = new StringBuilder();
						if (Strings.isNotEmpty(pub.getTitle()))
							sb.append(pub.getTitle()).append(" ");
						if (Strings.isNotEmpty(pubDetail.getTitleAlternative()))
							sb.append(pubDetail.getTitleAlternative()).append(" ");
						/* 重复?? */
						
						if (Strings.isNotEmpty(pub.getAuthors()))
							sb.append(pub.getAuthors().toLowerCase().replaceAll("\\|\\|", " ")).append(" ");
						if (Strings.isNotEmpty(pub.getKeywords()))
							sb.append(pub.getKeywords().replaceAll("\\|\\|", " ")).append(" ");
						doc.add(new TextField("person.publication", sb.toString(), Field.Store.YES));

						String[] authorArray = pub.getAuthors().toLowerCase().split("\\|\\|");
						for (String author : authorArray) {
							coauthorNameSet.add(author);
						}
					}
				}
				for (String author : coauthorNameSet) {
					doc.add(new StringField("person.coauthor", author, Field.Store.YES));
				}
			}
			indexWriter.addDocument(doc);
		}
		long endTime = new Date().getTime();
		System.out.println("It takes " + (endTime - startTime) + "ms");
		// for (Course c : courseAll) {
		// Document doc = new Document();
		// Field tf = new Field("type", "course", stringStoreNotIndex);
		// doc.add(tf);
		// Field idf = new IntField("course.id", c.id, intStoreNotIndex);
		// doc.add(idf);
		// doc.add(new TextField("course.name", c.name, Field.Store.YES));
		// doc.add(new StringField("course.teacher", c.tearcher,
		// Field.Store.YES));
		// doc.add(new TextField("course.abstract", c._abstract,
		// Field.Store.YES));
		// indexWriter.addDocument(doc);
		// }

	}

	private void buildPublicationIndex(IndexWriter indexWriter) throws IOException {
		long startTime = new Date().getTime();
		int count = 0;
		for (SchooltestPublication p : publicationAll) {
			if (++count % 1000 == 0) {
				long ctime = new Date().getTime();
				System.out.println(String.format("[building publication Index]\t%d/%d\t[total used] %d ms", count, publicationAll.size(), (ctime - startTime)));
			}
			Document doc = new Document();

			SchooltestPublicationDetail pd = publicationDetailMap.get(p.getId());
			
			Field tf = new Field("type", "publication", stringStoreNotIndex);
			doc.add(tf);
			Field idf = new IntField("publication.id", p.getId(), intStoreNotIndex);
			doc.add(idf);
			doc.add(new TextField("publication.title", p.getTitle(), Field.Store.YES));
			doc.add(new StringField("publication.title", p.getTitle(), Field.Store.YES));
			if (Strings.isNotEmpty(pd.getTitleAlternative())) {
				doc.add(new TextField("publication.title", pd.getTitleAlternative(), Field.Store.YES));
				doc.add(new StringField("publication.title", pd.getTitleAlternative(), Field.Store.YES));
			}

			if (Strings.isNotEmpty(p.getAuthors())) {
				String[] split = p.getAuthors().toLowerCase().split("\\|\\|");
				for (String s : split) {
					s = s.trim();
					doc.add(new StringField("publication.author", s, Field.Store.YES));
				}
			}
			if (publication2PersonMap.containsKey(p.getId())) {
				Set<String> authorTitleSet = new HashSet<String>();
				Set<String> positionSet = new HashSet<String>();
				List<Integer> list = publication2PersonMap.get(p.getId());
				for (Integer pid : list) {
					if (personMap.containsKey(pid)) {
						SchooltestPerson person = personMap.get(pid);
						doc.add(new StringField("publication.author", person.getName(), Field.Store.YES));
					}
					if (personProfileMap.containsKey(pid)) {
						SchooltestPersonProfile profile = personProfileMap.get(pid);
						if(Strings.isNotEmpty(profile.getPosition())) {
							String[] split = profile.getPosition().split("[;；,，\n\\s]+");
							for(String s : split) {
								positionSet.add(s);
							}
						}
					}
					if (personExtMap.containsKey(pid)) {
						String pTitle = personExtMap.get(pid).getTitle();
						if(Strings.isNotEmpty(pTitle)) {
						String[] titles = pTitle.split("[;；,，\n\\s]+");
							for (String title : titles) {
								if (Strings.isNotEmpty(title)) {
									authorTitleSet.add(title);
								}
							}
						}
					}
				}
				for (String title : authorTitleSet) {
					if (Strings.isNotEmpty(title)) {
						String[] titleAlias = CollectionsService.Title.getTitleAlias(title);
						if(null != titleAlias) {
							for(String titleAlia : titleAlias) {
								doc.add(new StringField("publication.author.title", titleAlia, Field.Store.YES));
							}
						}
						doc.add(new StringField("publication.author.title", title, Field.Store.YES));
					}
				}
				for (String position : positionSet) {
					if (Strings.isNotEmpty(position)) {
						doc.add(new StringField("publication.author.position", position, Field.Store.YES));
					}
				}
			}
			if (Strings.isNotEmpty(p.getJconf())) {
				doc.add(new TextField("publication.jconf", p.getJconf(), Field.Store.YES));
				doc.add(new StringField("publication.jconf", p.getJconf(), Field.Store.YES));
			}
			if (Strings.isNotEmpty(p.getKeywords())) {
				String[] split = p.getKeywords().split("\\|\\|");
				for (String s : split) {
					s = s.trim();
					doc.add(new TextField("publication.keyword", s, Field.Store.YES));
					doc.add(new StringField("publication.keyword", s, Field.Store.YES));
				}
			}
			if (Strings.isNotEmpty(pd.getAbstract())) {
				doc.add(new TextField("publication.abstract", pd.getAbstract(), Field.Store.YES));
			}
			indexWriter.addDocument(doc);
		}
		long endTime = new Date().getTime();
		System.out.println("It takes " + (endTime - startTime) + "ms");
	}

	private void buildCouserIndex(IndexWriter indexWriter) throws IOException {
		long startTime = new Date().getTime();

		for (String info : course2PersonMap.keySet()) {
			List<Integer> courseIdList = course2PersonMap.get(info);

			SchooltestCourse course = courseAll.get(courseIdList.get(0) - 1);
			SchooltestCourseDetail courseDetail = courseDetailAll.get(courseIdList.get(0) - 1);
			Document doc = new Document();
			Field tf = new Field("type", "course", stringStoreNotIndex);
			doc.add(tf);
			doc.add(new TextField("course.name", course.getCourseName(), Field.Store.YES));
			doc.add(new StringField("course.name", course.getCourseName(), Field.Store.YES));

			doc.add(new TextField("course.person_name", course.getTeacherName(), Field.Store.YES));
			doc.add(new StringField("course.person_name", course.getTeacherName(), Field.Store.YES));

			if (Strings.isNotEmpty(courseDetail.getCourseDescription())) {
				doc.add(new TextField("course.courseDescription", courseDetail.getCourseDescription(), Field.Store.YES));
			}
			for (Integer courseId : courseIdList) {
				doc.add(new IntField("course.id", courseId, intStoreNotIndex));
			}
			if (course2TeacherMap.containsKey(course.getId())) {
				List<Integer> list = course2TeacherMap.get(course.getId());
				Integer pid = list.get(0);
				if (personExtMap.containsKey(pid)) {
					SchooltestPersonExt pe = personExtMap.get(pid);
					if (null != pe && null != pe.getTitle()) {
						String[] titles = pe.getTitle().split("[;；,，\n]");
						for (String title : titles) {
							if (Strings.isNotEmpty(title)) {
								String[] titleAlias = CollectionsService.Title.getTitleAlias(title);
								if(null != titleAlias) {
									for(String titleAlia : titleAlias) {
										doc.add(new StringField("course.person.title", titleAlia, Field.Store.YES));
									}
								}
								
								doc.add(new StringField("course.person.title", title, Field.Store.YES));
							}
						}
					}
				}
			}
			if (course2TeacherMap.containsKey(course.getId())) {
				List<Integer> list = course2TeacherMap.get(course.getId());
				Integer pid = list.get(0);
				if (personProfileMap.containsKey(pid)) {
					SchooltestPersonProfile pp = personProfileMap.get(pid);
					if (null != pp && null != pp.getPosition()) {
						String[] positions = pp.getPosition().split("[;；,，\n]");
						for (String position : positions) {
							if (Strings.isNotEmpty(position)) {
								doc.add(new StringField("course.person.position", position, Field.Store.YES));
							}
						}
					}
				}
			}
			
			indexWriter.addDocument(doc);
		}
		long endTime = new Date().getTime();
		System.out.println("It takes " + (endTime - startTime) + "ms");
	}

	public void makeLuceneIndex() throws IOException {
		loadCache();
		prepareVarible();
		if (!indexDir.exists())
			indexDir.mkdirs();
		File[] listFiles = indexDir.listFiles();
		for (File f : listFiles) {
			f.delete();
		}
		Analyzer luceneAnalyzer = new IKAnalyzer(false);
		IndexWriter indexWriter = new IndexWriter(FSDirectory.open(indexDir), new IndexWriterConfig(Version.LUCENE_43, luceneAnalyzer));
		
		buildPersonIndex(indexWriter);
		buildPublicationIndex(indexWriter);
		buildCouserIndex(indexWriter);

		indexWriter.close();

	}

	public static void main(String[] args) {
		LuceneIndexBuilder lib = new LuceneIndexBuilder();
		try {
			lib.makeLuceneIndex();
		} catch (IOException e) {
			e.printStackTrace();
		}
		T5RegistryHelper.shutdown();
	}

	private Map<Integer, List<Integer>> convertToRelationMap(List<? extends Object> source, String fromFieldName, String toFieldName) {
		Map<Integer, List<Integer>> result = new HashMap<Integer, List<Integer>>();
		try {
			java.lang.reflect.Field fromField = source.get(0).getClass().getDeclaredField(fromFieldName);
			fromField.setAccessible(true);
			java.lang.reflect.Field toField = source.get(0).getClass().getDeclaredField(toFieldName);
			toField.setAccessible(true);
			for (Object o : source) {
				Integer fromValue = (Integer) fromField.get(o);
				Integer toValue = (Integer) toField.get(o);
				if (!result.containsKey(fromValue))
					result.put(fromValue, new ArrayList<Integer>());
				List<Integer> list = result.get(fromValue);
				if (!list.contains(toValue))
					list.add(toValue);
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return result;
	}

	private <T> Map<Integer, T> convertToMap(List<T> source, String fieldName) {
		Map<Integer, T> target = new HashMap<Integer, T>();
		if (source.size() > 0) {
			java.lang.reflect.Field f = null;
			try {
				f = source.get(0).getClass().getDeclaredField(fieldName);
				f.setAccessible(true);
				for (T s : source) {
					Integer id = (Integer) f.get(s);
					target.put(id, s);
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return target;
	}

	private Map<String, List<Integer>> convertToCourseMap(List<SchooltestCourse> source) {
		Map<String, List<Integer>> result = new HashMap<String, List<Integer>>();
		if (source.size() > 0) {
			for (SchooltestCourse c : source) {
				String key = _generateCourseCombineKey(c);
				Integer value = c.getId();
				if (!result.containsKey(key)) {
					result.put(key, new ArrayList<Integer>());
				}
				result.get(key).add(value);
			}
		}
		return result;
	}

	private String _generateCourseCombineKey(SchooltestCourse c) {
		return c.getCourseName() + "##" + c.getTeacherName();
	}
}