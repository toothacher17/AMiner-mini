package SchoolSearch.module;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.services.ComponentRequestFilter;
import org.apache.tapestry5.services.ComponentRequestHandler;
import org.apache.tapestry5.validator.ValidatorMacro;

import SchoolSearch.services.admin.MergePersonService;
import SchoolSearch.services.admin.impl.MergePersonServiceImpl;
import SchoolSearch.services.cache.CourseCacheService;
import SchoolSearch.services.cache.GraduatePublicationCacheService;
import SchoolSearch.services.cache.OrganizationLevelsCacheService;
import SchoolSearch.services.cache.OrganizationRelationCacheService;
import SchoolSearch.services.cache.PersonCacheService;
import SchoolSearch.services.cache.PersonRelationCacheService;
import SchoolSearch.services.cache.PublicationCacheService;
import SchoolSearch.services.cache.impl.CourseCacheServiceImpl;
import SchoolSearch.services.cache.impl.GraduatePublicationCacheServiceImpl;
import SchoolSearch.services.cache.impl.OrganizationLevelsCacheServiceImpl;
import SchoolSearch.services.cache.impl.OrganizationRelationCacheServiceImpl;
import SchoolSearch.services.cache.impl.PersonCacheServiceImpl;
import SchoolSearch.services.cache.impl.PersonRelationCacheServiceImpl;
import SchoolSearch.services.cache.impl.PublicationCacheServiceImpl;
import SchoolSearch.services.dao.userActionLog.model.UserActionLog;
import SchoolSearch.services.monitor.MonitorService;
import SchoolSearch.services.monitor.impl.MonitorServiceImpl;
import SchoolSearch.services.services.auth.Authenticator;
import SchoolSearch.services.services.auth.annotation.filter.RequireLoginFilter;
import SchoolSearch.services.services.auth.impl.AuthenticatorImpl;
import SchoolSearch.services.services.course.CourseService;
import SchoolSearch.services.services.course.impl.CourseServiceImpl;
import SchoolSearch.services.services.editLog.EditLogService;
import SchoolSearch.services.services.editLog.impl.EditLogServiceImpl;
import SchoolSearch.services.services.graduatePublication.GraduatePublicationService;
import SchoolSearch.services.services.graduatePublication.impl.GraduatePublicationServiceImpl;
import SchoolSearch.services.services.organizationLevels.DepartmentService;
import SchoolSearch.services.services.organizationLevels.InstituteService;
import SchoolSearch.services.services.organizationLevels.SchoolService;
import SchoolSearch.services.services.organizationLevels.impl.DepartmentServiceImpl;
import SchoolSearch.services.services.organizationLevels.impl.InstituteServiceImpl;
import SchoolSearch.services.services.organizationLevels.impl.SchoolServiceImpl;
import SchoolSearch.services.services.person.PersonRelationService;
import SchoolSearch.services.services.person.PersonService;
import SchoolSearch.services.services.person.impl.PersonRelationServiceImpl;
import SchoolSearch.services.services.person.impl.PersonServiceImpl;
import SchoolSearch.services.services.prominent.ProminentService;
import SchoolSearch.services.services.prominent.impl.ProminentServiceImpl;
import SchoolSearch.services.services.publication.PublicationService;
import SchoolSearch.services.services.publication.impl.PublicationServiceImpl;
import SchoolSearch.services.services.suggestion.SuggestionService;
import SchoolSearch.services.services.suggestion.impl.SuggestionServiceImpl;
import SchoolSearch.services.services.user.UserService;
import SchoolSearch.services.services.user.impl.UserServiceImpl;
import SchoolSearch.services.services.userActionLog.UserActionLogService;
import SchoolSearch.services.services.userActionLog.impl.UserActionLogServiceImpl;
import SchoolSearch.services.services.userEditLog.UserEditLogService;
import SchoolSearch.services.services.userEditLog.impl.UserEditLogServiceImpl;
import SchoolSearch.services.services.userLog.UserLogService;
import SchoolSearch.services.services.userLog.impl.UserLogServiceImpl;
import SchoolSearch.services.suggestion.TrieHandle;
import SchoolSearch.services.suggestion.impl.TrieHandleImpl;

public class ServicesModule {

	public static void bind(ServiceBinder binder) {
		binder.bind(PersonCacheService.class, PersonCacheServiceImpl.class);
		binder.bind(OrganizationLevelsCacheService.class, OrganizationLevelsCacheServiceImpl.class);
		binder.bind(PersonRelationCacheService.class, PersonRelationCacheServiceImpl.class);
		binder.bind(OrganizationRelationCacheService.class, OrganizationRelationCacheServiceImpl.class);
		
		binder.bind(CourseCacheService.class, CourseCacheServiceImpl.class);
		binder.bind(PublicationCacheService.class, PublicationCacheServiceImpl.class);
		binder.bind(GraduatePublicationCacheService.class, GraduatePublicationCacheServiceImpl.class);
		//-------
		binder.bind(PersonService.class, PersonServiceImpl.class);
		binder.bind(PersonRelationService.class, PersonRelationServiceImpl.class);
		//----------
		binder.bind(SchoolService.class, SchoolServiceImpl.class);
		binder.bind(DepartmentService.class, DepartmentServiceImpl.class);
		binder.bind(InstituteService.class, InstituteServiceImpl.class);
		binder.bind(ProminentService.class, ProminentServiceImpl.class);
		
		binder.bind(CourseService.class, CourseServiceImpl.class);
		binder.bind(PublicationService.class, PublicationServiceImpl.class);
		binder.bind(GraduatePublicationService.class, GraduatePublicationServiceImpl.class);
		
		
		
//		binder.bind(PersonGraduatePublicationService.class, GraduatePublicationServiceImpl.class);
		binder.bind(EditLogService.class, EditLogServiceImpl.class);
		binder.bind(UserLogService.class, UserLogServiceImpl.class);
		
		binder.bind(SuggestionService.class, SuggestionServiceImpl.class);
		
		binder.bind(UserService.class, UserServiceImpl.class);
		binder.bind(UserActionLogService.class, UserActionLogServiceImpl.class);
		binder.bind(UserEditLogService.class, UserEditLogServiceImpl.class);
		
		binder.bind(Authenticator.class, AuthenticatorImpl.class);
	
		binder.bind(MergePersonService.class, MergePersonServiceImpl.class);
		
		binder.bind(MonitorService.class, MonitorServiceImpl.class);
		
		binder.bind(TrieHandle.class, TrieHandleImpl.class);
	}
	
	@Contribute(ValidatorMacro.class)
	public static void combineValidators(MappedConfiguration<String, String> configuration) {
//		configuration.add("username", "required, minlength=3, maxlength=25");
//		configuration.add("password", "required, minlength=3, maxlength=18");
		configuration.add("upload.requestsize-max", "20480 kilobytes");
		configuration.add("upload.filesize-max", "20480 kilobytes");
	}
	
	
	

	@Contribute(ComponentRequestHandler.class)
	public static void contributeComponentRequestHandler(OrderedConfiguration<ComponentRequestFilter> configuration) {
		configuration.addInstance("RequiresLogin", RequireLoginFilter.class);
	}

}
