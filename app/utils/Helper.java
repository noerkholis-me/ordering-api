package com.utils;

import java.lang.Exception;
import java.text.SimpleDateFormat;

public class Helper {
  public static boolean isValidDate(String dateRegex,SimpleDateFormat dateFormat,String date) {
    try{
      if(date.matches(dateRegex)){
        dateFormat.parse(date);
        return true;
      } else  {
        return false;
      }
    } catch (Exception ex) {
      return false;
    }
  }
}