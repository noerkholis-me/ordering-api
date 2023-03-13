package dtos.store;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.hokeba.util.Helper;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.finance.FinanceWithdraw;

@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoreWithdrawEmail {
	
	private String adminName;
	private String invoiceNumber;
	private String amount;
	private String remainingBalance;
	private String date;
	private String account;
	private String storeName;
	
	
	public static StoreWithdrawEmail getInstance(FinanceWithdraw data) {
		StoreWithdrawEmail response = new StoreWithdrawEmail();
		SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy - HH : mm");
		response.adminName = data.getRequestBy();
		response.invoiceNumber = data.getRequestNumber();
		response.amount = Helper.getRupiahFormat(data.getAmount().doubleValue());
		response.remainingBalance = Helper.getRupiahFormat(data.getStore().getActiveBalance().doubleValue());
		response.date = sdf.format(new Date());
		response.account = data.getAccountNumber();
		response.storeName = data.getStore().storeName;
		return response;
	}

	public String getAdminName() {
		return adminName;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public String getAmount() {
		return amount;
	}

	public String getRemainingBalance() {
		return remainingBalance;
	}

	public String getDate() {
		return date;
	}

	public String getAccount() {
		return account;
	}
	
	public String getStoreName () {
		return storeName;
	}
	
	
	
}
