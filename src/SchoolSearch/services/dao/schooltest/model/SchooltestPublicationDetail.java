package SchoolSearch.services.dao.schooltest.model;


public class SchooltestPublicationDetail {
	Integer id;
	String collection;
	String type;
	String title_alternative;
	String keywords;
	String subject_discipline_en;
	String subject_clc_en;
	String relation_isIndexedBy_en;
	String relation_conferencename;
	String relation_conferencelocation;
	String publisher;
	String publisher_location_en;
	String publisher_city_en;
	String language;
	String identifier_volume_en;
	String url;
	String identifier_page;
	String identifier_issue;
	String identifier_issn_en;
	String identifier_isilocal_en;
	String identifier_inspeclocal_en;
	String identifier_endpage_en;
	String identifier_doi_en;
	String identifier_beginpage_en;
	String format_page_en;
	String description_sponsorship;
	String relation_conferencesponsorship_zh;
	String description_reference_en;
	String _abstract;
	String date_issuedMD_en;
	String date_conferencedate_en;
	String author_affiliations;
	String identifier_isbn;
	String institute_key;

	public SchooltestPublicationDetail() {
	}

	public SchooltestPublicationDetail(Integer id,String collection,String type,String title_alternative,String keywords,String subject_discipline_en,String subject_clc_en,String relation_isIndexedBy_en,String relation_conferencename,String relation_conferencelocation,String publisher,String publisher_location_en,String publisher_city_en,String language,String identifier_volume_en,String url,String identifier_page,String identifier_issue,String identifier_issn_en,String identifier_isilocal_en,String identifier_inspeclocal_en,String identifier_endpage_en,String identifier_doi_en,String identifier_beginpage_en,String format_page_en,String description_sponsorship,String relation_conferencesponsorship_zh,String description_reference_en,String _abstract,String date_issuedMD_en,String date_conferencedate_en,String author_affiliations,String identifier_isbn,String institute_key) {
		this.id = id;
		this.collection = collection;
		this.type = type;
		this.title_alternative = title_alternative;
		this.keywords = keywords;
		this.subject_discipline_en = subject_discipline_en;
		this.subject_clc_en = subject_clc_en;
		this.relation_isIndexedBy_en = relation_isIndexedBy_en;
		this.relation_conferencename = relation_conferencename;
		this.relation_conferencelocation = relation_conferencelocation;
		this.publisher = publisher;
		this.publisher_location_en = publisher_location_en;
		this.publisher_city_en = publisher_city_en;
		this.language = language;
		this.identifier_volume_en = identifier_volume_en;
		this.url = url;
		this.identifier_page = identifier_page;
		this.identifier_issue = identifier_issue;
		this.identifier_issn_en = identifier_issn_en;
		this.identifier_isilocal_en = identifier_isilocal_en;
		this.identifier_inspeclocal_en = identifier_inspeclocal_en;
		this.identifier_endpage_en = identifier_endpage_en;
		this.identifier_doi_en = identifier_doi_en;
		this.identifier_beginpage_en = identifier_beginpage_en;
		this.format_page_en = format_page_en;
		this.description_sponsorship = description_sponsorship;
		this.relation_conferencesponsorship_zh = relation_conferencesponsorship_zh;
		this.description_reference_en = description_reference_en;
		this._abstract = _abstract;
		this.date_issuedMD_en = date_issuedMD_en;
		this.date_conferencedate_en = date_conferencedate_en;
		this.author_affiliations = author_affiliations;
		this.identifier_isbn = identifier_isbn;
		this.institute_key = institute_key;
	}

