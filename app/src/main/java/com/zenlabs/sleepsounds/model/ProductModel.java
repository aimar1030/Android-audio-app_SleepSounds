package com.zenlabs.sleepsounds.model;

import java.util.Date;

/**
 * Created by fedoro on 6/7/16.
 */
public class ProductModel {

    public String sku;
    public Date product_time;

    public ProductModel()   {

        sku = "";
        product_time = new Date();
    }
}
