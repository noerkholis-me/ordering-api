package models.mapper;

import com.avaje.ebean.annotation.Sql;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.CommonFunction;

import javax.persistence.Entity;
import java.util.Date;
import java.util.Objects;

@Entity
@Sql
public class MapVoucher {
    @JsonProperty("code")
    private String code;
    @JsonProperty("start_date")
    private Date startDate;
    @JsonProperty("end_date")
    private Date endDate;
    @JsonProperty("status")
    private String status;
    @JsonProperty("masking")
    private String masking;

    public java.lang.String getCode() {
        return masking == null ? code : code+" ("+masking+")";
    }

    public void setCode(java.lang.String code) {
        this.code = code;
    }

    public String getStartDate() {
        return CommonFunction.getDateTime(startDate);
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return CommonFunction.getDateTime(endDate);
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public java.lang.String getStatus() {
        if (Objects.equals(status, "1")){
            return "Used";
        }else if (endDate.compareTo(new Date()) < 0) {
            return "Expired";
        }else{
            return "Active";
        }
    }

    public void setStatus(java.lang.String status) {
        this.status = status;
    }

    public String getMasking() {
        return masking;
    }

    public void setMasking(String masking) {
        this.masking = masking;
    }
}