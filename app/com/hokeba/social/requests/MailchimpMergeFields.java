package com.hokeba.social.requests;

import java.util.Date;

public class MailchimpMergeFields {
	private String FNAME;
	private String LNAME;
	private String BDAY;
	
	public MailchimpMergeFields(String fname, String lname, String bday) {
		FNAME = fname;
		LNAME = lname;
		BDAY = bday;
	}
	
	public MailchimpMergeFields() {
	}
	
	public String getFname() {
		return FNAME;
	}
	public void setFname(String fname) {
		this.FNAME = fname;
	}
	
	public String getLname() {
		return LNAME;
	}
	public void setLname(String lname) {
		this.LNAME = lname;
	}
	
	public String getBday() {
		return BDAY;
	}
	public void setBday(String bday) {
		this.BDAY = bday;
	}
}
