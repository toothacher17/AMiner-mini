package SchoolSearch.pages.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
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
public class AdminIndex {

	@Property
	List<EditLog> allLogList;

	@Property
	EditLog log;

	@Property
	String requiredTableName;
	
	@Property
	Integer requiredUid;
	
	@Property
	Integer requiredColumnId;
	
	@Property
	String requiredField;
	
	@Property
	Integer requiredState;

	
	@InjectComponent
	Zone tableZone;
	
	@SetupRender
	void setupRender() {
		allLogList=logService.getAllLog();
	}
	
	Object onSuccessFromTableNameForm() {
		prepareData();
		return tableZone.getBody();
	}
	Object onSuccessFromUidForm() {
		prepareData();
		return tableZone.getBody();
	}
	Object onSuccessFromColumnIdForm() {
		prepareData();
		return tableZone.getBody();
	}
	Object onSuccessFromFieldForm() {
		prepareData();
		return tableZone.getBody();
	}
	Object onSuccessFromStateForm() {
		prepareData();
		return tableZone.getBody();
	}
	
	Object onRollBack(EventContext context) {
		Integer logid = context.get(Integer.class, 0);
		requiredUid = context.get(Integer.class, 1);
		requiredTableName = context.get(String.class, 2);
		requiredColumnId = context.get(Integer.class, 3);
		requiredField = context.get(String.class, 4);
		requiredState = context.get(Integer.class, 5);
		logService.rollback(logid);
		prepareData();
		return tableZone.getBody();
	}
	
	void prepareData() {
		List<Object> list = new ArrayList<Object>();
		list.add(requiredUid);
		list.add(requiredTableName);
		list.add(requiredColumnId);
		list.add(requiredField);
		list.add(requiredState);
		allLogList = logService.getConditionalEditLog(list);
	}
	
	public static void main(String[] args) {
		AdminIndex a = new AdminIndex();
	}
	
	@Inject 
	EditLogService logService;
	
	
}
