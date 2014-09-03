package SchoolSearch.services.search.model;

public class SearchCondition {
	public static class Default {
		public static SearchCondition personSearchCondition = new SearchCondition( //
				new String[]{"person.name", "person.position", "person.name_alias", "person.interest","person.title","person.info","person.publication", "person.coauthor"}, //
				new float[]{2f, 1f, 2f, 1f, 1f, 0.1f, 1f, 1f});
		public static SearchCondition personSearchCondition_NameQuery = new SearchCondition( //
				new String[]{"person.name", "person.info", "person.coauthor"}, //
				new float[]{5f, 0.1f, 1.5f});
		
		public static SearchCondition publicationSearchCondition = new SearchCondition( //
				new String[]{"publication.title", "publication.author", "publication.jconf", "publication.keyword", "publication.abstract"}, //
				new float[]{1f, 2f, 1f, 1f, 0.1f}); 
		public static SearchCondition courseSearchCondition = new SearchCondition( //
				new String[]{"course.name", "course.person_name", "course.courseDescription"}, //
				new float[]{1f, 1f, 0.2f}); 
	}
	
	String[] fields;
	float[] boosters;

	public SearchCondition(String[] fields, float[] boosters) {
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
