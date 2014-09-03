package SchoolSearch.components.person;

import java.lang.reflect.Field;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

import SchoolSearch.services.dao.person.model.PersonExt;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonExt;
import SchoolSearch.services.dao.user.model.User;
import SchoolSearch.services.services.auth.Authenticator;
import SchoolSearch.services.services.person.PersonService;

public class PersonExtComponent {
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

	SchooltestPersonExt _personExt;

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
	Zone pextComponentZone;

	void setupRender() {
		_personId = personId;
		_title = title;
		_field = field;
		isEdit = false;
	}

	public SchooltestPersonExt getPersonExt() {
		_personExt = personService.getPersonExt(_personId);

		if (null != _personExt) {
			Field f;
			try {
				f = SchooltestPersonExt.class.getDeclaredField(_field);
				f.setAccessible(true);
				realString = (String) f.get(_personExt);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			if (null != realString) {
				realString = realString.trim();
				displayStrings = realString.split(newLineRegex);
			}
		}
		return _personExt;
	}

	Object onActionFromDoEdit(EventContext context) {
		this._personId = context.get(Integer.class, 0);
		this._field = context.get(String.class, 1);
		this._title = context.get(String.class, 2);
		isEdit = true;
		return pextComponentZone.getBody();
	}

	Object onActionFromDoCancel(EventContext context) {
		this._personId = context.get(Integer.class, 0);
		this._field = context.get(String.class, 1);
		this._title = context.get(String.class, 2);
		isEdit = false;
		return pextComponentZone.getBody();
	}

	Object onSuccess() {
		if (null != realString) {
			realString = realString.trim();
		}
		_personExt = personService.getPersonExt(_personId);
		if (null == _personExt) {
			personService.createPersonExt(_personId);
			_personExt = personService.getPersonExt(_personId);
		}
		personService.updatePersonExt(_personId, _field, realString, _personExt);
		Field f;
		try {
			f = SchooltestPersonExt.class.getDeclaredField(_field);
			f.setAccessible(true);
			f.set(_personExt, realString);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return pextComponentZone.getBody();
	}

	public User getUser() {
		return authenticator.getCurrentUser();
	}

	@Inject
	Authenticator authenticator;

	@Inject
	PersonService personService;
}
