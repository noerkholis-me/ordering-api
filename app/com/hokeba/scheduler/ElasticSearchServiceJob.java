package com.hokeba.scheduler;

import com.github.cleverage.elasticsearch.IndexService;
import models.Product;

/**
 * Created by hendriksaragih on 7/2/17.
 */
public class ElasticSearchServiceJob extends BaseJob {

    public ElasticSearchServiceJob(String cron) {
        super(cron);
    }

    @Override
    public void doJob() {
        syncElasticSearch();
    }

    private void syncElasticSearch(){
        IndexService.cleanIndex();
        Product.find.where().eq("is_deleted", false)
                .eq("first_po_status", 1)
                .eq("approved_status", "A")
                .eq("status", true)
                .eq("is_show", true)
                .gt("item_count", 0)
                .findList()
                .forEach(p->{
                    indexing.Product product = new indexing.Product(p);
                    product.index();
                });
    }
}