	public Integer getId() {
		return this.id;
	}
	public String getCollection() {
		return this.collection;
	}
	public String getType() {
		return this.type;
	}
	public String getTitleAlternative() {
		return this.title_alternative;
	}
	public String getKeywords() {
		return this.keywords;
	}
	public String getSubjectDisciplineEn() {
		return this.subject_discipline_en;
	}
	public String getSubjectClcEn() {
		return this.subject_clc_en;
	}
	public String getRelationIsIndexedByEn() {
		return this.relation_isIndexedBy_en;
	}
	public String getRelationConferencename() {
		return this.relation_conferencename;
	}
	public String getRelationConferencelocation() {
		return this.relation_conferencelocation;
	}
	public String getPublisher() {
		return this.publisher;
	}
	public String getPublisherLocationEn() {
		return this.publisher_location_en;
	}
	public String getPublisherCityEn() {
		return this.publisher_city_en;
	}
	public String getLanguage() {
		return this.language;
	}
	public String getIdentifierVolumeEn() {
		return this.identifier_volume_en;
	}
	public String getUrl() {
		return this.url;
	}
	public String getIdentifierPage() {
		return this.identifier_page;
	}
	public String getIdentifierIssue() {
		return this.identifier_issue;
	}
	public String getIdentifierIssnEn() {
		return this.identifier_issn_en;
	}
	public String getIdentifierIsilocalEn() {
		return this.identifier_isilocal_en;
	}
	public String getIdentifierInspeclocalEn() {
		return this.identifier_inspeclocal_en;
	}
	public String getIdentifierEndpageEn() {
		return this.identifier_endpage_en;
	}
	public String getIdentifierDoiEn() {
		return this.identifier_doi_en;
	}
	public String getIdentifierBeginpageEn() {
		return this.identifier_beginpage_en;
	}
	public String getFormatPageEn() {
		return this.format_page_en;
	}
	public String getDescriptionSponsorship() {
		return this.description_sponsorship;
	}
	public String getRelationConferencesponsorshipZh() {
		return this.relation_conferencesponsorship_zh;
	}
	public String getDescriptionReferenceEn() {
		return this.description_reference_en;
	}
	public String getAbstract() {
		return this._abstract;
	}
	public String getDateIssuedMDEn() {
		return this.date_issuedMD_en;
	}
	public String getDateConferencedateEn() {
		return this.date_conferencedate_en;
	}
	public String getAuthorAffiliations() {
		return this.author_affiliations;
	}
	public String getIdentifierIsbn() {
		return this.identifier_isbn;
	}
	public String getInstituteKey() {
		return this.institute_key;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	public void setCollection(String collection) {
		this.collection = collection;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setTitleAlternative(String title_alternative) {
		this.title_alternative = title_alternative;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public void setSubjectDisciplineEn(String subject_discipline_en) {
		this.subject_discipline_en = subject_discipline_en;
	}
	public void setSubjectClcEn(String subject_clc_en) {
		this.subject_clc_en = subject_clc_en;
	}
	public void setRelationIsIndexedByEn(String relation_isIndexedBy_en) {
		this.relation_isIndexedBy_en = relation_isIndexedBy_en;
	}
	public void setRelationConferencename(String relation_conferencename) {
		this.relation_conferencename = relation_conferencename;
	}
	public void setRelationConferencelocation(String relation_conferencelocation) {
		this.relation_conferencelocation = relation_conferencelocation;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public void setPublisherLocationEn(String publisher_location_en) {
		this.publisher_location_en = publisher_location_en;
	}
	public void setPublisherCityEn(String publisher_city_en) {
		this.publisher_city_en = publisher_city_en;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public void setIdentifierVolumeEn(String identifier_volume_en) {
		this.identifier_volume_en = identifier_volume_en;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setIdentifierPage(String identifier_page) {
		this.identifier_page = identifier_page;
	}
	public void setIdentifierIssue(String identifier_issue) {
		this.identifier_issue = identifier_issue;
	}
	public void setIdentifierIssnEn(String identifier_issn_en) {
		this.identifier_issn_en = identifier_issn_en;
	}
	public void setIdentifierIsilocalEn(String identifier_isilocal_en) {
		this.identifier_isilocal_en = identifier_isilocal_en;
	}
	public void setIdentifierInspeclocalEn(String identifier_inspeclocal_en) {
		this.identifier_inspeclocal_en = identifier_inspeclocal_en;
	}
	public void setIdentifierEndpageEn(String identifier_endpage_en) {
		this.identifier_endpage_en = identifier_endpage_en;
	}
	public void setIdentifierDoiEn(String identifier_doi_en) {
		this.identifier_doi_en = identifier_doi_en;
	}
	public void setIdentifierBeginpageEn(String identifier_beginpage_en) {
		this.identifier_beginpage_en = identifier_beginpage_en;
	}
	public void setFormatPageEn(String format_page_en) {
		this.format_page_en = format_page_en;
	}
	public void setDescriptionSponsorship(String description_sponsorship) {
		this.description_sponsorship = description_sponsorship;
	}
	public void setRelationConferencesponsorshipZh(String relation_conferencesponsorship_zh) {
		this.relation_conferencesponsorship_zh = relation_conferencesponsorship_zh;
	}
	public void setDescriptionReferenceEn(String description_reference_en) {
		this.description_reference_en = description_reference_en;
	}
	public void setAbstract(String _abstract) {
		this._abstract = _abstract;
	}
	public void setDateIssuedMDEn(String date_issuedMD_en) {
		this.date_issuedMD_en = date_issuedMD_en;
	}
	public void setDateConferencedateEn(String date_conferencedate_en) {
		this.date_conferencedate_en = date_conferencedate_en;
	}
	public void setAuthorAffiliations(String author_affiliations) {
		this.author_affiliations = author_affiliations;
	}
	public void setIdentifierIsbn(String identifier_isbn) {
		this.identifier_isbn = identifier_isbn;
	}
	public void setInstituteKey(String institute_key) {
		this.institute_key = institute_key;
	}
}
