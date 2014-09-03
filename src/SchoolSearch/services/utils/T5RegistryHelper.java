package SchoolSearch.services.utils;

import java.util.Arrays;
import java.util.List;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.test.PageTester;

import SchoolSearch.services.dao.publication.model.Publication;
import SchoolSearch.services.dao.schooltest.model.SchooltestPublication;
import SchoolSearch.services.services.publication.PublicationService;

public class T5RegistryHelper {

	public static Registry registry;

	public static void setRegistry(Registry registry) {
		T5RegistryHelper.registry = registry;
	}

	public static <T> T getService(Class<T> serviceInterface) {
		initStandalone();
		return registry.getService(serviceInterface);
	}

	public synchronized static void initStandalone() {
		if (null == registry) {
			System.out.println("== IOC Start =============");
			PageTester pageTester = new PageTester("SchoolSearch", "SchoolSearch");
			setRegistry(pageTester.getRegistry());
		}
	}

	/**
	 * Standalone only, cleanup.
	 */
	public static void shutdown() {
		System.out.println("== IOC Shutdown =============");
		// for operations done from this thread
		registry.cleanupThread();
		// call this to allow services clean shutdown
		registry.shutdown();
	}

	/**
	 * This is a test.
	 */
//	public static void main2(String[] args) {
//		CommonDAO commonDao = T5RegistryHelper.getService(CommonDAO.class);
//		User user = commonDao.findUniqueWithNamedQuery(UserConstants.NQ_BY_USERNAME,
//				QueryParameters.with("username", Strings.safeTrim("vivo")).parameters());
//		System.out.println(user);
//
//		T5RegistryHelper.shutdown();
//	}

	public static void main(String[] args) {
		PageTester tester = new PageTester("SchoolSearch", "SchoolSearch");
		PublicationService pubService = tester.getService(PublicationService.class);
		List<SchooltestPublication> publications = pubService.getPublications(Arrays.asList(12,22,32));
		for(SchooltestPublication p : publications) {
			System.out.println(p.getTitle());
		}
	}

}
