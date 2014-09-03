package SchoolSearch.services.dao.person.model;

/**
 * @author GCR
 */
import java.util.HashMap;
import java.util.Map;

import SchoolSearch.services.ConsistanceService;
import SchoolSearch.services.utils.Strings;

public class PersonProfile {
	static String path = ConsistanceService.get("avatar.url");

	static Map<String, String> index_id2Avatar = new HashMap<String, String>();
	static {

		index_id2Avatar.put("2301", "xxkxyjsxy-dzgcx/");
		index_id2Avatar.put("2302", "xxkxjsxy-jsjkxyjsx/");
		index_id2Avatar.put("2303", "xxkxjsxy-zdhx/");
		index_id2Avatar.put("2304", "");
		index_id2Avatar.put("2305", "xxkxjsxy-wdzxyjs/");
		index_id2Avatar.put("2306", "xxkxjsxy-rjxy/");

		index_id2Avatar.put("0101", "rwxy-zgyywx/");
		index_id2Avatar.put("0103", "rwxy-ls/");
		index_id2Avatar.put("0104", "rwxy-zx/");

		index_id2Avatar.put("0301", "hthkxy-gclx/");
		index_id2Avatar.put("0302", "hthkxy-hkyhgc/");

		index_id2Avatar.put("0401", "shkxy-shxx/");
		index_id2Avatar.put("0402", "shkxy-zzxx/");
		index_id2Avatar.put("0403", "shkxy-gjgxxx/");
		index_id2Avatar.put("0404", "shkxy-xlxx/");
		index_id2Avatar.put("0405", "");
		index_id2Avatar.put("0406", "shkxy-kxyshyjs/");

		index_id2Avatar.put("0901", "tmslxy-tmgcsx/");
		index_id2Avatar.put("0902", "tmslxy-slsdgc/");
		index_id2Avatar.put("0903", "tmslxy-jsgl/");

		index_id2Avatar.put("1101", "jjglxy-kj/");
		index_id2Avatar.put("1102", "jjglxy-jj/");
		index_id2Avatar.put("1103", "jjglxy-jr/");
		index_id2Avatar.put("1104", "jjglxy-cxcyyzl/");
		index_id2Avatar.put("1105", "jjglxy-ldlyzzgl");
		index_id2Avatar.put("1106", "jjglxy-glkxygc/");
		index_id2Avatar.put("1106", "jjglxy-scyx/");

		index_id2Avatar.put("1200", "ggglxy-gggl/");

	}

	// public static PersonProfile construct(int id, int department_id, Faculty
	// f) {
	// PersonProfile result = new PersonProfile();
	// result.id = id;
	// result.department_id = department_id;
	// result.No = f.No;
	// result.position = f.position;
	// result.department_key = f.index_id;
	// result.location = f.office;
	// result.phone = f.phone;
	// result.email = f.email;
	// if(null != f.url)
	// result.homepage = f.url;
	// if(null != f.homepage)
	// result.homepage = f.homepage;
	// result.imagelink = f.photo;
	// result.author_id = f.author_id;
	// return result;
	// }

	public PersonProfile() {
	}

	public PersonProfile(String position, String location, String phone, String email, String homepage, String imagelink, Long author_id) {
		super();
		this.position = position;
		this.location = location;
		this.phone = phone;
		this.email = email;
		this.homepage = homepage;
		this.imagelink = imagelink;
		this.author_id = author_id;
	}

	public int id;
	public String No;
	public String position;
	public String department_key;
	public int department_id;
	public String location;
	public String phone;
	public String email;
	public String homepage;
	public String imagelink;
	public Long author_id;

	public void updateProfile(String position, String location, String phone, String email, String homepage, String imagelink, Long author_id) {
		this.position = position;
		this.location = location;
		this.phone = phone;
		this.email = email;
		this.homepage = homepage;
		this.imagelink = imagelink;
		this.author_id = author_id;
	}

	public String getAvatar() {
		if (Strings.isEmpty(imagelink) || imagelink.equals("upload"))
			return path + id + ".jpg";
		else
			return imagelink;
	}

	public void setAvatar(String avatar) {
		this.imagelink = avatar;
	}

}
