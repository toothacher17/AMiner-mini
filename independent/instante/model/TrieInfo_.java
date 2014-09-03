package instante.model;

import java.io.Serializable;


public class TrieInfo_ implements Serializable{
	private static final long serialVersionUID = 7486434369575430691L;
	public enum TrieInfoType_ {PERSON, PUBLICATION, GRADUATEPULICATION, TEXT};
	
	public TrieInfo_(String text, TrieInfoType_ type, Integer id, double score) {
		super();
		this.text = text;
		this.type = type;
		this.id = id;
		this.score = score;
	}
	String text;
	TrieInfoType_ type;
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
	public TrieInfoType_ getType() {
		return type;
	}
	public void setType(TrieInfoType_ type) {
		this.type = type;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
}
