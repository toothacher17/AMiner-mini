package SchoolSearch.services.dao.publication.model;

import java.util.Comparator;

/**
 * 
 * @author guanchengran
 *
 */
public class Publication {
	
	public Integer id;
	public String collection;
	public String author_affiliations;
	public String authors;
	public String date_conferencedate_en;
	public String date_issuedMD_en;
	public String year;
	public String abstract_;
	public String description_reference_en;
	public String description_sponsorship;
	public String format_page_en;
	public String identifier_beginpage_en; 
	public String identifier_doi_en;
	public String identifier_endpage_en;
	public String identifier_inspeclocal_en;
	public String identifier_isbn;
	public String identifier_isilocal_en;
	public String identifier_issn_en;
	public String identifier_issue; 
	public String identifier_page;
	public String url;
	public String identifier_volume_en;
	public String language;
	public String publisher_city_en;  
	public String publisher_location_en;  
	public String publisher;  
	public String relation_conferencelocation;  
	public String relation_conferencename;  
	public String relation_conferencesponsorship_zh;
	public String relation_isIndexedBy_en;  
	public String jconf;
	public String subject_clc_en;
	public String subject_discipline_en;
	public String keywords;
	public String title_alternative;
	public String title;
	public String type;  
	public String institute_key;  
	
	public static Comparator<Publication> defaultComparator = new Comparator<Publication>() {
		@Override
		public int compare(Publication o1, Publication o2) {
			return o1.year.compareTo(o2.year);
		}
	};
	
	public String getJConfType() {
		if(type.contains("会议")) {
			return "conf";
		} else {
			return "journal";
		}
	}
}
