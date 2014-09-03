package SchoolSearch.pages.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.dao.editLog.model.EditLog;
import SchoolSearch.services.services.auth.annotation.RequireLogin;
import SchoolSearch.services.services.editLog.EditLogService;
import SchoolSearch.services.utils.Strings;

@Import()
@RequireLogin(admin=true)
public class TestIndex {
	@Property
	Integer personId;
	
	Object setupRender() {
		personId = 13;
		return true;
	}
}
