package com.hokeba.util;

import java.io.IOException;

import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

import play.Play;

public class EmailSendGrid {
	private Mail mail;
	private SendGrid sendGrid;
	private Request request;

	public EmailSendGrid() {
		// TODO Auto-generated constructor stub
		this.sendGrid = new SendGrid(Play.application().configuration().getString("play.sendgrid.apikey"));
		this.request = new Request();
	}

	public void create(String to, String subject, String content_value) {
		String emailFromAddress = Play.application().configuration().getString("play.sendgrid.email_default");
		String emailFromName = Play.application().configuration().getString("play.sendgrid.email_name");

		Email emailFrom = new Email(emailFromAddress, emailFromName);
		Email emailTo = new Email(to);
		Content content = new Content("text/html", content_value);
		this.mail = new Mail(emailFrom, subject, emailTo, content);
	}

	public Response send() throws IOException {
		this.request.setMethod(Method.POST);
		this.request.setEndpoint("mail/send");
		this.request.setBody(this.mail.build());
		Response response = this.sendGrid.api(this.request);
		return response;
	}

	public Response createRecepient(String email, String name) throws IOException {
		String body = new String();
		if (name == null || name.equals("")) {
			body = ("[{" + "\"email\":\"" + email + "\"" + "}]");
		} else {
			body = ("[{" + "\"email\":\"" + email + "\"," + "\"first_name\": \"" + name + "\"" + "}]");
		}
		this.request.setMethod(Method.POST);
		this.request.setEndpoint("/contactdb/recipients");
		this.request.setBody(body);
		Response response = this.sendGrid.api(this.request);
		return response;
	}
}
