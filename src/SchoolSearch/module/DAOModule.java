package SchoolSearch.module;

import org.apache.tapestry5.ioc.ServiceBinder;

import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.dao.course.CourseDao;
import SchoolSearch.services.dao.course.Person2CourseDao;
import SchoolSearch.services.dao.editLog.EditLogDao;
import SchoolSearch.services.dao.graduatePublication.GraduatePublicationDao;
import SchoolSearch.services.dao.graduatePublication.Person2GraduatePublicationDao;
import SchoolSearch.services.dao.oranizationLevels.DepartmentDao;
import SchoolSearch.services.dao.oranizationLevels.Institute2DepartmentDao;
import SchoolSearch.services.dao.oranizationLevels.InstituteDao;
import SchoolSearch.services.dao.oranizationLevels.Person2InstituteDao;
import SchoolSearch.services.dao.oranizationLevels.SchoolDao;
import SchoolSearch.services.dao.person.PersonDao;
import SchoolSearch.services.dao.person.PersonExtDao;
import SchoolSearch.services.dao.person.PersonInfoDao;
import SchoolSearch.services.dao.person.PersonInterestDao;
import SchoolSearch.services.dao.person.PersonProfileDao;
import SchoolSearch.services.dao.person.PersonRelationDao;
import SchoolSearch.services.dao.publication.Person2PublicationDao;
import SchoolSearch.services.dao.publication.PublicationDao;
import SchoolSearch.services.dao.schooltest.dao.SchooltestCourseDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestCourseDetailDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestDepartment2schoolDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestDepartmentDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPerson2courseDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPerson2departmentDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPerson2publicationDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPersonDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPersonExtDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPersonInfoDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPersonInterestDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPersonProfileDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPersonrelationDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPublicationDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestPublicationDetailDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestSchoolDAO;
import SchoolSearch.services.dao.schooltest.dao.SchooltestUsereditDAO;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson2publication;
import SchoolSearch.services.dao.user.UserDao;
import SchoolSearch.services.dao.userActionLog.UserActionLogDao;
import SchoolSearch.services.dao.userActionLog.model.UserActionLog;
import SchoolSearch.services.dao.userLog.UserLogDao;

public class DAOModule {

	public static void bind(ServiceBinder binder) {
		binder.bind(PersonDao.class);
		binder.bind(PersonProfileDao.class);
		binder.bind(PersonInfoDao.class);
		binder.bind(PersonExtDao.class);
		binder.bind(PersonInterestDao.class);
		
		binder.bind(SchooltestPersonDAO.class);
		binder.bind(SchooltestPersonProfileDAO.class);
		binder.bind(SchooltestPersonInfoDAO.class);
		binder.bind(SchooltestPersonExtDAO.class);
		binder.bind(SchooltestPersonInterestDAO.class);
		
		binder.bind(SchoolDao.class);
		binder.bind(DepartmentDao.class);
		binder.bind(Institute2DepartmentDao.class);
		binder.bind(InstituteDao.class);
		
		binder.bind(SchooltestSchoolDAO.class);
		binder.bind(SchooltestDepartmentDAO.class);
		binder.bind(SchooltestDepartment2schoolDAO.class);
		binder.bind(SchooltestPerson2departmentDAO.class);
		binder.bind(SchooltestPersonrelationDAO.class);
		
		binder.bind(Person2InstituteDao.class);
		binder.bind(Person2PublicationDao.class);
		binder.bind(PersonRelationDao.class);
		
		binder.bind(SchooltestPerson2publicationDAO.class);
		
		binder.bind(PublicationDao.class);
		binder.bind(GraduatePublicationDao.class);
		binder.bind(Person2GraduatePublicationDao.class);
		
		binder.bind(SchooltestPublicationDAO.class);
		binder.bind(SchooltestPublicationDetailDAO.class);
		
		binder.bind(EditLogDao.class);
		binder.bind(UserLogDao.class);
		binder.bind(UserDao.class);
		binder.bind(UserActionLogDao.class);
		binder.bind(SchooltestUsereditDAO.class);
		
		binder.bind(CourseDao.class);
		binder.bind(Person2CourseDao.class);
		
		binder.bind(SchooltestCourseDAO.class);
		binder.bind(SchooltestCourseDetailDAO.class);
		binder.bind(SchooltestPerson2courseDAO.class);
	}

}
