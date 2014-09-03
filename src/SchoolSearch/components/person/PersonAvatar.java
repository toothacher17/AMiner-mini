package SchoolSearch.components.person;

import java.io.File;

import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.upload.services.UploadedFile;

import SchoolSearch.pages.person.PersonIndex;
import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.dao.person.model.Person;
import SchoolSearch.services.dao.person.model.PersonProfile;
import SchoolSearch.services.dao.schooltest.model.SchooltestPerson;
import SchoolSearch.services.dao.schooltest.model.SchooltestPersonProfile;
import SchoolSearch.services.dao.user.model.User;
import SchoolSearch.services.services.auth.Authenticator;
import SchoolSearch.services.services.person.PersonService;

@Import(library = ("context:/res/js/jquery.form.js"))
public class PersonAvatar {
	
	@Parameter(allowNull = false)
	Integer personId;

	@Property
	Integer _personId;

	@Persist(PersistenceConstants.FLASH)
	@Property
	private String errorMessage;

	@Property
	Boolean isEditAvatar;
	
	@Property
	String name;
	
	@Property
	String avatar;
	
	@Property
	String imagelink;

	@Property
	UploadedFile file;

	@InjectComponent
	Zone avatarZone;
	
	@InjectPage
	PersonIndex perosnIndex;
	
	void setupRender() {
//		isEditAvatar = true;
		this._personId = personId;
		prepareData(_personId);
	}

	public Object onSuccessFromUploadAvatarForm() {
		if (null != file) {
			if(file.getSize() > 20 * 1024 * 1024) {
				errorMessage = "上传文件过大，超过20M";
				isEditAvatar = true;
			} else {
				File saveDist = new File(avatarPath + _personId + ".jpg");
				if(saveDist.exists())
					saveDist.delete();
				file.write(saveDist);
				prepareData(_personId);
				imagelink = "upload";
			}
		} 
		SchooltestPersonProfile personProfile = personService.getPersonProfile(_personId);
		personService.updatePersonProfileAvatar(_personId, imagelink, personProfile);
		personProfile.setImagelink(imagelink);
		isEditAvatar = false;
		
		perosnIndex.activate(_personId);
		return perosnIndex;
	}
	
	public Object onActionFromUploadAvatar(Integer personId) {
		_personId = personId;
		isEditAvatar = true;
		prepareData(_personId);
		return avatarZone.getBody();
	}
	public Object onActionFromCancel(Integer personId) {
		_personId = personId;
		isEditAvatar = false;
		prepareData(_personId);
		return avatarZone.getBody();
	}

	private void prepareData(int personId) {
		if(null == isEditAvatar) {
			isEditAvatar = false;
		}
		SchooltestPerson person = personService.getPerson(_personId);
		name = person.getName();
		SchooltestPersonProfile profile = personService.getPersonProfile(personId);
		imagelink = profile.getImagelink();
//		avatar = profile.getAvatar() + "?v=" + System.currentTimeMillis()%100;
		avatar = personService.getAvatar(_personId) + "?v=" + System.currentTimeMillis()%100;
	}

	public User getUser() {
		return authenticator.getCurrentUser();
	}

	@Property
	static String defaultAvatar = ConsistanceService.get("avatar.default");
	
	static String avatarPath = ConsistanceService.get("avatar.path");

	@Inject
	Authenticator authenticator;

	@Inject
	PersonService personService;
}
