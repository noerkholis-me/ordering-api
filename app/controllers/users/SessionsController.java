package controllers.users;

import assets.JsonMask;

import java.util.*;

import assets.Tool;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Expression;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.api.UserSession;
import com.hokeba.http.response.global.ServiceResponse;
import com.hokeba.mapping.response.MapOrderMerchantList;
import com.hokeba.social.response.AppleIdUserOauth;
import com.hokeba.social.requests.MailchimpCustomerRequest;
import com.hokeba.social.requests.MailchimpSubscriberRequest;
import com.hokeba.social.response.FacebookUser;
import com.hokeba.social.response.GoogleOauthUserinfoResponse;
import com.hokeba.social.response.GoogleOauthUserinfoSandboxResponse;
import com.hokeba.social.response.GooglePlusUser;
import com.hokeba.social.response.GooglePlusUserOauth;
import com.hokeba.social.service.FacebookService;
import com.hokeba.social.service.GooglePlusService;
import com.hokeba.social.service.MailchimpService;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Constant;
import com.hokeba.util.Encryption;
import com.hokeba.util.MailConfig;
import com.hokeba.util.OtpGenerator;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import models.*;
import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.Result;
import repository.MemberRepository;
import sendinblue.ApiClient;
import sendinblue.ApiException;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.AccountApi;
import sibApi.EmailCampaignsApi;
import sibApi.TransactionalEmailsApi;
import sibModel.CreateEmailCampaign;
import sibModel.CreateEmailCampaignRecipients;
import sibModel.CreateEmailCampaignSender;
import sibModel.GetAccount;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailTo;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Created by hendriksaragih on 2/28/17.
 */
@Api(value = "/users/sessions", description = "Session")
public class SessionsController extends BaseController {

	private final static Logger.ALogger logger = Logger.of(SessionsController.class);

	private static final char[] saltStr = null;
	@SuppressWarnings("rawtypes")
	private static BaseResponse response = new BaseResponse();
	private static Map<String, String> messageDescription = new LinkedHashMap<>();
	final String baseUrl = Play.application().configuration().getString("whizliz.images.url");

	private static String referral_code;

