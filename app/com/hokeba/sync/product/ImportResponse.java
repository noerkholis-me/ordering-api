package com.hokeba.sync.product;

public class ImportResponse {
	public boolean status;
	public String message;
	public int importedRows;
	
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getRow() {
		return importedRows;
	}
	public void setRow(int row) {
		this.importedRows = row;
	}

	

}
