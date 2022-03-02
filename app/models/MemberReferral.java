package models;

import com.avaje.ebean.*;
import java.util.List;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

import javax.persistence.*;
import java.util.List;
import java.text.ParseException;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "member_referral")
public class MemberReferral extends BaseModel {

	@ManyToOne
    @JoinColumn(name = "member_id")
    @JsonIgnore
    public Member referrer;

	@ManyToOne
    @JoinColumn(name = "referral_id")
    @JsonIgnore
    public Member referral;

	//public Long referral_id;
	
	@JsonGetter ("referrer_id")
	public Long getReferrerId () {
		return referrer.id;
	}
	
	@JsonGetter ("referral_id")
	public Long getReferralId () {
		return referral.id;
	}
	
//	@JsonProperty("member_id")
//    @Column(name = "member_id")
//    public Long member_id;
//	
//	@JsonProperty("referral_id")
//    @Column(name = "referral_id")
//    public Long referral_id;
//	
//
//    @javax.persistence.Transient
//    @Column(name = "member_id")
//    public Long memberId;
//    
//    @javax.persistence.Transient
//    @Column(name = "referral_id")
//    public Long referralId;
	
	public static Finder<Long, MemberReferral> find = new Finder<Long, MemberReferral>(Long.class, MemberReferral.class);
    
    public MemberReferral(Member referrer, Member referral) {
		super();
		this.referrer = referrer;
		this.referral = referral;
	}
    
//    public static List<MemberReferral> getReffererByMember(Long id){
//        String sql = "SELECT  member_id,referral_id FROM member_referral " +
//                "WHERE referral_id = "+id+" " +
//                "ORDER BY member_id ASC";
//
//        RawSql rawSql = RawSqlBuilder.parse(sql)
//                .columnMapping("member_id", "referrer")
//                .columnMapping("referral_id", "referral")
//                .create();
//        com.avaje.ebean.Query<MemberReferral> query = Ebean.find(MemberReferral.class);
//        query.setRawSql(rawSql);
//        List<MemberReferral> resData = query.findList();
//
//        return resData;
//    }

    
    public MemberReferral() {

    }
	

}
