package SchoolSearch.services.dao.graduatePublication.model;

import java.util.Comparator;

import SchoolSearch.services.dao.publication.model.Publication;

/**
 * 
 * @author guanchengran
 *
 */
public class GraduatePublication {
	public int id;
	public int department_id;
	public String res_id;
	public String author;
	public String au_id;
	public String au_dept;
	public String au_major;
	public String au_phone;
	public String au_email;
	public String rights_secret;
	public String latest_date;
	public String submit_date;
	public String degree;
	public String title_cn;
	public String title_en;
	public String tutor1_name;
	public String tutor1_address;
	public String tutor2_name;
	public String tutor2_address;
	public String keyword_cn;
	public String keyword_en;
	public int pagenum;
	public String abstract_cn;
	public String abstract_en;
	public String subject;
	public String fulltext_url;
	public String freetext_url;
	public String ti_spell;
	public String au_spell;
	public String degree_type;
	public String subject_name;
	
	public static Comparator<GraduatePublication> defaultComparator = new Comparator<GraduatePublication>() {
		@Override
		public int compare(GraduatePublication o1, GraduatePublication o2) {
			String v1 = o1.submit_date;
			String v2 = o2.submit_date;
			if(v1 == null)
				v1 = "";
			if(v2 == null)
				v2 = "";
			return v1.compareTo(v2);
		}
	};
	
}
