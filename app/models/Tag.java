package models;

import com.avaje.ebean.Expr;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.*;


/**
 * Created by hendriksaragih on 2/4/17.
 */
@Entity
public class Tag extends BaseModel {
    @Column(unique = true)
    public String name;

    @JsonIgnore
    @ManyToMany
    public List<Article> articles;

    @JsonIgnore
    @ManyToMany
    public List<Product> products;


    public static Finder<Long, Tag> find = new Finder<Long, Tag>(Long.class, Tag.class);


    public static List<Tag> applyTag(String stringTags) {
        return applyTag(Arrays.asList(stringTags.split(",")));
    }

    public static List<Tag> applyTag(List<String> set) {
        Set<String> sets = new HashSet<>(set);
        List<Tag> result = new ArrayList<>();
        for (String name : sets) {
            if (!name.trim().equals("")) {
                Tag check = Tag.find.where().ieq("name", name).findUnique();
                if (check == null) {
                    check = new Tag();
                    check.name = name.toLowerCase();
                    check.save();
                }
                result.add(check);
            }
        }
        return result;
    }

    public static Set<Tag> getArticleTag(){
        Set<Tag> result = Tag.find.fetch("articles").where().not(Expr.eq("articles.id", null)).findSet();
        return result;
    }

    public static Set<Tag> getProductTag(){
        Set<Tag> result = Tag.find.fetch("products").where().not(Expr.eq("products.id", null)).findSet();
        return result;
    }


}