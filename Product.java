package com.bazario.app.model;

import java.io.Serializable;

public class Product implements Serializable {
    public final int id;
    public final String name;
    public final String category;
    public final double price;
    public final double oldPrice;
    public final int imageRes;
    public final String description;

    public Product(int id, String name, String category, double price, double oldPrice, int imageRes, String description) {
        this.id = id; this.name = name; this.category = category; this.price = price;
        this.oldPrice = oldPrice; this.imageRes = imageRes; this.description = description;
    }
}
