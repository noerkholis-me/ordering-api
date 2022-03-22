package com.hokeba.util;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import play.mvc.Http.MultipartFormData.FilePart;

import java.io.*;
import java.text.*;
import java.text.Normalizer.Form;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class CommonFunction {
	//Standar validasi password : tidak ada spasi, minimal 8 karakter, 
	//dan terpenuhi 3 kondisi di antara : 1 huruf kecil, 1 huruf besar, 1 angka, atau 1 simbol
//	public static final String passwordRegex =
//			"^(((?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^\\w]))|"
//			+ "((?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]))|"
//			+ "((?=.*[0-9])(?=.*[a-z])(?=.*[^\\w]))|"
//			+ "((?=.*[0-9])(?=.*[^\\w])(?=.*[A-Z]))|"
//			+ "((?=.*[^\\w])(?=.*[a-z])(?=.*[A-Z])))"
//			+ "(?=\\S+$)(.{7,})$";
// public static final String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$";
public static final String passwordRegex = "^.{8,}$";
	public static final String emailRegex =
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
					+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	public static final String phoneRegex = "^(?=.{11,16}$)([0])([0-9])*$";
	public static final String usernameRegex = "^(?=.{8,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$";
	public static final String nameRegex = "^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$";

	// Otniel, 09-01-2017, Method untuk generate slug.
	public static String slugGenerate(String input) {
		String slug = Normalizer.normalize(input.toLowerCase(), Form.NFD)
				.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").replaceAll("[^\\p{Alnum}]+", "-");
		if (slug.charAt(slug.length() - 1) == '-') {
			return slug.substring(0, slug.length() - 1);
		} else
			return slug;
	}

	// Alex, 29-11-2016, method ini mengembalikan tanggal dan waktu saat ini
	// sesuai dengan format yang dimasukkan.
	public static String getCurrentTime(String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		Date currentDateTime = new Date();
		String stringDate = dateFormat.format(currentDateTime);
		return stringDate;

	}

	// Otniel, 29-11-2016, method ini mengembalikan list dari input file
	// (csv,xlsx)
	public static List<List<String>> insertFileCSV(FilePart input) {

		List<List<String>> rows = new ArrayList<List<String>>();
		String line = "";
		try (BufferedReader br = new BufferedReader(new FileReader(input.getFile()))) {
			while ((line = br.readLine()) != null) {
				List<String> column = new ArrayList<String>(Arrays.asList(line.split(",")));
				rows.add(column);
			}
			System.out.println("ininaoidfa csv " + rows.get(0).size());
			return rows;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static List<List<String>> insertFileXlsx(FilePart input) {

		List<List<String>> rows = new ArrayList<List<String>>();
		String ext = FilenameUtils.getExtension(input.getFilename());
		try {
			FileInputStream excelFile = new FileInputStream(input.getFile());
			Workbook workbook = new XSSFWorkbook(excelFile);
			Sheet datatypeSheet = workbook.getSheetAt(0);
			Iterator<Row> iterator = datatypeSheet.iterator();
			while (iterator.hasNext()) {

				Row currentRow = iterator.next();
				Iterator<Cell> cellIterator = currentRow.iterator();
				List<String> column = new ArrayList<String>();
				while (cellIterator.hasNext()) {

					Cell currentCell = cellIterator.next();
					// getCellTypeEnum shown as deprecated for version
					// 3.15
					// getCellTypeEnum ill be renamed to getCellType
					// starting from version 4.0
					String cell = currentCell.getStringCellValue();
					column.add(cell);
				}
				System.out.println(ext);
				rows.add(column);
			}
			System.out.println("ininaoidfa " + rows.size());
			return rows;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	// Alex, 19-01-2017, method ini digunakan untuk validasi password
	// Standar password yang digunakan terdapat pada attribut passwordRegex
	public static boolean passwordValidation(String password){
		return password.matches(passwordRegex);
	}

	public static String passwordValidation(String password, String confPassword){
		if(password==null||confPassword==null){
			return "Please input password and confirm password";
		}
		if (!CommonFunction.passwordValidation(password)) {
			return "Password must be at least 6 character, has no whitespace, "
					+ "and have at least 3 variations from uppercase, lowercase, number, or symbol";
		}
		if (!confPassword.equals(password)) {
			return "Password didn't match confirm password";
		}
		return null;
	}
	
	//Alex, 25-01-2017, method ini digunakan untuk memparsing nominal yang besar
	//Nominal besar tersebut akan diparsing menjadi sebuah string yang lebih sederhana
	//contoh : nominal 10000 menjadi 10k
	public static String getStringNominal(long number){
		String res;
		if(number/1000000000>0){
			res = number/1000000000+((number/1000000000<10)? "."+((number/100000000)%10) : "")+"B";
		} else if (number/1000000>0){
			res = number/1000000+((number/1000000<10)? "."+((number/100000)%10) : "")+"M";
		} else if (number/1000>0){
			res = number/1000+((number/1000<10)? "."+((number/100)%10) : "")+"k";
		} else {
			res = ""+number;
		}
		return res;
	}
	
	//Alex, 25-01-2017, method ini digunakan untuk mendapatkan Date 
	//berdasarkan String dan format yang dimasukkan
	public static Date getDateFrom(String date, String format) {
		DateFormat formatter = new SimpleDateFormat(format);
		Date dateResult = null;
		try {
			dateResult = formatter.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateResult;
	}

	public static String getDateFrom(Date date, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		String stringDate ="";
		if(date != null){
			stringDate = dateFormat.format(date);
		}
		return stringDate;
	}

	public static Date getDateFrom(String date) {
		return getDateFrom(date, "yyyy-MM-dd");
	}

	public static Map getDateFromBetween(String date) {
		return getDateFromBetween(date, date);
	}

	public static Map getDateFromBetween(String date, String date2) {
		Map<String, Date> result = new HashMap<>();
		result.put("sdate", getDateTimeStartFromDate(date));
		result.put("edate", getDateTimeEndFromDate(date2));
		return result;
	}

	public static Date getDateTimeStartFromDate(String date) {
		return getDateFrom(date.concat(" 00:00:00"), "yyyy-MM-dd HH:mm:ss");
	}

	public static Date getDateTimeEndFromDate(String date) {
		return getDateFrom(date.concat(" 23:59:59"), "yyyy-MM-dd HH:mm:ss");
	}

	public static String getDate(Date date) {
        return date==null ? "" : getDateFrom(date, "yyyy-MM-dd");
	}

	public static String getDate2(Date date) {
        return date==null ? "" : getDateFrom(date, "dd/MM/yyyy");
	}

	public static String getDateTime(Date date) {
		return date==null ? "" : getDateFrom(date, "yyyy-MM-dd HH:mm:ss");
	}

	public static String getDateTime2(Date date) {
		return date==null ? "" : getDateFrom(date, "dd/MM/yyyy HH:mm:ss");
	}
	
	public static String getStringMonthYear(int month, int year){
		String res = "";
		switch(month){
			case 1  : {res = "January "+year; break;}
			case 2  : {res = "February "+year; break;}
			case 3  : {res = "March "+year; break;}
			case 4  : {res = "April "+year; break;}
			case 5  : {res = "May "+year; break;}
			case 6  : {res = "June "+year; break;}
			case 7  : {res = "July "+year; break;}
			case 8  : {res = "August "+year; break;}
			case 9  : {res = "September "+year; break;}
			case 10 : {res = "October "+year; break;}
			case 11 : {res = "November "+year; break;}
			case 12 : {res = "December "+year; break;}
		}
		return res;
	}

	public static Date addDate(Date date, int diff) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, diff);
		return c.getTime();
	}
	
	//Alex, 27-01-2017, method ini digunakan untuk generate string acak.
	//Panjang dari string sesuai dengan nilai yang dimasukkan
	public static String generateRandomString(int length){
		Random rd = new Random();
		String res = "";
		int count = 0;
		while(count<length){
			String variation = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
			res += variation.charAt(rd.nextInt(variation.length()));
			count++;
		}
		return res;
	}

	public static String numberFormat(Double amount) {
		return String.format("%,.2f", amount);

	}

	public static String discountFormat(Double amount) {
		DecimalFormat df = new DecimalFormat("###.#");
		return df.format(amount);
//		return String.format("%,.2f", amount);

	}
	
	public static long getDateDifference(Date date1, Date date2, TimeUnit timeUnit) {
		long diffInMillies = date1.getTime() - date2.getTime();
		return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
	}

	public static String currencyFormat(Double amount, String currencyCode) {
		return currencyCode+" "+numberFormat(amount);

	}
}
