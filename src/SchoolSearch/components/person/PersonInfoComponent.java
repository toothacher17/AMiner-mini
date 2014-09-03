package SchoolSearch.components.person;

import java.lang.reflect.Field;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

//import SchoolSearch.services.dao.person.model.PersonInfo;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonInfo;
import SchoolSearch.services.dao.user.model.User;
import SchoolSearch.services.monitor.MonitorService;
import SchoolSearch.services.services.auth.Authenticator;
import SchoolSearch.services.services.person.PersonService;
import SchoolSearch.services.services.userEditLog.UserEditLogService;
import SchoolSearch.services.utils.Strings;

@Import(library="context:/res/js/person/personinfoComponent.js")
public class PersonInfoComponent {
	@Parameter(allowNull = false)
	Integer personId;

	@Parameter(allowNull = false, defaultPrefix = BindingConstants.LITERAL)
	String field;
	
	@Parameter(allowNull = false, defaultPrefix = BindingConstants.LITERAL)
	String title;

	@Parameter(allowNull = true, defaultPrefix = BindingConstants.LITERAL, value = "[;；\n]+")
	String newLineRegex;

	@Property
	Integer _personId;
	
	SchooltestPersonInfo _personInfo;

	@Property
	String _title;
	
	@Property
	String _field;
	
	@Property
	String[] displayStrings;
	
	@Property
	String displayString;
	
	@Property
	String realString;

	@Property 
	boolean isEdit;
	
	@InjectComponent
	Zone pinfoComponentZone;
	
	void setupRender() {
		_personId = personId;
		_title = title;
		_field = field;
		isEdit = false;
	}
	public Boolean getHide(){
		getPersonInfo();
		if(getUser() == null && Strings.isEmpty(realString)){
			return true;
		}
		return false;
	}
	public SchooltestPersonInfo getPersonInfo() {
		_personInfo = personService.getPersonInfo(_personId);
		if(null != _personInfo && Strings.isEmpty(realString)) {
			Field f;
			try {
				f = SchooltestPersonInfo.class.getDeclaredField(_field);
				f.setAccessible(true);
				realString = (String) f.get(_personInfo);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			if(null != realString)
				displayStrings = realString.split(newLineRegex);
		}
		return _personInfo;
	}
	
	Object onActionFromDoEdit(EventContext context) {
		this._personId  = context.get(Integer.class, 0);
		this._field = context.get(String.class, 1);
		this._title = context.get(String.class, 2);
		isEdit = true;
		return pinfoComponentZone.getBody();
	}
	
	Object onActionFromDoCancel(EventContext context) {
		this._personId  = context.get(Integer.class, 0);
		this._field = context.get(String.class, 1);
		this._title = context.get(String.class, 2);
		isEdit = false;
		return pinfoComponentZone.getBody();
	}
	
	Object onSuccess() {
		_personInfo = personService.getPersonInfo(_personId);
		if(null == _personInfo) {
			personService.createPersonInfo(_personId);
			_personInfo = personService.getPersonInfo(_personId);
		}
		personService.updatePersonInfo(_personId, _field, realString, _personInfo);
		Field f;
		try {
			f = SchooltestPersonInfo.class.getDeclaredField(_field);
			f.setAccessible(true);
			String originalValue = "null";
			if(null != f.get(_personInfo)) 
				originalValue = f.get(_personInfo).toString();
			monitorService.singleEditAction("personinfo", _field, originalValue, realString, _personId);
			
			f.set(_personInfo, realString);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return pinfoComponentZone.getBody();
	}
	
	public User getUser() {
		return authenticator.getCurrentUser();
	}
	
	@Inject
	Authenticator authenticator;
	
	@Inject
	PersonService personService;
	
	@Inject
	MonitorService monitorService;

}
