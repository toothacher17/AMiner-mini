package SchoolSearch.services.suggestion.model;

import java.io.Serializable;

public class TrieNodeContent implements Serializable{
	private static final long serialVersionUID = -5085706914970792727L;
	public enum TrieInfoType {PERSON, PUBLICATION, GRADUATEPULICATION, TEXT};
	
	public TrieNodeContent(String text, TrieInfoType type, Integer id, double score) {
		super();
		this.text = text;
		this.type = type;
		this.id = id;
		this.score = score;
	}
	String text;
	TrieInfoType type;
	Integer id;
	double score;
	
	public String getText() {
		return text; 
	}
	public void setText(String text) {
		this.text = text;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public TrieInfoType getType() {
		return type;
	}
	public void setType(TrieInfoType type) {
		this.type = type;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
}
