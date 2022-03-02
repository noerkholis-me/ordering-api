package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
public class MemberAddress extends BaseModel{
	private static final long serialVersionUID = 1L;

	public String name;
	public String note;
	public String latitude;
	public String longitude;
	
	@JsonInclude
	@ManyToOne
	public Member member;
	
    public static Page<MemberAddress> page(int page, int pageSize, Long memberId) {
		return
				find.where()
				.eq("member.id", memberId)
				.orderBy("t0.created_at desc")
				.findPagingList(pageSize)
				.setFetchAhead(false)
				.getPage(page);
	}

    public static Finder<Long, MemberAddress> find = new Finder<Long, MemberAddress>(Long.class, MemberAddress.class);

}
