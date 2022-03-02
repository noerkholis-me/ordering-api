package models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.elasticsearch.common.joda.time.DateTime;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import play.Logger;
import play.db.ebean.Model.Finder;

@Entity
@Table(name = "loyaltypoint")
public class LoyaltyPoint extends BaseModel {
    private static final long serialVersionUID = 1L;
    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;
    

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="member_id")
    public Member member;
    
//    @Column(name = "member_id")
//    @JsonProperty("member_id")
//    public Long memberId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="transaction_id")
    public SalesOrder salesOrder;
    
//    @Column(name = "transaction_id")
//    @JsonProperty("transaction_id")
//    public Long transactionId;
    
    @Column(name = "point")
    public Long point;
    
    @Column(name = "used")
    public Long used;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Asia/Jakarta")
    @Column(name = "expired_date")
    @JsonProperty("expired_date")
    public Date expiredDate;
    
    @Column(name = "note", columnDefinition = "TEXT")
    public String note;

    public static Finder<Long, LoyaltyPoint> find = new Finder<Long, LoyaltyPoint>(Long.class, LoyaltyPoint.class);

	public LoyaltyPoint() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LoyaltyPoint(Long memberId, Long transactionId, Long point, Long used, Date expiredDate, String note) {
		super();
		this.member = Member.find.byId(memberId);
		if(transactionId!=null){
			this.salesOrder = SalesOrder.find.byId(transactionId);
		}
		this.point = point;
		this.used = used;
		this.expiredDate = expiredDate;
		this.note = note;
	}
	
//	public LoyaltyPoint(Long memberId, Long point, Long used, String note) throws ParseException {
//		super();
//		this.member = Member.find.byId(memberId);
////		this.salesOrder = SalesOrder.find.byId(transactionId);
//		this.point = point;
//		this.used = used;
////		this.expiredDate = new SimpleDateFormat("yyyy-MM-dd").parse(expiredDate);
//		this.note = note;
//	}

	public Long getMemberId() {
		return member.id;
	}

//	public void setMemberId(Long memberId) {
//		this.memberId = memberId;
//	}

	public Long getTransactionId() {
		return salesOrder.id;
	}

