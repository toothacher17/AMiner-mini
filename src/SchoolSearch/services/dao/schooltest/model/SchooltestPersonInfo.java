package SchoolSearch.services.dao.schooltest.model;


public class SchooltestPersonInfo {
	Integer id;
	String education;
	String projects;
	String resume;
	String honor;
	String parttime;
	String fields;
	String summary;
	String achievements;
	String experience;
	Double score;

	public SchooltestPersonInfo() {
	}

	public SchooltestPersonInfo(Integer id,String education,String projects,String resume,String honor,String parttime,String fields,String summary,String achievements,String experience,Double score) {
		this.id = id;
		this.education = education;
		this.projects = projects;
		this.resume = resume;
		this.honor = honor;
		this.parttime = parttime;
		this.fields = fields;
		this.summary = summary;
		this.achievements = achievements;
		this.experience = experience;
		this.score = score;
	}

	public Integer getId() {
		return this.id;
	}
	public String getEducation() {
		return this.education;
	}
	public String getProjects() {
		return this.projects;
	}
	public String getResume() {
		return this.resume;
	}
	public String getHonor() {
		return this.honor;
	}
	public String getParttime() {
		return this.parttime;
	}
	public String getFields() {
		return this.fields;
	}
	public String getSummary() {
		return this.summary;
	}
	public String getAchievements() {
		return this.achievements;
	}
	public String getExperience() {
		return this.experience;
	}
	public Double getScore() {
		return this.score;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public void setEducation(String education) {
		this.education = education;
	}
	public void setProjects(String projects) {
		this.projects = projects;
	}
	public void setResume(String resume) {
		this.resume = resume;
	}
	public void setHonor(String honor) {
		this.honor = honor;
	}
	public void setParttime(String parttime) {
		this.parttime = parttime;
	}
	public void setFields(String fields) {
		this.fields = fields;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public void setAchievements(String achievements) {
		this.achievements = achievements;
	}
	public void setExperience(String experience) {
		this.experience = experience;
	}
	public void setScore(Double score) {
		this.score = score;
	}
}