	@ApiOperation(value = "Sign in", notes = "Sign in.\n" + swaggerInfo
			+ "", response = UserSession.class, httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "login form", dataType = "temp.swaggermap.LoginForm", required = true, paramType = "body", value = "login form") })

	public static Result signIn() {
		JsonNode json = request().body().asJson();
		Logger.debug("checkAccessAuthorization = " + checkAccessAuthorization("guest"));
		Logger.debug("email = " + json.has("email"));

		System.out.println("check login");

		if (checkAccessAuthorization("guest") == 200) {
			if (json.has("email") && json.has("password") && json.has("device_model") && json.has("device_id")
					&& json.has("device_type")) {
				String preEmail = json.findPath("email").asText();
				String password = json.findPath("password").asText();
				String deviceModel = json.findPath("device_model").asText();
				String deviceType = json.findPath("device_type").asText();
				String deviceId = json.findPath("device_id").asText();

				String email = preEmail.toLowerCase();
				Logger.info("original email :" + preEmail + "; lowercase email : " + email);

				Member member = null;
				if (email.matches(CommonFunction.emailRegex)) {
					member = Member.login(email, password);
				} else if (email.matches(CommonFunction.phoneRegex)) {
					member = Member.loginByPhone(email, password);
					// } else if (email.matches(CommonFunction.usernameRegex)) {
					// member = Member.loginByUsername(email, password);
				}
				if (member != null) {
					if (member.isActive) {
						try {
							GetPointsFromSignIn(deviceModel, member);
							MemberLog log = MemberLog.loginMember(deviceModel, deviceType, deviceId, member);
							if (log == null) {
								response.setBaseResponse(0, 0, 0, inputParameter, null);
								return badRequest(Json.toJson(response));
							}
							DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
							UserSession session = new UserSession(log.token, df.format(log.expiredDate),
									log.memberType);
							ObjectMapper om = new ObjectMapper();
							om.addMixInAnnotations(Member.class, JsonMask.class);
							session.setProfile_data(Json.parse(om.writeValueAsString(member)));

							response.setBaseResponse(1, 0, 1, success, session);
							return ok(Json.toJson(response));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						response.setBaseResponse(0, 0, 0, error, null);
						return badRequest(Json.toJson(response));
					} else {
						response.setBaseResponse(0, 0, 0,
								"Your account hasn't actived, please check and verify from your email", null);
						return badRequest(Json.toJson(response));
					}
				}
				response.setBaseResponse(0, 0, 0, "Wrong username/email or password", null);
				return badRequest(Json.toJson(response));
			}
			response.setBaseResponse(0, 0, 0, inputParameter, null);
			return badRequest(Json.toJson(response));
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}

	public Result sendSmsRegister() {

		JsonNode json = request().body().asJson();

		String email = json.get("email").asText().toLowerCase();
		String code = OtpGenerator.randomString(4);

		try {
			Member member = Member.find.where().eq("email", email).findUnique();
			if (member != null)
				return forbidden("Email already exist.");

			return ok(Json.toJson(email));

		} catch (Exception e) {
			Logger.error("", e);
			return internalServerError();
		}
	}


	public void createContentEmail(String email, String title, String otpNumber) {
		StringBuilder contentBuilder = new StringBuilder();
		FileReader fileReader;
		try {
			fileReader = new FileReader(Play.application().path() + File.separator + "public/otp_template.html");
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String strBuffer;

			while ((strBuffer = bufferedReader.readLine()) != null) {
				contentBuilder.append(strBuffer);
			}

			bufferedReader.close();
			fileReader.close();
			String content = contentBuilder.toString();
			content = content.replace("#image_header#", baseUrl + "public/ic-got-beef.png");
			content = content.replace("#otp_number#", otpNumber);
			sendEmail(title, content, email);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendEmail(String subject, String content, String email) {

		final String username = "hellobisnis21@gmail.com";
		final String passxword = "A12345678!";
		final String fromEmail = "hellobisnis21@gmail.com";

		Session session = Session.getInstance(emailProps(), new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, passxword);
			}
		});

		try {
			Runnable runnable = () -> {
				try {
					Message message = new MimeMessage(session);
					message.setFrom(new InternetAddress(fromEmail));
					message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
					message.setSubject(subject);
					message.setContent(content, "text/html; charset=utf-8");
					Transport.send(message);
					Logger.info("email was sent");
				} catch (Exception e) {
					Logger.error("Email not send", e);
				}
			};
			new Thread(runnable).start();
		} catch (Exception e2) {
			Logger.error("Email not send", e2);
		}
	}

	public static Result registerUser() {
		int authority = checkAccessAuthorization("all");
		if (authority == 200 || authority == 203) {
			JsonNode json = request().body().asJson();
			String phone = json.get("phone").asText().toLowerCase();
			Long merchantId = json.get("merchant_id").asLong();
			try {
				String validatePhoneNumber = validateRegister(phone);
				if (!validatePhoneNumber.equalsIgnoreCase("")) {
					response.setBaseResponse(0, 0, 0, inputParameter + " " + validatePhoneNumber, null);
					return badRequest(Json.toJson(response));
				}

				Optional<Member> member = MemberRepository.findByPhoneAndMerchantId(phone, merchantId);
				if (member.isPresent()) {
					response.setBaseResponse(0, 0, 0, inputParameter + " phone number is exists.", null);
					return badRequest(Json.toJson(response));
				}

				Merchant merchant = Merchant.merchantGetId(merchantId);
				if (merchant == null) {
					response.setBaseResponse(0, 0, 0, inputParameter + " merchant not found.", null);
					return badRequest(Json.toJson(response));
				}

				Member newMember = new Member();
				newMember.phone = phone;
				newMember.firstName = "";
				newMember.lastName = "";
				newMember.fullName = "";
				newMember.email = "";
				newMember.lastPurchase = null;
				newMember.setMerchant(merchant);
				newMember.isActive = true;
				newMember.save();

				response.setBaseResponse(1, 0, 0, success + " register new customer", newMember.phone);
				return ok(Json.toJson(response));
			} catch (Exception e) {
				e.printStackTrace();
				response.setBaseResponse(0, 0, 0, error, null);
				return internalServerError(Json.toJson(response));
			}
		} else if (authority == 403) {
			response.setBaseResponse(0, 0, 0, forbidden, null);
			return forbidden(Json.toJson(response));
		} else {
			response.setBaseResponse(0, 0, 0, unauthorized, null);
			return unauthorized(Json.toJson(response));
		}
	}

	private static String validateRegister(String phoneNumber) {
		String errMessage = "";
		if (!phoneNumber.isEmpty()) {
			if (!phoneNumber.matches(CommonFunction.phoneRegex)) {
				errMessage += "Phone format not valid.";
			}
		}
		return errMessage;
	}

	public Result register() {
		JsonNode json = request().body().asJson();

		String email = json.get("email").asText().toLowerCase();
		String name = json.get("name").asText().toLowerCase();
		String phone = json.get("phone").asText().toLowerCase();
		String code = OtpGenerator.randomString(4);

		try {
			if (json.has("otp")) {
				Member member = Member.find.where().eq("otp", json.get("otp").asText()).eq("phone", phone).findUnique();
				if (member != null) {
					boolean result = Minutes.minutesBetween(new DateTime(member.otpExpireTime), new DateTime())
							.isLessThan(Minutes.minutes(1));

					if (result) {
						member.otp = null;
						member.otpExpireTime = null;
						member.isActive = true;
						member.update();
						messageDescription.put("deskripsi", "Register success.");
						response.setBaseResponse(1, 0, 1, success, messageDescription);
					} else {
						messageDescription.put("deskripsi", "Otp expired.");
						response.setBaseResponse(1, 0, 1, error, messageDescription);
					}
				} else {
					messageDescription.put("deskripsi", "Otp invalid.");
					response.setBaseResponse(1, 0, 1, error, messageDescription);
				}
				return ok(Json.toJson(response));
			} else {
				Member member = Member.find.where().eq("email", email).eq("is_active", true).findUnique();
				if (member != null) {
					messageDescription.put("deskripsi", "Email already exist.");
					response.setBaseResponse(1, 0, 1, error, messageDescription);
					return ok(Json.toJson(response));
				} else {
					member = Member.find.where().eq("phone", phone).eq("is_active", false).findUnique();
					createContentEmail(email, "OTP", code);
					if (member != null) {
						member.otp = code;
						member.otpExpireTime = new Date();
						member.update();
					} else {
						member = new Member();
						member.email = email;
						member.fullName = name;
						member.phone = phone;
						member.otp = code;
						member.otpExpireTime = new Date();
						member.save();
					}

					messageDescription.put("deskripsi", "Otp sent.");
					response.setBaseResponse(1, 0, 1, success, messageDescription);
					return ok(Json.toJson(response));
				}
			}

		} catch (Exception e) {
			Logger.error("", e);
			return internalServerError();
		}

	}

	public Result login() {
		JsonNode json = request().body().asJson();
		if (json.has("phone")) {
			String phone = json.findPath("phone").asText();
			Member member = Member.find.where().eq("phone", phone).findUnique();
			if (member != null) {
				if (member.isActive) {
					try {
						String code = OtpGenerator.randomString(4);
						member.otp = code;
						member.otpExpireTime = new Date();
						member.update();
						createContentEmail(member.email, "OTP", code);
						messageDescription.put("deskripsi", "Otp sent.");
						response.setBaseResponse(1, 0, 1, success, messageDescription);
						return ok(Json.toJson(response));
					} catch (Exception e) {
						e.printStackTrace();
						response.setBaseResponse(0, 0, 0, error, null);
						return badRequest(Json.toJson(response));
					}
				} else {
					messageDescription.put("deskripsi",
							"Your account hasn't actived, please check and verify from your email.");
					response.setBaseResponse(1, 0, 1, error, messageDescription);
					return ok(Json.toJson(response));
				}
			} else {
				messageDescription.put("deskripsi", "Wrong phone number.");
				response.setBaseResponse(1, 0, 1, error, messageDescription);
				return ok(Json.toJson(response));
			}
		}
		response.setBaseResponse(0, 0, 0, inputParameter, null);
		return badRequest(Json.toJson(response));
	}

	public Result sendOtpLogin() {
		JsonNode json = request().body().asJson();
		if (json.has("phone") && json.has("otp")) {
			String phone = json.get("phone").asText();
			String otp = json.get("otp").asText();

			Member member = Member.find.where().eq("phone", phone).eq("otp", otp).findUnique();
			if (member != null) {
				try {
					boolean result = Minutes.minutesBetween(new DateTime(member.otpExpireTime), new DateTime())
							.isLessThan(Minutes.minutes(1));

					if (result) {
						UserSession session = new UserSession(createToken(member), null, null);
						ObjectMapper mapper = new ObjectMapper();
						mapper.addMixInAnnotations(Member.class, JsonMask.class);
						session.setProfile_data(Json.parse(mapper.writeValueAsString(member)));
						member.otp = null;
						member.otpExpireTime = null;
						member.update();
						response.setBaseResponse(1, 0, 1, success, session);
					} else {
						messageDescription.put("deskripsi", "Otp expired.");
						response.setBaseResponse(1, 0, 1, error, messageDescription);
					}

					return ok(Json.toJson(response));
				} catch (Exception e) {
					e.printStackTrace();
					response.setBaseResponse(1, 0, 1, error, "");
					return badRequest(Json.toJson(response));
				}
			} else {
				messageDescription.put("deskripsi", "OTP invalid.");
				response.setBaseResponse(1, 0, 1, error, messageDescription);
				return ok(Json.toJson(response));
			}
		} else {
			response.setBaseResponse(1, 0, 1, error, "");
			return badRequest(Json.toJson(response));
		}
	}

	public Result resendOtp() {
		JsonNode json = request().body().asJson();
		if (json.has("phone")) {
			String phone = json.findPath("phone").asText();
			Member member = Member.find.where().eq("phone", phone).findUnique();
			if (member != null) {
				try {
					String code = OtpGenerator.randomString(4);
					member.otp = code;
					member.otpExpireTime = new Date();
					member.update();
					createContentEmail(member.email, "OTP", code);
					messageDescription.put("deskripsi", "Otp sent.");
					response.setBaseResponse(1, 0, 1, success, messageDescription);
					return ok(Json.toJson(response));
				} catch (Exception e) {
					e.printStackTrace();
					response.setBaseResponse(0, 0, 0, error, null);
					return badRequest(Json.toJson(response));
				}
			} else {
				messageDescription.put("deskripsi", "Wrong phone number.");
				response.setBaseResponse(1, 0, 1, error, messageDescription);
				return ok(Json.toJson(response));
			}
		}
		response.setBaseResponse(0, 0, 0, inputParameter, null);
		return badRequest(Json.toJson(response));
	}

	public static String createToken(Member member) {
		String token = "";
		try {
			token = Encryption.SHA1(new Date().toString() + "hellobisnis");
			member.token = token;
			member.tokenExpireTime = new Date();
			member.update();
		} catch (NoSuchAlgorithmException e) {
			token = null;
			e.printStackTrace();
		}

		return token;
	}

	public Properties emailProps() {
		String smtpHost = "smtp.gmail.com";
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.port", "587");

		props.put("mail.debug", "false");
		props.put("mail.smtp.sendpartial", "true");
		props.put("mail.smtp.ssl.enable", "false");
		props.put("mail.smtp.ssl.trust", "*");
		return props;
	}

	private static void GetPointsFromSignIn(String deviceModel, Member member) {
		if (deviceModel.equalsIgnoreCase(MemberLog.DEV_TYPE_ANDROID)
				|| deviceModel.equalsIgnoreCase(MemberLog.DEV_TYPE_IOS)) {
			Logger.info("login mobile");
			DateFormat df = new SimpleDateFormat("yy-MM-dd");
			ConfigSettings configSetting = ConfigSettings.find.where().eq("module", "loyaltysetting").findUnique();

			String value[] = configSetting.value.split("##");
			int status = Integer.parseInt(value[0]);
			Date startDate, endDate;
			try {
				startDate = df.parse(value[1]);
				endDate = df.parse(value[2]);
			} catch (Exception e) {
				// TODO: handle exception
				startDate = new Date();
				endDate = new Date();
			}
			long loyaltyBonus = Long.parseLong(value[3]);
			int expiredDays = Integer.parseInt(value[4]);
			String type = Integer.parseInt(value[5]) == 0 ? "Web"
					: (Integer.parseInt(value[5]) == 1 ? "Mobile" : "Web & Mobile");

			Date currentDate = new Date();
			List<MemberLog> loginMobile = MemberLog.find.where().eq("member_id", member.id).ge("created_at", startDate)
					.or(Expr.eq("device_model", "ANDROID"), Expr.eq("device_model", "IOS")).findList();

			// Logger.info(String.valueOf(currentDate));
			// Logger.info(String.valueOf(startDate));
			// Logger.info(String.valueOf(endDate));
			// Logger.info(String.valueOf(currentDate.after(startDate)));
			// Logger.info(String.valueOf(currentDate.before(endDate)));
			if (currentDate.after(startDate) && currentDate.before(endDate) && loginMobile.isEmpty()) {
				Logger.info("valid got points");
				Date expiredDate = new DateTime(new Date()).plusDays(expiredDays).toDate();
				// DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				LoyaltyPoint.addPoint(member.id, loyaltyBonus, null, expiredDate, "Bonus points from login");
			}
		}
	}

	@ApiOperation(value = "Refresh token", notes = "Refresh your current token.\n" + swaggerInfo
			+ "", response = UserSession.class, httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "email", dataType = "temp.swaggermap.LoginForm", required = true, paramType = "body", value = "account email") })
	public static Result refreshToken() {
		if (request().headers().get(API_KEY) != null && request().headers().get(TOKEN) != null) {
			String apiKey = request().headers().get(API_KEY)[0];
			String token = request().headers().get(TOKEN)[0];
			MemberLog targetLog = MemberLog.isMemberAuthorized(token, apiKey);
			if (targetLog != null) {
				Member targetMember = targetLog.member;
				// create new token
				MemberLog log = MemberLog.loginMember(targetLog.deviceModel, targetLog.deviceType, targetLog.deviceId,
						targetMember);
				if (log == null) {
					response.setBaseResponse(0, 0, 0, inputParameter, null);
					return badRequest(Json.toJson(response));
				}
				// deactivate old token
				targetLog.isActive = false;
				targetLog.save();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				UserSession session = new UserSession(log.token, df.format(log.expiredDate), log.memberType);
				ObjectMapper om = new ObjectMapper();
				om.addMixInAnnotations(Member.class, JsonMask.class);
				try {
					session.setProfile_data(Json.parse(om.writeValueAsString(targetMember)));
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				response.setBaseResponse(1, 0, 1, success, session);
				return ok(Json.toJson(response));
			}
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}

	@ApiOperation(value = "Sign out", notes = "Sign out.", response = BaseResponse.class, httpMethod = "GET")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "access token", dataType = "string", required = true, paramType = "header", value = "access token") })
	public static Result signOut() {
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		if (checkAccessAuthorization("member") == 200) {
			String token = request().headers().get(TOKEN)[0];
			if (MemberLog.logoutMember(token)) {
				response.setBaseResponse(1, 0, 1, success, null);
				return ok(Json.toJson(response));
			}
		}
		return unauthorized(Json.toJson(response));
	}

	public static Result signOut2() {
		try {
			JsonNode json = request().body().asJson();
			
			if (json.has("X-API-TOKEN") && json.has("X-API-KEY")) {
				System.err.println("Api key : " + json.get("X-API-KEY").asText());
				System.err.println("Token : " + json.get("X-API-TOKEN").asText());

				String token = json.get("X-API-TOKEN").asText();
				String apiKey = json.get("X-API-KEY").asText();
				
				Member member = Member.find.where().eq("token", token).findUnique();
				if (member != null && apiKey.equals(Constant.API_KEY)) {
					member.token = "";
					member.tokenExpireTime = new Date();
					member.update();
					response.setBaseResponse(1, 0, 1, success, null);
					return ok(Json.toJson(response));
				} else {
					response.setBaseResponse(0, 0, 0, unauthorized, null);
					return unauthorized(Json.toJson(response));
				} 
			} else {
				messageDescription.put("deskripsi", "Signout failed");
				response.setBaseResponse(1, 0, 1, error, messageDescription);
				return internalServerError(Json.toJson(response));
			}
		} catch (Exception e) {
			e.printStackTrace();
			messageDescription.put("deskripsi", "Signout failed");
			response.setBaseResponse(1, 0, 1, error, messageDescription);
			return internalServerError(Json.toJson(response));
		}
		
	}

	public static Result signInFacebook() {
		JsonNode json = request().body().asJson();
		if (checkAccessAuthorization("guest") == 200 && json.has("facebook_user_id") && json.has("access_token")
				&& json.has("device_model") && json.has("device_type")) {
			String facebookUserId = json.findPath("facebook_user_id").asText();
			String accessToken = json.findPath("access_token").asText();
			String deviceModel = json.findPath("device_model").asText();
			String deviceType = json.findPath("device_type").asText();
			String deviceId = json.findPath("device_id").asText();

			ServiceResponse sresponse = FacebookService.getInstance().getFacebookUserData(accessToken);
			if (sresponse.getCode() == 200) {
				ObjectMapper mapper = new ObjectMapper();
				FacebookUser fuser = mapper.convertValue(sresponse.getData(), FacebookUser.class);
				if (facebookUserId != null && !facebookUserId.equals("") && facebookUserId.equals(fuser.getId())) {
					Transaction txn = Ebean.beginTransaction();
					try {
						Member target = Member.find.where().eq("facebook_user_id", facebookUserId).findUnique();
						if (target != null && !target.isActive) {
							response.setBaseResponse(0, 0, 0, "Your account was disabled", null);
							return badRequest(Json.toJson(response));
						} else if (target == null) {
							response.setBaseResponse(0, 0, 0, "User not found", null);
							return badRequest(Json.toJson(response));
						}
						MemberLog log = MemberLog.loginMember(deviceModel, deviceType, deviceId, target);
						if (log == null) {
							response.setBaseResponse(0, 0, 0, inputParameter, null);
							return badRequest(Json.toJson(response));
						}
						txn.commit();
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						UserSession session = new UserSession(log.token, df.format(log.expiredDate), log.memberType);
						mapper.addMixInAnnotations(Member.class, JsonMask.class);
						session.setProfile_data(Json.parse(mapper.writeValueAsString(target)));
						response.setBaseResponse(1, 0, 1, success, session);
						return ok(Json.toJson(response));
					} catch (Exception e) {
						e.printStackTrace();
						txn.rollback();
					} finally {
						txn.end();
					}
					response.setBaseResponse(0, 0, 0, error, null);
					return internalServerError(Json.toJson(response));
				}
				response.setBaseResponse(0, 0, 0, "Token didn't match Facebook user ID", null);
				return badRequest(Json.toJson(response));
			} else if (sresponse.getCode() == 408) {
				response.setBaseResponse(0, 0, 0, timeOut, null);
				return badRequest(Json.toJson(response));
			} else {
				response.setBaseResponse(0, 0, 0, sresponse.getCode() + " " + error, null);
				return badRequest(Json.toJson(response));
			}
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}

	// odoo
	public static Result signInFacebookOld() {
		JsonNode json = request().body().asJson();
		if (checkAccessAuthorization("guest") == 200 && json.has("facebook_user_id") && json.has("access_token")
				&& json.has("device_model") && json.has("device_type")) {
			String facebookUserId = json.findPath("facebook_user_id").asText();
			String accessToken = json.findPath("access_token").asText();
			String deviceModel = json.findPath("device_model").asText();
			String deviceType = json.findPath("device_type").asText();
			String deviceId = json.findPath("device_id").asText();

			ServiceResponse sresponse = FacebookService.getInstance().getFacebookUserData(accessToken);
			if (sresponse.getCode() == 200) {
				ObjectMapper mapper = new ObjectMapper();
				FacebookUser fuser = mapper.convertValue(sresponse.getData(), FacebookUser.class);
				if (facebookUserId != null && !facebookUserId.equals("") && facebookUserId.equals(fuser.getId())) {
					Transaction txn = Ebean.beginTransaction();
					try {
						Member target = Member.find.where().eq("facebook_user_id", facebookUserId).findUnique();
						if (target != null && !target.isActive) {
							response.setBaseResponse(0, 0, 0, "Your account was disabled", null);
							return badRequest(Json.toJson(response));
						} else if (target == null) {
							target = new Member();
							target.facebookUserId = fuser.getId();
							target.firstName = fuser.getName();
							target.lastName = "";
							target.fullName = fuser.getName();
							target.gender = (fuser.getGender() != null) ? (fuser.getGender().equals("male") ? "M" : "F")
									: null;
							target.isActive = true;
							if (fuser.getEmail() != null) {
								target.emailNotifikasi = fuser.getEmail();
							}
							target.largeImageUrl = target.mediumImageUrl = target.thumbnailImageUrl = fuser.getImage();
							target.save();
							// odoo
							// OdooService.getInstance().createCustomer(target);
						}
						MemberLog log = MemberLog.loginMember(deviceModel, deviceType, deviceId, target);
						if (log == null) {
							response.setBaseResponse(0, 0, 0, inputParameter, null);
							return badRequest(Json.toJson(response));
						}
						txn.commit();
						DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						UserSession session = new UserSession(log.token, df.format(log.expiredDate), log.memberType);
						mapper.addMixInAnnotations(Member.class, JsonMask.class);
						session.setProfile_data(Json.parse(mapper.writeValueAsString(target)));
						response.setBaseResponse(1, 0, 1, success, session);
						return ok(Json.toJson(response));
					} catch (Exception e) {
						e.printStackTrace();
						txn.rollback();
					} finally {
						txn.end();
					}
					response.setBaseResponse(0, 0, 0, error, null);
					return internalServerError(Json.toJson(response));
				}
				response.setBaseResponse(0, 0, 0, "Token didn't match Facebook user ID", null);
				return badRequest(Json.toJson(response));
			} else if (sresponse.getCode() == 408) {
				response.setBaseResponse(0, 0, 0, timeOut, null);
				return badRequest(Json.toJson(response));
			} else {
				response.setBaseResponse(0, 0, 0, sresponse.getCode() + " " + error, null);
				return badRequest(Json.toJson(response));
			}
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}

	// odoo
	public static Result signInGoogle() {
		JsonNode json = request().body().asJson();
		if (checkAccessAuthorization("guest") == 200 && json.has("google_user_id") && json.has("access_token")
				&& json.has("device_model") && json.has("device_type")) {
			String googleUserId = json.findPath("google_user_id").asText();
			String accessToken = json.findPath("access_token").asText();
			String deviceModel = json.findPath("device_model").asText();
			String deviceType = json.findPath("device_type").asText();
			String deviceId = json.findPath("device_id").asText();
			String firstName = json.has("first_name") ? json.findPath("first_name").asText() : "";
			ObjectMapper mapper = new ObjectMapper();
			if (firstName.isEmpty()) {
				ServiceResponse sresponse = GooglePlusService.getInstance().getGooglePlusUserData(accessToken);
				if (sresponse.getCode() == 200) {
					GooglePlusUser guser = mapper.convertValue(sresponse.getData(), GooglePlusUser.class);
					if (googleUserId != null && !googleUserId.equals("") && googleUserId.equals(guser.getId())) {
						Transaction txn = Ebean.beginTransaction();
						try {
							Member target = Member.find.where().eq("google_user_id", googleUserId).findUnique();
							if (target != null && !target.isActive) {
								response.setBaseResponse(0, 0, 0, "Your account was disabled", null);
								return badRequest(Json.toJson(response));
							} else if (target == null) {
								response.setBaseResponse(0, 0, 0, "User not found", null);
								return badRequest(Json.toJson(response));
							}
							GetPointsFromSignIn(deviceModel, target);
							MemberLog log = MemberLog.loginMember(deviceModel, deviceType, deviceId, target);
							if (log == null) {
								response.setBaseResponse(0, 0, 0, inputParameter, null);
								return badRequest(Json.toJson(response));
							}

							txn.commit();
							DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
							UserSession session = new UserSession(log.token, df.format(log.expiredDate),
									log.memberType);
							mapper.addMixInAnnotations(Member.class, JsonMask.class);
							session.setProfile_data(Json.parse(mapper.writeValueAsString(target)));
							response.setBaseResponse(1, 0, 1, success, session);
							return ok(Json.toJson(response));
						} catch (Exception e) {
							e.printStackTrace();
							txn.rollback();
						} finally {
							txn.end();
						}
						response.setBaseResponse(0, 0, 0, error, null);
						return internalServerError(Json.toJson(response));
					}
					response.setBaseResponse(0, 0, 0, "Token didn't match Google user ID", null);
					return badRequest(Json.toJson(response));
				} else if (sresponse.getCode() == 408) {
					response.setBaseResponse(0, 0, 0, timeOut, null);
					return badRequest(Json.toJson(response));
				} else {
					response.setBaseResponse(0, 0, 0, sresponse.getCode() + " " + error, null);
					return badRequest(Json.toJson(response));
				}
			} else {
				Transaction txn = Ebean.beginTransaction();
				try {
					Member target = Member.find.where().eq("google_user_id", googleUserId).findUnique();
					if (target != null && !target.isActive) {
						response.setBaseResponse(0, 0, 0, "Your account was disabled", null);
						return badRequest(Json.toJson(response));
					} else if (target == null) {
						response.setBaseResponse(0, 0, 0, "User not found", null);
						return badRequest(Json.toJson(response));
					}
					MemberLog log = MemberLog.loginMember(deviceModel, deviceType, deviceId, target);
					if (log == null) {
						response.setBaseResponse(0, 0, 0, inputParameter, null);
						return badRequest(Json.toJson(response));
					}
					// odoo
					// OdooService.getInstance().createCustomer(target);
					txn.commit();
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					UserSession session = new UserSession(log.token, df.format(log.expiredDate), log.memberType);
					mapper.addMixInAnnotations(Member.class, JsonMask.class);
					session.setProfile_data(Json.parse(mapper.writeValueAsString(target)));
					response.setBaseResponse(1, 0, 1, success, session);
					return ok(Json.toJson(response));
				} catch (Exception e) {
					e.printStackTrace();
					txn.rollback();
				} finally {
					txn.end();
				}
				response.setBaseResponse(0, 0, 0, error, null);
				return internalServerError(Json.toJson(response));
			}
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}

	public static Result signInGoogleOld() {
		JsonNode json = request().body().asJson();
		if (checkAccessAuthorization("guest") == 200 && json.has("google_user_id") && json.has("access_token")
				&& json.has("device_model") && json.has("device_type")) {
			String googleUserId = json.findPath("google_user_id").asText();
			String accessToken = json.findPath("access_token").asText();
			String deviceModel = json.findPath("device_model").asText();
			String deviceType = json.findPath("device_type").asText();
			String deviceId = json.findPath("device_id").asText();
			String firstName = json.has("first_name") ? json.findPath("first_name").asText() : "";
			String lastName = json.has("last_name") ? json.findPath("last_name").asText() : "";
			String gender = json.has("gender") ? json.findPath("gender").asText() : "";
			String email = json.has("email") ? json.findPath("email").asText() : "";
			ObjectMapper mapper = new ObjectMapper();
			if (firstName.isEmpty()) {
				ServiceResponse sresponse = GooglePlusService.getInstance().getGooglePlusUserData(accessToken);
				if (sresponse.getCode() == 200) {
					GooglePlusUserOauth guser = mapper.convertValue(sresponse.getData(), GooglePlusUserOauth.class);
					if (googleUserId != null && !googleUserId.equals("") && googleUserId.equals(guser.getSub())) {
						Transaction txn = Ebean.beginTransaction();
						try {
							Member target = Member.find.where().eq("google_user_id", googleUserId).setMaxRows(1)
									.findUnique();

							if (target == null) {
								String emailTarget = null;
								if (guser.getEmails() != null && guser.getEmails().length > 0) {
									emailTarget = guser.getEmails()[0].getValue();
									target = Member.find.where().eq("email", emailTarget).setMaxRows(1).findUnique();
								} else if (email != null && !email.isEmpty()) {
									emailTarget = email;
									target = Member.find.where().eq("email", emailTarget).setMaxRows(1).findUnique();
								}

								// add generate referral code ngambil dari function refferalcontroller
								String referral_code = generateReferralCode();

								// validasi referral code member
								Member member_check = Member.find.where().eq("referral_code", referral_code)
										.setMaxRows(1).findUnique(); // new input

								String referral_code_new = referral_code;

								if (member_check != null) {
									referral_code_new = generateReferralCode();
								}

								if (target == null) {
									target = new Member();
									target.googleUserId = guser.getSub();
									target.firstName = guser.getGivenName();
									target.lastName = guser.getFamilyName();
									target.email = emailTarget;
									target.fullName = guser.getName();
									target.gender = (guser.getGender() != null)
											? (guser.getGender().equals("male") ? "M" : "F")
											: null;
									target.emailNotifikasi = emailTarget;
									target.referral_code = referral_code_new;
									if (guser.getImage() != null) {
										target.largeImageUrl = target.mediumImageUrl = target.thumbnailImageUrl = guser
												.getImage().getUrl();
									}
									target.isActive = true;
									target.save();
								} else if (target.googleUserId == null && target.isActive) {
									target.googleUserId = guser.getSub();
									target.save();
								} else if (target.googleUserId != null) {
									response.setBaseResponse(0, 0, 0,
											"Your email has been assigned to different Google id", null);
									return badRequest(Json.toJson(response));
								}
							}

							if (target != null && !target.isActive) {
								response.setBaseResponse(0, 0, 0, "Your account was disabled", null);
								return badRequest(Json.toJson(response));
							}
							GetPointsFromSignIn(deviceModel, target);
							MemberLog log = MemberLog.loginMember(deviceModel, deviceType, deviceId, target);
							if (log == null) {
								response.setBaseResponse(0, 0, 0, inputParameter, null);
								return badRequest(Json.toJson(response));
							}

							txn.commit();
							DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
							UserSession session = new UserSession(log.token, df.format(log.expiredDate),
									log.memberType);
							mapper.addMixInAnnotations(Member.class, JsonMask.class);
							session.setProfile_data(Json.parse(mapper.writeValueAsString(target)));
							response.setBaseResponse(1, 0, 1, success, session);
							return ok(Json.toJson(response));
						} catch (Exception e) {
							e.printStackTrace();
							txn.rollback();
						} finally {
							txn.end();
						}
						response.setBaseResponse(0, 0, 0, error, null);
						return internalServerError(Json.toJson(response));
					}
					response.setBaseResponse(0, 0, 0, "Token didn't match Google user ID", null);
					return badRequest(Json.toJson(response));
				} else if (sresponse.getCode() == 408) {
					response.setBaseResponse(0, 0, 0, timeOut, null);
					return badRequest(Json.toJson(response));
				} else {
					response.setBaseResponse(0, 0, 0, sresponse.getCode() + " " + error, null);
					return badRequest(Json.toJson(response));
				}
			} else {
				Transaction txn = Ebean.beginTransaction();
				try {
					Member target = Member.find.where().eq("google_user_id", googleUserId).findUnique();
					if (target == null) {
						String emailTarget = null;
						if (email != null && !email.isEmpty()) {
							emailTarget = email;
							target = Member.find.where().eq("email", emailTarget).setMaxRows(1).findUnique();
						}

						if (target == null) {
							target = new Member();
							target.googleUserId = googleUserId;
							target.firstName = firstName;
							target.lastName = lastName;
							target.fullName = target.firstName
									+ ((target.lastName != null && !target.lastName.equals("")) ? " " + target.lastName
											: "");
							target.gender = (gender != null && !gender.isEmpty()) ? (gender.equals("male") ? "M" : "F")
									: null;
							target.emailNotifikasi = emailTarget;
							target.email = emailTarget;
							target.isActive = true;
							target.save();
						} else if (target.googleUserId == null && target.isActive) {
							target.googleUserId = googleUserId;
							target.save();
						} else if (target.googleUserId != null) {
							response.setBaseResponse(0, 0, 0, "Your email has been assigned to different Google id",
									null);
							return badRequest(Json.toJson(response));
						}
					}

					if (target != null && !target.isActive) {
						response.setBaseResponse(0, 0, 0, "Your account was disabled", null);
						return badRequest(Json.toJson(response));
					}
					MemberLog log = MemberLog.loginMember(deviceModel, deviceType, deviceId, target);
					if (log == null) {
						response.setBaseResponse(0, 0, 0, inputParameter, null);
						return badRequest(Json.toJson(response));
					}

					txn.commit();
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					UserSession session = new UserSession(log.token, df.format(log.expiredDate), log.memberType);
					mapper.addMixInAnnotations(Member.class, JsonMask.class);
					session.setProfile_data(Json.parse(mapper.writeValueAsString(target)));
					response.setBaseResponse(1, 0, 1, success, session);
					return ok(Json.toJson(response));
				} catch (Exception e) {
					e.printStackTrace();
					txn.rollback();
				} finally {
					txn.end();
				}
				response.setBaseResponse(0, 0, 0, error, null);
				return internalServerError(Json.toJson(response));
			}
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}

	public static Result signInGoogleOld2() {
		JsonNode json = request().body().asJson();
		if (checkAccessAuthorization("guest") == 200 && json.has("google_user_id") && json.has("access_token")
				&& json.has("device_model") && json.has("device_type")) {
			String googleUserId = json.findPath("google_user_id").asText();
			String accessToken = json.findPath("access_token").asText();
			String deviceModel = json.findPath("device_model").asText();
			String deviceType = json.findPath("device_type").asText();
			String deviceId = json.findPath("device_id").asText();
			String firstName = json.has("first_name") ? json.findPath("first_name").asText() : "";
			String lastName = json.has("last_name") ? json.findPath("last_name").asText() : "";
			String gender = json.has("gender") ? json.findPath("gender").asText() : "";
			String email = json.has("email") ? json.findPath("email").asText() : "";
			ObjectMapper mapper = new ObjectMapper();
			if (firstName.isEmpty()) {
				ServiceResponse sresponse = GooglePlusService.getInstance().getGooglePlusUserData(accessToken);
				if (sresponse.getCode() == 200) {
					GooglePlusUserOauth guser = mapper.convertValue(sresponse.getData(), GooglePlusUserOauth.class);
					if (googleUserId != null && !googleUserId.equals("") && googleUserId.equals(guser.getSub())) {
						Transaction txn = Ebean.beginTransaction();
						try {

							Member target = Member.find.where().eq("google_user_id", googleUserId).findUnique();
							if (target != null && !target.isActive) {
								response.setBaseResponse(0, 0, 0, "Your account was disabled", null);
								return badRequest(Json.toJson(response));
							} else if (target == null) {
								target = new Member();
								target.googleUserId = guser.getSub();
								target.firstName = guser.getGivenName();
								target.lastName = guser.getFamilyName();
								if (guser.getEmails() != null && guser.getEmails().length > 0) {
									target.email = guser.getEmails()[0].getValue();
								}
								target.fullName = guser.getName();
								// target.fullName = target.firstName
								// + ((target.lastName != null && !target.lastName.equals("")) ? " " +
								// target.lastName
								// : "");
								target.gender = (guser.getGender() != null)
										? (guser.getGender().equals("male") ? "M" : "F")
										: null;
								if (guser.getEmails() != null && guser.getEmails().length > 0) {
									target.emailNotifikasi = guser.getEmails()[0].getValue();
								}
								if (guser.getImage() != null) {
									target.largeImageUrl = target.mediumImageUrl = target.thumbnailImageUrl = guser
											.getImage().getUrl();
								}
								target.isActive = true;
								target.save();
								// odoo
								// OdooService.getInstance().createCustomer(target);
							}
							MemberLog log = MemberLog.loginMember(deviceModel, deviceType, deviceId, target);
							if (log == null) {
								response.setBaseResponse(0, 0, 0, inputParameter, null);
								return badRequest(Json.toJson(response));
							}

							txn.commit();
							DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
							UserSession session = new UserSession(log.token, df.format(log.expiredDate),
									log.memberType);
							mapper.addMixInAnnotations(Member.class, JsonMask.class);
							session.setProfile_data(Json.parse(mapper.writeValueAsString(target)));
							response.setBaseResponse(1, 0, 1, success, session);
							return ok(Json.toJson(response));
						} catch (Exception e) {
							e.printStackTrace();
							txn.rollback();
						} finally {
							txn.end();
						}
						response.setBaseResponse(0, 0, 0, error, null);
						return internalServerError(Json.toJson(response));
					}
					response.setBaseResponse(0, 0, 0, "Token didn't match Google user ID", null);
					return badRequest(Json.toJson(response));
				} else if (sresponse.getCode() == 408) {
					response.setBaseResponse(0, 0, 0, timeOut, null);
					return badRequest(Json.toJson(response));
				} else {
					response.setBaseResponse(0, 0, 0, sresponse.getCode() + " " + error, null);
					return badRequest(Json.toJson(response));
				}
			} else {
				Transaction txn = Ebean.beginTransaction();
				try {
					Member target = Member.find.where().eq("google_user_id", googleUserId).findUnique();
					if (target != null && !target.isActive) {
						response.setBaseResponse(0, 0, 0, "Your account was disabled", null);
						return badRequest(Json.toJson(response));
					} else if (target == null) {
						target = new Member();
						target.googleUserId = googleUserId;
						target.firstName = firstName;
						target.lastName = lastName;
						target.fullName = target.firstName
								+ ((target.lastName != null && !target.lastName.equals("")) ? " " + target.lastName
										: "");
						target.gender = (gender != null && !gender.isEmpty()) ? (gender.equals("male") ? "M" : "F")
								: null;
						if (!email.isEmpty()) {
							target.emailNotifikasi = email;
						}
						target.email = email;
						target.isActive = true;
						target.save();
					}
					MemberLog log = MemberLog.loginMember(deviceModel, deviceType, deviceId, target);
					if (log == null) {
						response.setBaseResponse(0, 0, 0, inputParameter, null);
						return badRequest(Json.toJson(response));
					}
					// odoo
					// OdooService.getInstance().createCustomer(target);
					txn.commit();
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
					UserSession session = new UserSession(log.token, df.format(log.expiredDate), log.memberType);
					mapper.addMixInAnnotations(Member.class, JsonMask.class);
					session.setProfile_data(Json.parse(mapper.writeValueAsString(target)));
					response.setBaseResponse(1, 0, 1, success, session);
					return ok(Json.toJson(response));
				} catch (Exception e) {
					e.printStackTrace();
					txn.rollback();
				} finally {
					txn.end();
				}
				response.setBaseResponse(0, 0, 0, error, null);
				return internalServerError(Json.toJson(response));
			}
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}

	public static Result signInAppleId() throws Exception {
		JsonNode json = request().body().asJson();
		if (checkAccessAuthorization("guest") == 200 && json.has("id_token") && json.has("access_token")
				&& json.has("email") && json.has("device_model") && json.has("device_type")) {
			String idToken = json.findPath("id_token").asText();
			String accessToken = json.findPath("access_token").asText();
			String deviceModel = json.findPath("device_model").asText();
			String deviceType = json.findPath("device_type").asText();
			String deviceId = json.findPath("device_id").asText();
			String firstName = json.has("first_name") ? json.findPath("first_name").asText() : "";
			String lastName = json.has("last_name") ? json.findPath("last_name").asText() : "";
			String gender = json.has("gender") ? json.findPath("gender").asText() : "";
			String email = json.has("email") ? json.findPath("email").asText() : "";
			ObjectMapper mapper = new ObjectMapper();
			String payload[] = idToken.split("\\.");
			String decoded = new String(Base64.getDecoder().decode(payload[1]));
			AppleIdUserOauth appleUser = mapper.readValue(decoded, AppleIdUserOauth.class);

			Transaction txn = Ebean.beginTransaction();
			try {
				Member target = Member.find.where().eq("apple_user_id", appleUser.getSub()).findUnique();
				if (target == null) {
					String emailTarget = null;
					if (email != null && !email.isEmpty()) {
						emailTarget = email;
						target = Member.find.where().eq("email", emailTarget).setMaxRows(1).findUnique();
					}

					// add generate referral code ngambil dari function refferalcontroller
					String referral_code = generateReferralCode();

					// validasi referral code member
					Member member_check = Member.find.where().eq("referral_code", referral_code).setMaxRows(1)
							.findUnique(); // new input

					String referral_code_new = referral_code;

					if (member_check != null) {
						referral_code_new = generateReferralCode();
					}

					if (target == null) {
						target = new Member();
						target.appleUserId = appleUser.getSub();
						target.firstName = firstName;
						target.lastName = lastName;
						target.fullName = target.firstName
								+ ((target.lastName != null && !target.lastName.equals("")) ? " " + target.lastName
										: "");
						target.gender = (gender != null && !gender.isEmpty()) ? (gender.equals("male") ? "M" : "F")
								: null;
						target.emailNotifikasi = emailTarget;
						target.email = emailTarget;
						target.referral_code = referral_code_new;
						target.isActive = true;
						target.save();
					} else if (target.appleUserId == null && target.isActive) {
						target.appleUserId = appleUser.getSub();
						target.save();
					} else if (target.appleUserId != null) {
						response.setBaseResponse(0, 0, 0, "Your email has been assigned to different Apple id", null);
						return badRequest(Json.toJson(response));
					}
				}

				if (target != null && !target.isActive) {
					response.setBaseResponse(0, 0, 0, "Your account was disabled", null);
					return badRequest(Json.toJson(response));
				}
				GetPointsFromSignIn(deviceModel, target);
				MemberLog log = MemberLog.loginMember(deviceModel, deviceType, deviceId, target);
				if (log == null) {
					response.setBaseResponse(0, 0, 0, inputParameter, null);
					return badRequest(Json.toJson(response));
				}

				txn.commit();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				UserSession session = new UserSession(log.token, df.format(log.expiredDate), log.memberType);
				mapper.addMixInAnnotations(Member.class, JsonMask.class);
				session.setProfile_data(Json.parse(mapper.writeValueAsString(target)));
				response.setBaseResponse(1, 0, 1, success, session);
				return ok(Json.toJson(response));
			} catch (Exception e) {
				e.printStackTrace();
				txn.rollback();
			} finally {
				txn.end();
			}
			response.setBaseResponse(0, 0, 0, error, null);
			return internalServerError(Json.toJson(response));
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}

	@ApiOperation(value = "Forget password", notes = "Forget Password.", response = BaseResponse.class, httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "body", dataType = "Object", required = true, paramType = "body", value = "Forget password") })
	public static Result forgetPassword() {
		JsonNode json = request().body().asJson();
		if (checkAccessAuthorization("guest") == 200 && json.has("email")) {
			String email = json.findPath("email").asText();
			String redirect = Constant.getInstance().getFrontEndUrl() + "/reset-password";

			Member member = Member.find.where().eq("is_active", true).eq("email", email).setMaxRows(1).findUnique();
			if (member != null) {
				Long now = System.currentTimeMillis();
				try {
					member.resetToken = Encryption.EncryptAESCBCPCKS5Padding(member.email + now);
					member.resetTime = now;
					member.update();
				} catch (Exception e) {
					e.printStackTrace();
				}
				Thread thread = new Thread(() -> {
					try {
						MailConfig.sendmail2(member.email, MailConfig.subjectForgotPassword,
								MailConfig.renderMailForgotPasswordTemplate(member, redirect));

					} catch (Exception e) {
						e.printStackTrace();
					}
				});
				thread.start();
				response.setBaseResponse(1, 0, 1, success, null);
				return ok(Json.toJson(response));
			}
			response.setBaseResponse(0, 0, 0, notFound, null);
			return notFound(Json.toJson(response));
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}

	public static Result changePassword() throws InvalidKeyException, InvalidAlgorithmParameterException,
			NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		Member targetMember = checkMemberAccessAuthorization();
		if (targetMember != null) {
			JsonNode json = request().body().asJson();
			if (json.has("old_password") && json.has("new_password") && json.has("confirm_password")) {
				String oldPass = json.findPath("old_password").asText();
				String newPass = json.findPath("new_password").asText();
				String conPass = json.findPath("confirm_password").asText();
				String message = (targetMember.hasSetPassword())
						? targetMember.changePassword(oldPass, newPass, conPass)
						: targetMember.changePassword(newPass, conPass);
				if (message == null) {
					response.setBaseResponse(1, 0, 1, updated, null);
					return ok(Json.toJson(response));
				} else if (message.equals("500")) {
					response.setBaseResponse(0, 0, 0, error, null);
					return internalServerError(Json.toJson(response));
				} else {
					response.setBaseResponse(0, 0, 0, message, null);
					return badRequest(Json.toJson(response));
				}
			}
			response.setBaseResponse(0, 0, 0, inputParameter, null);
			return badRequest(Json.toJson(response));
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}

	@ApiOperation(value = "Reset password", notes = "Reset password.", response = BaseResponse.class, httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "body", dataType = "Object", required = true, paramType = "body", value = "Reset password data.") })
	public static Result resetPassword() {
		JsonNode json = request().body().asJson();
		if (checkAccessAuthorization("guest") == 200 && json.has("key") && json.has("password")
				&& json.has("confirm_password")) {
			String key = json.findPath("key").asText();
			String newPass = json.findPath("password").asText();
			String confPass = json.findPath("confirm_password").asText();

			String check = CommonFunction.passwordValidation(newPass, confPass);
			if (check != null) {
				response.setBaseResponse(0, 0, 0, check, null);
				return badRequest(Json.toJson(response));
			}

			Transaction txn = Ebean.beginTransaction();
			try {
				Member member = Member.find.where().eq("is_active", true).eq("reset_token", key).setMaxRows(1)
						.findUnique();
				if (member != null) {
					Date requestDate = new Date(member.resetTime);

					Calendar cal = Calendar.getInstance();
					cal.setTime(requestDate);
					cal.add(Calendar.HOUR, 1);
					if (cal.getTime().before(new Date(System.currentTimeMillis()))) {
						response.setBaseResponse(0, 0, 0, "Session has expired", null);
						return badRequest(Json.toJson(response));
					}

					member.password = Encryption.EncryptAESCBCPCKS5Padding(newPass);
					member.resetToken = "";
					member.save();
					Member.removeAllToken(member.id);
					txn.commit();
					response.setBaseResponse(1, 0, 1, success, null);
					return ok(Json.toJson(response));
				}
				response.setBaseResponse(0, 0, 0, notFound, null);
				return notFound(Json.toJson(response));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				txn.rollback();
			} finally {
				txn.end();
			}
			response.setBaseResponse(0, 0, 0, error, null);
			return badRequest(Json.toJson(response));
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}

	static String generateReferralCode() {
		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuilder salt = new StringBuilder();
		Random rnd = new Random();
		while (salt.length() < 4) { // length of the random string.
			int index = (int) (rnd.nextFloat() * SALTCHARS.length());
			salt.append(SALTCHARS.charAt(index));
		}

		String saltStr = salt.toString();
		// return saltStr;

		String SALTNUM = "123456789";
		StringBuilder saltNum = new StringBuilder();
		Random rndNum = new Random();
		while (saltNum.length() < 2) { // length of the random string.
			int index = (int) (rndNum.nextFloat() * SALTNUM.length());
			saltNum.append(SALTNUM.charAt(index));
		}

		String saltStrNum = saltNum.toString();
		// return saltStrNum;

		LocalDateTime myDateObj = LocalDateTime.now();
		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("HHmmss");
		String formattedDate = myDateObj.format(myFormatObj);
		// System.out.println("After formatting: " + formattedDate);

		// Base64.Encoder enc = Base64.getEncoder();
		String str = saltStr + formattedDate + saltStrNum;

		// encode data using BASE64
		// String encoded = enc.encodeToString(str.getBytes());
		// System.out.println("encoded value is \t"+ saltStr+ str);
		// return str;
		return str;

	}

	// public static Result GenerateCode() {
	// System.out.println(generateReferralCode());
	// response.setBaseResponse(1, 0, 1, generateReferralCode(), null);
	// return ok(Json.toJson(response));
	// }

	// odoo
	@ApiOperation(value = "Sign up", notes = "Sign up.", response = BaseResponse.class, httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "sign-up form", dataType = "temp.swaggermap.SignUpForm", required = true, paramType = "body", value = "sign-up form") })
	public static Result signUp() {
		JsonNode json = request().body().asJson();
		if (checkAccessAuthorization("guest") == 200) {
			if (json.has("email")) {
				String email = json.findPath("email").asText();
				// String username= json.findPath("username").asText();
				String username = null;
				String password = json.has("password") ? json.findPath("password").asText() : "";
				String confPassword = json.has("confirm_password") ? json.findPath("confirm_password").asText() : "";
				String fullName = json.findPath("full_name").asText();
				String gender = json.has("gender") ? json.findPath("gender").asText() : "";
				String birthDate = json.has("birth_date") ? json.findPath("birth_date").asText() : "";
				String phone = json.has("phone") ? json.findPath("phone").asText() : "";
				String deviceModel = json.has("device_model") ? json.findPath("device_model").asText() : "";
				String deviceType = json.has("device_type") ? json.findPath("device_type").asText() : "";
				String deviceId = json.has("device_id") ? json.findPath("device_id").asText() : "";
				Boolean newsLetter = !json.has("newsletter") || json.findPath("newsletter").asBoolean();
				String googleId = json.has("google_id") ? json.findPath("google_id").asText() : "";
				String fbId = json.has("fb_id") ? json.findPath("fb_id").asText() : "";
				Boolean isActive = true;
				Long merchantId = json.findPath("merchant_id").asLong();

				Merchant merchant = Merchant.merchantGetId(merchantId);
				if (merchant == null) {
					response.setBaseResponse(0, 0, 0, "merchant tidak ditemukan", null);
					return badRequest(Json.toJson(response));
				}

				// buat 1 field inputan refferal code
				String input_referral_code = json.has("input_referral_code")
						? json.findPath("input_referral_code").asText()
						: "";

				// add generate referral code ngambil dari function refferalcontroller
				String referral_code = generateReferralCode();

				// validasi referral code member
				Member member_check = Member.find.where().eq("referral_code", referral_code).setMaxRows(1).findUnique(); // new
																															// input

				String referral_code_new = referral_code;

				if (member_check != null) {
					referral_code_new = generateReferralCode();
				}

				String validation = Member.validation(email, phone, fullName, merchantId);
				if (validation == null) {
					Transaction txn = Ebean.beginTransaction();
					try {
						ObjectMapper mapper = new ObjectMapper();
						String redirect = Constant.getInstance().getFrontEndUrl() + "/activate/";

						Member newMember = new Member(email, phone, fullName);
						newMember.password = Encryption.EncryptAESCBCPCKS5Padding(password);
						newMember.setMerchant(merchant);
						newMember.save();

						// pengecekan ke table member refferal code tsb untuk menjadi id
						Member member = Member.find.where().eq("referral_code", referral_code).setMaxRows(1)
								.findUnique(); // new input
						Member referral_member = Member.find.where().eq("is_active", true)
								.eq("referral_code", input_referral_code).setMaxRows(1).findUnique(); // old cek
																										// referral

						// jika ada simpan member_referral
						if (referral_member != null) {
							// Long member_id = member.id;
							// Long referral_id = referral_member.id;

							MemberReferral newMemberReferral = new MemberReferral(member, referral_member);
							newMemberReferral.save();

							System.out.println("berhasil simpan member referral");

						}

						// Thread thread = new Thread(() -> {
						// 	try {

						// 		MailConfig.sendmail2(newMember.email, MailConfig.subjectActivation,
						// 				MailConfig.renderMailActivationMember(newMember, redirect));
						// 	} catch (Exception e) {
						// 		e.printStackTrace();
						// 	}
						// });
						// thread.start();
						// odoo
						// OdooService.getInstance().createCustomer(newMember);
						txn.commit();

						// mailchimp
						// mailchimpAddOrUpdateCustomer(newMember);

						response.setBaseResponse(1, offset, 1, success + " registering your account", null);
						return ok(Json.toJson(response));

					} catch (Exception e) {
						e.printStackTrace();
						txn.rollback();
					} finally {
						txn.end();
					}
					response.setBaseResponse(0, 0, 0, error, null);
					return badRequest(Json.toJson(response));
				}
				response.setBaseResponse(0, 0, 0, validation, null);
				return badRequest(Json.toJson(response));
			}
			response.setBaseResponse(0, 0, 0, inputParameter, null);
			return badRequest(Json.toJson(response));
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}

	private static void println(String referral_code2) {
		// TODO Auto-generated method stub

	}

	@ApiOperation(value = "Activate Member", notes = "Activate Member", response = BaseResponse.class, httpMethod = "POST")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "code", dataType = "string", required = true, paramType = "path", value = "activation code") })
	public static Result activate() {
		JsonNode json = request().body().asJson();
		if (checkAccessAuthorization("guest") == 200 && json.has("code")) {
			String code = json.findPath("code").asText();
			Member member = Member.find.where().eq("is_active", false).eq("activation_code", code).setMaxRows(1)
					.findUnique();
			if (member != null) {
				Date date = new Date();
				if (date.before(member.codeExpire)) {
					member.activationCode = "";
					member.isActive = true;
					member.save();
					response.setBaseResponse(1, 0, 1, success, null);
					return ok(Json.toJson(response));
				} else {
					response.setBaseResponse(0, 0, 0, expiredCode, null);
					return unauthorized(Json.toJson(response));
				}
			}
			response.setBaseResponse(0, 0, 0, error, null);
			return badRequest(Json.toJson(response));
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}

	public static Result checkStatusResetPassowrd() {
		JsonNode json = request().body().asJson();
		if (checkAccessAuthorization("guest") == 200 && json.has("key")) {
			String key = json.findPath("key").asText();
			Transaction txn = Ebean.beginTransaction();
			try {
				Member member = Member.find.where().eq("is_active", true).eq("reset_token", key).setMaxRows(1)
						.findUnique();
				if (member != null) {
					response.setBaseResponse(1, 0, 1, success, true);
					return ok(Json.toJson(response));
				}
				response.setBaseResponse(0, 0, 0, notFound, false);
				return notFound(Json.toJson(response));
			} catch (Exception e) {
				e.printStackTrace();
				txn.rollback();
			} finally {
				txn.end();
			}
			response.setBaseResponse(0, 0, 0, error, null);
			return badRequest(Json.toJson(response));
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}

	public static Result refreshActivationCode() {
		JsonNode json = request().body().asJson();
		if (checkAccessAuthorization("guest") == 200 && json.has("email")) {
			String email = json.findPath("email").asText();
			Transaction txn = Ebean.beginTransaction();
			try {
				Member member = Member.find.where().eq("is_active", false).eq("email", email).setMaxRows(1)
						.findUnique();
				if (member != null) {
					SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
					Date date = new Date();
					String actCode = email + formatter.format(date);
					LocalDateTime exp = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusDays(1);
					Date expCode = Date.from(exp.atZone(ZoneId.systemDefault()).toInstant());
					member.activationCode = Encryption.EncryptAESCBCPCKS5Padding(actCode);
					member.codeExpire = expCode;
					member.update();
					txn.commit();
					String redirect = Constant.getInstance().getFrontEndUrl() + "/activate/";
					Thread thread = new Thread(() -> {
						try {
							MailConfig.sendmail2(member.email, MailConfig.subjectActivation,
									MailConfig.renderMailActivationMember(member, redirect));
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
					thread.start();
					response.setBaseResponse(1, 0, 1, success + ", please check your mail", null);
					return ok(Json.toJson(response));
				}
				response.setBaseResponse(0, 0, 0, notFound, null);
				return notFound(Json.toJson(response));
			} catch (Exception e) {
				e.printStackTrace();
				txn.rollback();
			} finally {
				txn.end();
			}
			response.setBaseResponse(0, 0, 0, error, null);
			return badRequest(Json.toJson(response));
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}

	public static Result subscribe() {
		JsonNode json = request().body().asJson();
		if (checkAccessAuthorization("guest") == 200) {
			ObjectMapper mapper = new ObjectMapper();
			// boolean mailchimpEnabled =
			// Play.application().configuration().getBoolean("whizliz.social.mailchimp.enabled");
			if (MailchimpService.isEnabled()) {
				try {
					MailchimpSubscriberRequest request = mapper.readValue(json.toString(),
							MailchimpSubscriberRequest.class);
					request.setStatus(MailchimpSubscriberRequest.SUBSCRIBED);
					ServiceResponse result = MailchimpService.getInstance().AddSubscriber(request);
					if (result.getCode() == 200) {
						response.setBaseResponse(1, offset, 1, success, result);
						return ok(Json.toJson(response));
					} else {
						response.setBaseResponse(1, offset, 1, error, null);
						return ok(Json.toJson(response));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		response.setBaseResponse(0, 0, 0, error, null);
		return badRequest(Json.toJson(response));
	}

	public static void mailchimpAddOrUpdateCustomer(Member member) {
		// boolean mailchimpEnabled =
		// Play.application().configuration().getBoolean("whizliz.social.mailchimp.enabled");
		if (MailchimpService.isEnabled()) {
			MailchimpCustomerRequest request = new MailchimpCustomerRequest(member);
			MailchimpService.getInstance().AddOrUpdateCustomer(request);
		}
	}

	public static Result checkCustomer(String email, Long storeId) {
		int authority = checkAccessAuthorization("all");
		if (authority == 200 || authority == 203) {
			if (storeId == null || storeId == 0L) {
				response.setBaseResponse(0, 0, 0, "store id tidak boleh null atau nol", Boolean.FALSE);
				return badRequest(Json.toJson(response));
			}

			Store store = Store.findById(storeId);
			if (store == null) {
				response.setBaseResponse(0, 0, 0, "store id tidak ditemukan", Boolean.FALSE);
				return badRequest(Json.toJson(response));
			}

			if (email == null || email.equalsIgnoreCase("")) {
				response.setBaseResponse(0, 0, 0, "email tidak boleh null atau kosong", Boolean.FALSE);
				return badRequest(Json.toJson(response));
			}

			Member member = Member.findByEmailAndMerchantId(email, store.getMerchant().id);
			if (member == null) {
				response.setBaseResponse(0, 0, 0, "customer tidak terdaftar", Boolean.FALSE);
				return badRequest(Json.toJson(response));
			}

			Map<String, Object> responses = new HashMap<>();
			responses.put("member_id", Integer.valueOf(Math.toIntExact(member.id)));
			responses.put("email", member.email);
			responses.put("name", member.fullName);
			responses.put("phone_number", member.phone);
			responses.put("status", Boolean.TRUE);

			response.setBaseResponse(0, 0, 0, success, responses);
			return ok(Json.toJson(response));
		} else if (authority == 403) {
			response.setBaseResponse(0, 0, 0, forbidden, null);
			return forbidden(Json.toJson(response));
		} else {
			response.setBaseResponse(0, 0, 0, unauthorized, null);
			return unauthorized(Json.toJson(response));
		}
	}

	public static Result checkCustomerv2() {
		int authority = checkAccessAuthorization("all");
		if (authority == 200 || authority == 203) {
			JsonNode json = request().body().asJson();
			String phone = json.get("phone").asText().toLowerCase();
			String email = json.get("email").asText();
			Long merchantId = json.get("merchant_id").asLong();

			if (email == null && email == "" || phone == null && phone == "") {
				response.setBaseResponse(0, 0, 0, "Email atau Nomor Telepon dibutuhkan", null);
        return badRequest(Json.toJson(response));
			}
			if (merchantId == null || merchantId == 0L) {
				response.setBaseResponse(0, 0, 0, "merchant id tidak boleh null atau nol", Boolean.FALSE);
				return badRequest(Json.toJson(response));
			}

			Merchant store = Merchant.merchantGetId(merchantId);
			if (store == null) {
				response.setBaseResponse(0, 0, 0, "merchant id tidak ditemukan", Boolean.FALSE);
				return badRequest(Json.toJson(response));
			}

			// if (email == null || email.equalsIgnoreCase("")) {
			// 	response.setBaseResponse(0, 0, 0, "email tidak boleh null atau kosong", Boolean.FALSE);
			// 	return badRequest(Json.toJson(response));
			// }

			Member member = Member.findByEmailAndMerchantId(email, merchantId);
			if (member == null) {
				response.setBaseResponse(0, 0, 0, "customer tidak terdaftar", Boolean.FALSE);
				return badRequest(Json.toJson(response));
			}

			Map<String, Object> responses = new HashMap<>();
			responses.put("member_id", Integer.valueOf(Math.toIntExact(member.id)));
			responses.put("email", member.email);
			responses.put("name", member.fullName);
			responses.put("phone_number", member.phone);
			// responses.put("loyalty_point", member.loyaltyPoint);
			responses.put("loyalty_point", String.valueOf(member.loyaltyPoint));
			responses.put("status", Boolean.TRUE);

			response.setBaseResponse(0, 0, 0, success, responses);
			return ok(Json.toJson(response));
		} else if (authority == 403) {
			response.setBaseResponse(0, 0, 0, forbidden, null);
			return forbidden(Json.toJson(response));
		} else {
			response.setBaseResponse(0, 0, 0, unauthorized, null);
			return unauthorized(Json.toJson(response));
		}
	}

	public static Result updateProfile() {
		int authority = checkAccessAuthorization("all");
		if (authority == 200 || authority == 203) {
			try {
				JsonNode json = request().body().asJson();
				Long merchantId = json.findPath("merchant_id").asLong();
				String email = json.findPath("email").asText();
				String name = json.findPath("name").asText();
				String phoneNumber = json.findPath("phone_number").asText();

				String validation = validateUpdateProfileRequest(phoneNumber, merchantId);
				if (validation != null) {
					response.setBaseResponse(0, 0, 0, validation, null);
					return badRequest(Json.toJson(response));
				}

				Member member = Member.findByEmailAndMerchantId(email, merchantId);
				if (member == null) {
					response.setBaseResponse(0, 0, 0, "member tidak ditemukan.", null);
					return badRequest(Json.toJson(response));
				}

				String[] names = name.split("\\s+");
				if (names.length > 1) {
					member.firstName = names[0];
					member.lastName = names[1];
				}
				member.fullName = name;
				member.email = email;
				member.phone = phoneNumber;

				member.update();

				response.setBaseResponse(0, 0, 0, success + " update profile customer", member.id);
				return ok(Json.toJson(response));
			} catch (Exception e) {
				response.setBaseResponse(0, 0, 0, error, null);
				return internalServerError(Json.toJson(response));
			}
		} else if (authority == 403) {
			response.setBaseResponse(0, 0, 0, forbidden, null);
			return forbidden(Json.toJson(response));
		} else {
			response.setBaseResponse(0, 0, 0, unauthorized, null);
			return unauthorized(Json.toJson(response));
		}
	}

	private static String validateUpdateProfileRequest(String phoneNumber, Long merchantId) {
		if (!phoneNumber.isEmpty()) {
			Member member = Member.findByPhoneAndMerchantId(phoneNumber, merchantId);
			if (member != null) {
				return "Nomor telepon sudah terpakai.";
			}
		}

		return null;
	}

	public static Result signInGoogleSandbox() {
		JsonNode json = request().body().asJson();
		if (checkAccessAuthorization("guest") == 200 && json.has("google_user_id") && json.has("access_token")) {
			String googleUserId = json.findPath("google_user_id").asText();
			String accessToken = json.findPath("access_token").asText();
			String firstName = json.has("first_name") ? json.findPath("first_name").asText() : "";
			ObjectMapper mapper = new ObjectMapper();
			ServiceResponse sresponse = GooglePlusService.getInstance().getGooglePlusUserData(accessToken);
			if (sresponse.getCode() == 200) {
				GoogleOauthUserinfoResponse guser = mapper.convertValue(sresponse.getData(), GoogleOauthUserinfoResponse.class);
				if (googleUserId != null && !googleUserId.isEmpty() && googleUserId.equals(guser.getSub())) {
					Transaction txn = Ebean.beginTransaction();
					try {
						Member target = Member.find.where().eq("google_user_id", googleUserId).findUnique();
						if (target != null && !target.isActive) {
							response.setBaseResponse(0, 0, 0, "Your account was disabled", null);
							return badRequest(Json.toJson(response));
						} else if (target == null) {
							target = new Member();
							target.fullName = (firstName != null && !firstName.trim().isEmpty()) ? firstName : guser.getName();
							target.email = guser.getEmail();
							target.phone = null;
							target.googleUserId = guser.getSub();
							target.isActive = true;
							target.save();
							txn.commit();
						}
						response.setBaseResponse(1, 0, 1, success, new GoogleOauthUserinfoSandboxResponse(target.googleUserId, target.fullName, target.email));
						return ok(Json.toJson(response));
					} catch (Exception e) {
						e.printStackTrace();
						txn.rollback();
					} finally {
						txn.end();
					}
					response.setBaseResponse(0, 0, 0, error, null);
					return internalServerError(Json.toJson(response));
				}
				response.setBaseResponse(0, 0, 0, "Token didn't match Google user ID", null);
				return badRequest(Json.toJson(response));
			} else if (sresponse.getCode() == 408) {
				response.setBaseResponse(0, 0, 0, timeOut, null);
				return badRequest(Json.toJson(response));
			} else {
				response.setBaseResponse(0, 0, 0, sresponse.getCode() + " " + error, null);
				return badRequest(Json.toJson(response));
			}
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}
	
}
