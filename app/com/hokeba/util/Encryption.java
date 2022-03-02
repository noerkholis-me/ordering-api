package com.hokeba.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encryption {
    public static final String KEY_SHA256 = "SHA-256";
	public static final String KEY_SHA512 = "SHA-512";

	public static String KEY = "770A8A65DA156D24";
	public static String IV = "9mjs7ws3dlsugfhe";

	public static String EncryptAESCBCPCKS5Padding(String plain) {

		byte[] key = KEY.getBytes();
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

		byte[] iv = IV.getBytes();
		IvParameterSpec ivspec = new IvParameterSpec(iv);

		// initialize the cipher for encrypt mode
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivspec);

			// encrypt the message
			byte[] encrypted = cipher.doFinal(plain.getBytes());
			// return new String(encrypted);

			return DatatypeConverter.printHexBinary(encrypted);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String DecryptAESCBCPCKS5Padding(String encryptText) {

		byte[] key = KEY.getBytes();
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");

		byte[] iv = IV.getBytes();
		IvParameterSpec ivspec = new IvParameterSpec(iv);

		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivspec);

			byte[] decrypted = cipher.doFinal(DatatypeConverter.parseHexBinary(encryptText));

			return new String(decrypted);
		} catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | IllegalBlockSizeException | InvalidKeyException | BadPaddingException | NoSuchPaddingException e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * Hex encodes a byte array. <BR>
	 * Returns an empty string if the input array is null or empty.
	 * 
	 * @param input
	 *            bytes to encode
	 * @return string containing hex representation of input byte array
	 */
	public static String hexEncode(byte[] input) {
		if (input == null || input.length == 0) {
			return "";
		}

		int inputLength = input.length;
		StringBuilder output = new StringBuilder(inputLength * 2);

		for (int i = 0; i < inputLength; i++) {
			int next = input[i] & 0xff;
			if (next < 0x10) {
				output.append("0");
			}

			output.append(Integer.toHexString(next));
		}

		return output.toString();
	}

	public static String MD5(String plain) throws NoSuchAlgorithmException {
		String enc = null;

		MessageDigest md = MessageDigest.getInstance("MD5");
		md.reset();

		md.update(plain.getBytes());
		byte[] encPasswordByte = md.digest();

		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < encPasswordByte.length; i++) {
			hexString.append(Integer.toHexString(0xFF & encPasswordByte[i]));
		}

		enc = hexString.toString();

		return enc;
	}

	public static String SHA1(String plain) throws NoSuchAlgorithmException {
		String enc = null;

		MessageDigest md = MessageDigest.getInstance("SHA1");
		md.reset();

		md.update(plain.getBytes());
		byte[] encPasswordByte = md.digest();

		/*
		 * StringBuffer hexString = new StringBuffer(); for (int i = 0; i <
		 * encPasswordByte.length; i++) {
		 * hexString.append(Integer.toHexString(0xFF & encPasswordByte[i])); }
		 * enc = hexString.toString();
		 */

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < encPasswordByte.length; i++) {
			sb.append(Integer.toString((encPasswordByte[i] & 0xff) + 0x100, 16).substring(1));
		}
		enc = sb.toString();

		return enc;
	}

	public static String HMACSHA256(String plain) throws NoSuchAlgorithmException {
		String enc = null;
		try {
			String secret = "secret";

			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
			sha256_HMAC.init(secret_key);
			enc = Base64.encodeBase64String(sha256_HMAC.doFinal(plain.getBytes()));

		} catch (Exception e) {
			System.out.println("Error");
		}
		return enc;
	}
	
	/**
     * Method ini digunakan untuk hashing String 'plain' dengan metode yang dijabarkan melalui 'hashMethodCode'
     * 
     * @param plain
     * @param hashMethodCode
     * @return String hasil hashing
     * @throws NoSuchAlgorithmException
     */
    public static String hash(String plain, String hashMethodCode) throws NoSuchAlgorithmException {
    	final MessageDigest sha = MessageDigest.getInstance(hashMethodCode);
        sha.update(plain.getBytes());
        
        byte data[] = sha.digest();
        StringBuffer hexData = new StringBuffer();
        for (int byteIndex = 0; byteIndex < data.length; byteIndex++)
            hexData.append(Integer.toString((data[byteIndex] & 0xff) + 0x100, 16).substring(1));
        
        return hexData.toString();
    }

}
