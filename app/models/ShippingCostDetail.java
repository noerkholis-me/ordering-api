package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Calendar;
import java.util.Date;

@Entity
public class ShippingCostDetail extends BaseModel{
    private static final long serialVersionUID = 1L;

    @ManyToOne(optional=false)
    public ShippingCost shippingCost;

    @ManyToOne
    public CourierService service;

    public String description;
    public Double cost;
    public int estimatedTimeDelivery;


    public static Finder<Long, ShippingCostDetail> find = new Finder<>(Long.class, ShippingCostDetail.class);

    public Long getType(){
        return service.id;
    }

    public Date getDelivered(){
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, estimatedTimeDelivery);
        return c.getTime();
    }

    public Date getDelivered(Date sdate){
        Calendar c = Calendar.getInstance();
        c.setTime(sdate);
        c.add(Calendar.DATE, estimatedTimeDelivery);
        return c.getTime();
    }

    public Double calculateCost(Double weights, Double volumes){
        Double result = 0D;
        switch (service.courier.type){
            case 1 : // Volumetric
                Double volume = Math.ceil(volumes/service.courier.divider);
                Double weight = Math.ceil(weights/1000.0);
                if (weight > volume){
                    result = weight * cost;
                }else {
                    result = volume * cost;
                }
                break;
            case 2 : // Volume
                result = Math.ceil(volumes/5000.0) * cost;
                break;
            case 3 : // Weight
                result = Math.ceil(weights/1000.0) * cost;
                break;
        }

        return result;
    }

    public String getServiceName(){
        return service.courier.name +" - "+service.service;
    }

    public static void seed(ShippingCost shippingCost, CourierService service, String desc, Double cost, int estimatedTimeDelivery){
        ShippingCostDetail model = new ShippingCostDetail();
        model.shippingCost = shippingCost;
        model.description = desc;
        model.service = service;
        model.cost = cost;
        model.estimatedTimeDelivery = estimatedTimeDelivery;
        model.save();
    }
}