package SchoolSearch.services.page.model;

public class RelationShow {

	
	
	public RelationShow(Integer relationId, String objectName, String relationType) {
		super();
		this.relationId = relationId;
		this.objectName = objectName;
		this.relationType = relationType;
	}

	Integer relationId;
	String objectName;
	String relationType;
	Integer personId;
	double translate_x;
	double translate_y;
	double rotate;
	double length;
	
	final double text_trans = 5.5; 
	
	public String getTextAnchor() {
		return rotate < 90 ? "start" : "end";
	}
	public String getTextTransform() {
		return rotate < 90 ? "translate(" + text_trans+ ")" : "rotate(180)translate(-" + text_trans + ")";
//		return "translate(" + text_trans+ ")";
	}


	
	
	public Integer getRelationId() {
		return relationId;
	}
	public void setRelationId(Integer relationId) {
		this.relationId = relationId;
	}
	public Integer getPersonId() {
		return personId;
	}
	public void setPersonId(Integer personId) {
		this.personId = personId;
	}
	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getRelationType() {
		return relationType;
	}

	public void setRelationType(String relationType) {
		this.relationType = relationType;
	}

	public double getTranslate_x() {
		return translate_x;
	}

	public void setTranslate_x(double translate_x) {
		this.translate_x = translate_x;
	}

	public double getTranslate_y() {
		return translate_y;
	}

	public void setTranslate_y(double translate_y) {
		this.translate_y = translate_y;
	}

	public double getRotate() {
		return rotate;
	}

	public void setRotate(double rotate) {
		this.rotate = rotate;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

}
