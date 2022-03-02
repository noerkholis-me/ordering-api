package com.hokeba.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hendriksaragih on 7/26/17.
 */
public class StaticText {
    private static StaticText instance = null;
    private List<String> notifLists = new ArrayList<>();

    public StaticText(){
        notifLists = Arrays.asList("",
                "Thank you for transacting in Hladal, your order with ID {OrderID} have been received. Please proceed to make payment and payment confirmation within 48 hours",
                "The payment for Your order with ID {OrderID} have been confirmed. Your order will be sent to you within 2-3 working days. Thank you for transacting with us",
                "Your order with ID {OrderID} is on its way",
                "Your order with ID {OrderID} has been sent. We hope you are satisfied with our service",
                "Your returned product with ID {OrderID} {Product_Name} {Qty} has been received. Please wait for our retur QC process",
                "Your product return request has been accepted, we will make a replacement for the product you purchased soon",
                "Your product return request has been accepted. We will proceed to make a refund to you soon",
                "No Need, communication will be via Customer Service");
    }

    public static StaticText getInstance() {
        if (instance == null) {
            instance = new StaticText();
        }
        return instance;
    }

    public String getTextNotif(Integer idx){
        return notifLists.get(idx);
    }
}