//	public void setTransactionId(Long transactionId) {
//		this.transactionId = transactionId;
//	}

	public Long getPoint() {
		return point;
	}

	public void setPoint(Long point) {
		this.point = point;
	}

	public Long getUsed() {
		return used;
	}

	public void setUsed(Long used) {
		this.used = used;
	}

	public Date getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = expiredDate;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	public static LoyaltyPoint reducePoint(Long memberId, Long point, Long transactionId) {
//		Member actor = checkMemberAccessAuthorization();
		Member actor = Member.find.byId(memberId);
		Date currentDate = new Date(System.currentTimeMillis());
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String currentDateString = dateFormat.format(currentDate);
		List<SqlRow> sql = null;
		int pointer = 0;
		LoyaltyPoint lp = null;
		if (actor != null) {
			//			JsonNode json = request().body().asJson();
			//			if(!json.has("loyaltypoint")) {
			//				response.setBaseResponse(0, 0, 0, "invalid json request", null);
			//				return badRequest(Json.toJson(response));
			//			}
			//			long point = json.get("loyaltypoint").asLong();
			long total = point;
			if(point<=countPoint(memberId)) {
//				try {
//					Ebean.beginTransaction();
					String query = "SELECT *" +
							" FROM loyaltypoint" +
							" WHERE" +
							" is_deleted = false "
							+ " and member_id = " + actor.id
							+ " and point > 0"
							+ " and expired_date > " + "'"+currentDateString+"'"
							+ " and point - used > 0" +
							" ORDER BY created_at ASC";
					SqlQuery sqlQuery = Ebean.createSqlQuery(query);
					sql = sqlQuery.findList();

					while(point > 0) {
						lp = LoyaltyPoint.find.byId(sql.get(pointer).getLong("id"));
						if(point >= lp.point-lp.used) {
							point = point-lp.point-lp.used;
							lp.setUsed(lp.point);
						}
						else {
							lp.setUsed(lp.used+point);
							point = 0L;
						}
						lp.update();
						pointer ++;
					}
					LoyaltyPoint usedLoyalty;
					try {
						SalesOrder salesOrder = SalesOrder.find.byId(transactionId);
						usedLoyalty = new LoyaltyPoint(actor.id,salesOrder.id,-1*total,0L,null,"Used Point in transaction " + salesOrder.orderNumber);
						usedLoyalty.save();
						return usedLoyalty;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			return null;
		}
		return null;
	}

	public static boolean addPoint(Long memberId, Long points, Long transactionId, Date expireDate, String notes) {
		//	public static Result addPoint() {
//		Member actor = checkMemberAccessAuthorization();
		Member actor = Member.find.byId(memberId);
		if (actor != null) {
//			try{
//				Ebean.beginTransaction();
			LoyaltyPoint newLoyalty;
			newLoyalty = new LoyaltyPoint(actor.id,transactionId,points,0L,expireDate,notes);
			newLoyalty.save();
				//				LoyaltyPoint newLoyalty = new LoyaltyPoint(actor.id,1612L,500L,0L,"2021-04-17 07:07:22","Got Ponts fron transaction "+1612L);
//				Ebean.commitTransaction();
//			}catch(Exception e) {
//				Ebean.rollbackTransaction();
//				Logger.info(e.toString());
//				response.setBaseResponse(0, 0, 0, notFound, null);
//				return badRequest(Json.toJson(response));
//				return false;
//			}finally {
//				Ebean.endTransaction();
//			}
//			response.setBaseResponse(1, 0, 1, success, "successfully added " + points +" point to member " + actor.id);
//			//			response.setBaseResponse(1, 0, 1, success, "successfully added " + 500L +" point to member " + actor.id);
//			return ok(Json.toJson(response));
			return true;
		}
//		response.setBaseResponse(0, 0, 0, unauthorized, null);
//		return unauthorized(Json.toJson(response));
		return false;
	}
	
	public static boolean addPointReferral(Long id_referral, Long pointsreferral, Long transactionId, Date expireDate, String notes) {
		//	public static Result addPoint() {
//		Member actor = checkMemberAccessAuthorization();
		Long id_referral_new = id_referral;
		if (id_referral_new > 1) {
			System.out.println("Save Point Referral ID");
//			try{
//				Ebean.beginTransaction();
			LoyaltyPoint newLoyalty;
			newLoyalty = new LoyaltyPoint(id_referral_new,transactionId,pointsreferral,0L,expireDate,notes);
			newLoyalty.save();
				//				LoyaltyPoint newLoyalty = new LoyaltyPoint(actor.id,1612L,500L,0L,"2021-04-17 07:07:22","Got Ponts fron transaction "+1612L);
//				Ebean.commitTransaction();
//			}catch(Exception e) {
//				Ebean.rollbackTransaction();
//				Logger.info(e.toString());
//				response.setBaseResponse(0, 0, 0, notFound, null);
//				return badRequest(Json.toJson(response));
//				return false;
//			}finally {
//				Ebean.endTransaction();
//			}
//			response.setBaseResponse(1, 0, 1, success, "successfully added " + points +" point to member " + actor.id);
//			//			response.setBaseResponse(1, 0, 1, success, "successfully added " + 500L +" point to member " + actor.id);
//			return ok(Json.toJson(response));
			return true;
		}
//		response.setBaseResponse(0, 0, 0, unauthorized, null);
//		return unauthorized(Json.toJson(response));
		return false;
	}
	

	public static long countPoint(Long memberId)  {
		Member actor = Member.find.byId(memberId);
		Date currentDate = new Date(System.currentTimeMillis());
		long sum = 0;
		//ObjectMapper om = new ObjectMapper();
		if (actor != null) {
			List<LoyaltyPoint> data = LoyaltyPoint.find.where()
					.eq("is_deleted", false)
					.eq("member_id",actor.id)
					.ge("point", 0)
					.ge("expired_date", currentDate)
					.findList();
			for(LoyaltyPoint sumPoints: data) {
				sum += sumPoints.point-sumPoints.used;
			}
		}
		return sum;
	}
	
}
