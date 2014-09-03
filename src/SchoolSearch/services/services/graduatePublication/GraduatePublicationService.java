package SchoolSearch.services.services.graduatePublication;
/**
 * @author GCR
 */
import java.util.List;

import SchoolSearch.services.dao.graduatePublication.model.GraduatePublication;
import SchoolSearch.services.dao.graduatePublication.model.Person2GraduatePublication;

public interface GraduatePublicationService {
	List<GraduatePublication> getStudentGraduatePublicationsByPersonId(
			Integer id);

	List<GraduatePublication> getTutorGraduatePublicationsByPersonId(Integer id);

	List<Integer> getGraduateStudentPublicationIdListByPersonId(Integer id);

	List<Integer> getGraduateTutorPublicationIdListByPersonId(Integer id);

	List<GraduatePublication> getGraduatePublicationsByPersonIds(
			List<Integer> ids);
	
	List<GraduatePublication> getGraduatePublications(List<Integer> grapubids);
	
	List<Person2GraduatePublication> getPersonGraduatePublicationsBygrapubId(Integer id);
	
	List<Person2GraduatePublication> getPersonGraduatePublicationsByPersonId(Integer person_id);
	
	List<GraduatePublication> getAllGraduatePublications();
}
