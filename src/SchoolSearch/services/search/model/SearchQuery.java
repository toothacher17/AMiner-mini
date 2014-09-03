package SchoolSearch.services.search.model;

public class SearchQuery {
	public static class Default {
		public static SearchQuery personSearchCondition = new SearchQuery( //
				new String[]{"person.name", "person.position", "person.name_alias", "person.interest","person.title","person.info","person.publication", "person.coauthor"}, //
				new float[]{2f, 1f, 2f, 1f, 1f, 0.1f, 2f, 1f});
		public static SearchQuery personSearchCondition_NameQuery = new SearchQuery( //
				new String[]{"person.name", "person.info", "person.coauthor"}, //
				new float[]{5f, 0.1f, 1.5f});
		
		public static SearchQuery publicationSearchCondition = new SearchQuery( //
				new String[]{"publication.title", "publication.author", "publication.jconf", "publication.keyword", "publication.abstract"}, //
				new float[]{1f, 2f, 1f, 1f, 0.1f}); 
		public static SearchQuery courseSearchCondition = new SearchQuery( //
				new String[]{"course.name", "course.person_name", "course.courseDescription"}, //
				new float[]{1f, 1f, 0.2f}); 
	}
	
	String[] fields;
	float[] boosters;

	public SearchQuery(String[] fields, float[] boosters) {
		super();
		this.fields = fields;
		this.boosters = boosters;
	}

	public String[] getFields() {
		return fields;
	}

	public void setFields(String[] fields) {
		this.fields = fields;
	}

	public float[] getBoosters() {
		return boosters;
	}

	public void setBoosters(float[] boosters) {
		this.boosters = boosters;
	}

}
