package SchoolSearch.services.utils.dataUpdateTools.model;

import java.io.Serializable;

/**
 * 
 * @author CX
 *
 */
public class ColumnStructure implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String type;
	public int index;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
	
}
