package models;

import com.avaje.ebean.Page;

import javax.persistence.Entity;

@Entity
// USED AS 'PROVINSI' IN INDONESIA
public class Region extends BaseModel {
	private static final long serialVersionUID = 1L;

	public String code;
	public String name;

	public Region() {
	};

	public Region(long id, String name) {
		super();
		this.id = id;
		this.code = id + "";
		this.name = name;
	}

	public static Finder<Long, Region> find = new Finder<>(Long.class, Region.class);

	public static Page<Region> page(int page, int pageSize, String sortBy, String order, String filter) {
		return find.where().ilike("name", "%" + filter + "%").eq("is_deleted", false).orderBy(sortBy + " " + order)
				.findPagingList(pageSize).setFetchAhead(false).getPage(page);
	}

	public static Integer RowCount() {
		return find.where().eq("is_deleted", false).findRowCount();
	}

	public static void seed(String code, String name) {
		Region model = new Region();
		model.code = code;
		model.name = name;
		model.isDeleted = false;
		model.save();
	}
}