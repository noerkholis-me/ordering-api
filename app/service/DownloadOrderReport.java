package service;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import models.*;
import models.merchant.*;
import models.transaction.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import play.Logger;

import repository.*;


public class DownloadOrderReport {

    private static final String[] COLUMNS = {"No", "Tanggal Order", "No Order", "Tipe Order", "Nama Customer", "Nama Toko", "Antrian", "Nama Produk", "Tipe Produk", "Harga Produk", "Quantity", "Total Harga Produk", "Tax", "Service", "Payment Fee Owner", "Payment Fee Customer", "Total Harga", "Payment Fee Type", "Status", "Kategori Produk", "Metode Pembayaran" };

    private static final String FILE_NAME = "Order";
    private static final String FILE_TYPE = ".xlsx";

    public static File downloadOrderReport(List<Order> orderData) throws Exception{
        File file = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");

            file = File.createTempFile(FILE_NAME+simpleDateFormat, FILE_TYPE);
            file.deleteOnExit();

            FileOutputStream fileOut = new FileOutputStream(file);
            Workbook workbook = new XSSFWorkbook();
            CreationHelper createHelper = workbook.getCreationHelper();
            Sheet sheet = workbook.createSheet("Order - " + simpleDateFormat.format(new Date()).toString());

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerFont.setColor(IndexedColors.BLACK.getIndex());

            CellStyle titleCellStyle = workbook.createCellStyle();
            titleCellStyle.setFont(headerFont);

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
            headerCellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
            headerCellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
            headerCellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
            cellStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);

            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy HH:mm:ss"));
            dateStyle.setBorderLeft(XSSFCellStyle.BORDER_THIN);
            dateStyle.setBorderRight(XSSFCellStyle.BORDER_THIN);
            dateStyle.setBorderTop(XSSFCellStyle.BORDER_THIN);
            dateStyle.setBorderBottom(XSSFCellStyle.BORDER_THIN);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < COLUMNS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(COLUMNS[i]);
                cell.setCellStyle(headerCellStyle);
            }

            int row = 1;
            int number = 1;
            for (Order data : orderData) {
                // GET ORDER PAYMENT DETAIL
                Optional<OrderPayment> orderPayment = OrderPaymentRepository.findByOrderId(data.id);
                OrderPayment getOrderPayment = orderPayment.get();
                
                // GET NAME OF CUSTOMER
                Member member = null;
                if(data.getMember() != null) {
                    member = Member.findByIdMember(data.getMember().id);
                }
                
                if(getOrderPayment.getStatus().equalsIgnoreCase("PAID") || getOrderPayment.getStatus().equalsIgnoreCase("CANCEL") || getOrderPayment.getStatus().equalsIgnoreCase("CANCELED")){
                    Row rowSheet = sheet.createRow(row++);
                    // GET PRODUCT DETAIL ORDER ON MAIN PRODUCT
                    List<OrderDetail> orderDetails = OrderRepository.findOrderDetailByOrderId(data.id);
                    for(OrderDetail oDetail : orderDetails) {
                        // rowSheet = sheet.createRow(row++);
                        rowSheet.createCell(0).setCellValue(number++);
                        rowSheet.getCell(0).setCellStyle(cellStyle);
                        
                        // Tanggal Order
                        rowSheet.createCell(1).setCellValue("'"+data.getOrderDate());
                        rowSheet.getCell(1).setCellStyle(cellStyle);
                        
                        // No Order
                        rowSheet.createCell(2).setCellValue(data.getOrderNumber());
                        rowSheet.getCell(2).setCellStyle(dateStyle);

                        // Tipe Order
                        rowSheet.createCell(3).setCellValue(data.getOrderType());
                        rowSheet.getCell(3).setCellStyle(cellStyle);

                        // Nama Customer
                        rowSheet.createCell(4).setCellValue(member != null ? member.fullName : "GENERAL CUSTOMER ("+ data.getStore().storeName +")");
                        rowSheet.getCell(4).setCellStyle(cellStyle);

                        // Nama Toko
                        rowSheet.createCell(5).setCellValue(data.getStore().storeName);
                        rowSheet.getCell(5).setCellStyle(cellStyle);

                        // Antrian
                        rowSheet.createCell(6).setCellValue(data.getOrderQueue());
                        rowSheet.getCell(6).setCellStyle(cellStyle);

                        // Nama Produk
                        rowSheet.createCell(7).setCellValue(oDetail.getProductName());
                        rowSheet.getCell(7).setCellStyle(cellStyle);
                        System.out.print("Product id: ");
                        System.out.println(oDetail.getProductMerchant().id);
                        
                        // Tipe Produk
                        ProductMerchantDetail pmdetail = ProductMerchantDetailRepository.getTypeData(oDetail.getProductMerchant().id);
                        
                        if (pmdetail != null) {
                            rowSheet.createCell(8).setCellValue(pmdetail.getProductType());
                            rowSheet.getCell(8).setCellStyle(cellStyle);
                        }
                        
                        System.out.print("Product: ");
                        System.out.println(oDetail.getProductMerchant());

                        // Harga Produk
                        rowSheet.createCell(9).setCellValue(oDetail.getProductPrice().doubleValue());
                        rowSheet.getCell(9).setCellStyle(cellStyle);

                        // Quantity
                        rowSheet.createCell(10).setCellValue(oDetail.getQuantity());
                        rowSheet.getCell(10).setCellStyle(cellStyle);

                        // Total Harga Produk
                        rowSheet.createCell(11).setCellValue(oDetail.getSubTotal().doubleValue());
                        rowSheet.getCell(11).setCellStyle(cellStyle);

                        // Tax
                        rowSheet.createCell(12).setCellValue(getOrderPayment.getTaxPrice().doubleValue());
                        rowSheet.getCell(12).setCellStyle(cellStyle);

                        // Service
                        rowSheet.createCell(13).setCellValue(getOrderPayment.getServicePrice().doubleValue());
                        rowSheet.getCell(13).setCellStyle(cellStyle);

                        // Payment Fee Owner
                        rowSheet.createCell(14).setCellValue(getOrderPayment.getPaymentFeeOwner() != null ? getOrderPayment.getPaymentFeeOwner().doubleValue() : 0);
                        rowSheet.getCell(14).setCellStyle(cellStyle);

                        // Payment Fee Customer
                        rowSheet.createCell(15).setCellValue(getOrderPayment.getPaymentFeeCustomer() != null ? getOrderPayment.getPaymentFeeCustomer().doubleValue() : 0);
                        rowSheet.getCell(15).setCellStyle(cellStyle);

                        // Total Harga
                        rowSheet.createCell(16).setCellValue(data.getTotalPrice().doubleValue());
                        rowSheet.getCell(16).setCellStyle(cellStyle);

                        // Payment Fee Type
                        rowSheet.createCell(17).setCellValue(getOrderPayment.getPaymentFeeType());
                        rowSheet.getCell(17).setCellStyle(cellStyle);

                        // Status
                        rowSheet.createCell(18).setCellValue(data.getStatus() +" - ("+ getOrderPayment.getStatus() + ")");
                        rowSheet.getCell(18).setCellStyle(cellStyle);
                        
                        //Kategori Produk
                        rowSheet.createCell(19).setCellValue(oDetail.getProductMerchant().getCategoryMerchant().getCategoryName());
                        rowSheet.getCell(19).setCellStyle(cellStyle);

                        //Metode Pembayaran
                        rowSheet.createCell(20).setCellValue(data.getOrderPayment().getPaymentType());
                        rowSheet.getCell(20).setCellStyle(cellStyle);

                        // GET PRODUCT ADD ON FROM PRODUCT MAIN
                        for(OrderDetailAddOn orderDetailAddOn: oDetail.getOrderDetailAddOns()) {
                            rowSheet = sheet.createRow(row++);

                            rowSheet.createCell(0).setCellValue(number++);
                            rowSheet.getCell(0).setCellStyle(cellStyle);
                            
                            // Tanggal Order
                            rowSheet.createCell(1).setCellValue("'"+data.getOrderDate());
                            rowSheet.getCell(1).setCellStyle(cellStyle);
                            
                            // No Order
                            rowSheet.createCell(2).setCellValue(data.getOrderNumber());
                            rowSheet.getCell(2).setCellStyle(dateStyle);

                            // Tipe Order
                            rowSheet.createCell(3).setCellValue(data.getOrderType());
                            rowSheet.getCell(3).setCellStyle(cellStyle);

                            // Nama Customer
                            rowSheet.createCell(4).setCellValue(member != null ? member.fullName : "GENERAL CUSTOMER ("+ data.getStore().storeName +")");
                            rowSheet.getCell(4).setCellStyle(cellStyle);

                            // Nama Toko
                            rowSheet.createCell(5).setCellValue(data.getStore().storeName);
                            rowSheet.getCell(5).setCellStyle(cellStyle);

                            // Antrian
                            rowSheet.createCell(6).setCellValue(data.getOrderQueue());
                            rowSheet.getCell(6).setCellStyle(cellStyle);

                            // Nama Produk
                            rowSheet.createCell(7).setCellValue(orderDetailAddOn.getProductName());
                            rowSheet.getCell(7).setCellStyle(cellStyle);

                            System.out.print("Product add on id: ");
                            System.out.println(orderDetailAddOn.getProductAssignId());

                            // Tipe Produk
                            ProductMerchantDetail pmdetailAddOn = ProductMerchantDetailRepository.getTypeData(orderDetailAddOn.getProductAssignId());
                            if (pmdetailAddOn != null) {
                                rowSheet.createCell(8).setCellValue(pmdetailAddOn.getProductType());
                                rowSheet.getCell(8).setCellStyle(cellStyle);
                            }

                            System.out.println("End of Product Add On ID");

                            // Harga Produk
                            rowSheet.createCell(9).setCellValue(orderDetailAddOn.getProductPrice().doubleValue());
                            rowSheet.getCell(9).setCellStyle(cellStyle);

                            // Quantity
                            rowSheet.createCell(10).setCellValue(orderDetailAddOn.getQuantity());
                            rowSheet.getCell(10).setCellStyle(cellStyle);

                            // Total Harga Produk
                            rowSheet.createCell(11).setCellValue(orderDetailAddOn.getSubTotal().doubleValue());
                            rowSheet.getCell(11).setCellStyle(cellStyle);

                            // Tax
                            rowSheet.createCell(12).setCellValue(getOrderPayment.getTaxPrice().doubleValue());
                            rowSheet.getCell(12).setCellStyle(cellStyle);

                            // Service
                            rowSheet.createCell(13).setCellValue(getOrderPayment.getServicePrice().doubleValue());
                            rowSheet.getCell(13).setCellStyle(cellStyle);

                            // Payment Fee Owner
                            rowSheet.createCell(14).setCellValue(getOrderPayment.getPaymentFeeOwner() != null ? getOrderPayment.getPaymentFeeOwner().doubleValue() : 0);
                            rowSheet.getCell(14).setCellStyle(cellStyle);

                            // Payment Fee Customer
                            rowSheet.createCell(15).setCellValue(getOrderPayment.getPaymentFeeCustomer() != null ? getOrderPayment.getPaymentFeeCustomer().doubleValue() : 0);
                            rowSheet.getCell(15).setCellStyle(cellStyle);

                            // Total Harga
                            rowSheet.createCell(16).setCellValue(data.getTotalPrice().doubleValue());
                            rowSheet.getCell(16).setCellStyle(cellStyle);

                            // Payment Fee Type
                            rowSheet.createCell(17).setCellValue(getOrderPayment.getPaymentFeeType());
                            rowSheet.getCell(17).setCellStyle(cellStyle);

                            // Status
                            rowSheet.createCell(18).setCellValue(data.getStatus() +" - ("+ getOrderPayment.getStatus() + ")");
                            rowSheet.getCell(18).setCellStyle(cellStyle);
                            
                            //Kategori Produk
                            rowSheet.createCell(19).setCellValue(oDetail.getProductMerchant().getCategoryMerchant().getCategoryName());
                            rowSheet.getCell(19).setCellStyle(cellStyle);

                            //Metode Pembayaran
                            rowSheet.createCell(20).setCellValue(data.getOrderPayment().getPaymentType());
                            rowSheet.getCell(20).setCellStyle(cellStyle);
                        }
                    }
                }
            }

            // Resize all columns to fit the content size
            for (int i = 0; i < COLUMNS.length; i++) {
                sheet.autoSizeColumn(i);
            }
            // Write the output to a file
            workbook.write(fileOut);
            fileOut.close();
            // Closing the workbook
            workbook.close();

            return file;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
