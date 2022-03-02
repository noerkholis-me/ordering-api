package com.hokeba.util;

import java.security.SecureRandom;

public class OtpGenerator {
	private static final String NUMBER = "0123456789";

	public static String randomString(int len) {
		SecureRandom rnd = new SecureRandom();
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(NUMBER.charAt(rnd.nextInt(NUMBER.length())));
		return sb.toString();
	}

}
