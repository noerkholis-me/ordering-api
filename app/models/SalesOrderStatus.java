package models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.mapping.request.MapVoucherCode;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Constant;

import play.Logger;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hendriksaragih on 4/26/17.
 */
@Entity
public class SalesOrderStatus extends BaseModel {
    private static final long serialVersionUID = 1L;

    public static Finder<Long, SalesOrderStatus> find = new Finder<>(Long.class,
            SalesOrderStatus.class);

        
    public String status;

    
    
}
