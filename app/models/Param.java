package models;

import play.db.ebean.Model;

import javax.persistence.Entity;

/**
 * Created by nugraha on 5/24/17.
 */
@Entity
public class Param extends Model{
    public String param;

    public String code;

    public String value;

    public Param(String param, String code, String value) {
        this.param = param;
        this.code = code;
        this.value = value;
    }

    public static Finder<Long, Param> find = new Finder<>(Long.class, Param.class);

}
