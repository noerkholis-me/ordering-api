package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.CommonFunction;

import javax.persistence.*;
import java.util.List;

/**
 * Created by hendriksaragih on 2/4/17.
 */
@Entity
public class ArticleComment extends BaseModel {

    public final static int APPROVED = 1;
    public final static int REJECT = 2;
    public final static int PENDING = 0;


    @JsonIgnore
    @JoinColumn(name = "comment_parent_id")
    @ManyToOne
    public ArticleComment replyFrom;

    @JsonIgnore
    @Column(insertable = false)
    @OneToMany(mappedBy = "replyFrom")
    public List<ArticleComment> replies;

    @JsonIgnore
    @ManyToOne
    public Article article;

    @JsonProperty("article_id")
    public Long articleId() {
        return article.id;
    }

    @JsonProperty("article_title")
    public String articleTitle() {
        return article.title;
    }

    @JsonProperty("article_slug")
    public String articleSlug() {
        return article.slug;
    }

    @JsonProperty("created_at_str")
    public String getCreatedAtStr() {
        return CommonFunction.getDateTime(createdAt);
    }

    @JsonIgnore
    public Long commenterId;
    @JsonIgnore
    public boolean isAdmin;

    @JsonIgnore
    @Column(columnDefinition = "TEXT")
    public String comment;
    @JsonIgnore
    public boolean isRemoved;

    public int status;

    @JoinColumn(name = "approve_by")
    @ManyToOne
    public UserCms userCms;

    @JsonProperty("commenter_name")
    public String getCommenterName() {
        String res = "";
        if (isAdmin) {
            UserCms userTarget = UserCms.find.byId(commenterId);
            res = userTarget.fullName;
        }
        else{
            Member memberTarget = Member.find.byId(commenterId);
            res = memberTarget.fullName;
        }
        return res;
    }

    public String getCommenterNameBe() {
        String res = "";
        if (isAdmin) {
            UserCms userTarget = UserCms.find.byId(commenterId);
            res = "(Admin) "+userTarget.fullName+"<"+userTarget.email+">";
        }
        else{
            Member memberTarget = Member.find.byId(commenterId);
            res = memberTarget.fullName+"<"+memberTarget.email+">";
        }
        return res;
    }

    @JsonProperty("comment_text")
    public String getCommentText() {
        String res = "<This comment was removed.>";
        if (!isRemoved) {
            res = comment;
        }
        return res;
    }

    @JsonProperty("reply")
    public Integer getReply() {
        return RowCount(id);
    }

    @JsonProperty("has_reply")
    public boolean getHasReply(){
        return this.replies.size() != 0;
    }

    public boolean isParent(){
        return replyFrom == null;
    }

    public ArticleComment(Article article, Long parentCommentId, String comment, Long commenterId, boolean isAdmin) {
        if (parentCommentId != null) {
            this.replyFrom = ArticleComment.find.byId(parentCommentId);
        }
        this.article = article;
        this.commenterId = commenterId;
        this.isAdmin = isAdmin;
        this.status = isAdmin ? APPROVED : PENDING;
        this.comment = comment;
        this.isRemoved = false;
    }

    public ArticleComment() {
    }

    public static Finder<Long, ArticleComment> find = new Finder<>(Long.class,
            ArticleComment.class);

    public static String validation(ArticleComment model) {
        if (model.replyFrom!=null && ArticleComment.find.byId(model.replyFrom.id) == null) {
            return "Comment is not found.";
        }
        if (model.comment.equals("")) {
            return "Please insert your comment.";
        }
        if (model.status!=APPROVED && model.status!=REJECT && model.status!=PENDING)
        {
            return "Please check input status.";
        }
        return null;
    }

    public String getStatusName(){
        if (!isRemoved){
            switch (status){
                case PENDING : return "Pending";
                case APPROVED : return "Approved";
                case REJECT : return "Rejected";
            }
        }
        return "Removed";
    }

    public static Page<ArticleComment> page(int page, int pageSize, String sortBy, String order, String name, int status) {
        ExpressionList<ArticleComment> qry = ArticleComment.find
                .where()
                .ilike("comment", "%" + name + "%")
                .eq("is_deleted", false);

        if (status >= 0){
            qry.eq("status", status);
        }

        return
                qry.orderBy(sortBy + " " + order)
                    .findPagingList(pageSize)
                    .setFetchAhead(false)
                    .getPage(page);
    }

    public static Integer RowCount() {
        return find.where().eq("is_deleted", false).findRowCount();
    }

    public static Integer RowCount(Long id) {
        return find.where().eq("is_deleted", false).eq("comment_parent_id", id).findRowCount();
    }

    public static List<ArticleComment> getComments(Long articleId){
        return find.where().eq("is_deleted", false)
                .eq("article_id", articleId).eq("status", APPROVED)
                .eq("comment_parent_id", null)
                .setOrderBy("created_at DESC").findList();
    }

}