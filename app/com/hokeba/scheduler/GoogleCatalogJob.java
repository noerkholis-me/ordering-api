package com.hokeba.scheduler;
import models.Product;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.mapping.response.MapGoogleCatalog;
import com.hokeba.util.Constant;

public class GoogleCatalogJob extends BaseJob {

	public GoogleCatalogJob(String cron) {
		super(cron);
	}
	
	@Override
	public void doJob() {
		createGoogleCatalog();
	}
	
	private void createGoogleCatalog() {
		List<Product> products = Product.find.where().eq("isDeleted", false).findList();
		ObjectMapper mapper = new ObjectMapper();
    	MapGoogleCatalog[] results = mapper.convertValue(products, MapGoogleCatalog[].class);

        try {
			PrintWriter pw = new PrintWriter(Constant.getInstance().getCatalogPath() + "google-catalog.csv");
			pw.println("id,title,description,price,condition,availability,link,image_link,brand");
			for (MapGoogleCatalog item : results) {
				pw.println(item.sku +","+ item.name +","+ item.name +","+ item.getPriceDisplay() +","+ item.getCondition() + ","+ item.getAvailability() +","+ item.getLink() + ","+ item.imageUrl +","+ item.brandName);
			}
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
