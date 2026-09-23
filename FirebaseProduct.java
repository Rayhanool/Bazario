package com.bazario.app.model;

import java.io.Serializable;

public class FirebaseProduct implements Serializable {
    public String id, shopId, shopName, name, category, description, area, unit;
    public double price, oldPrice, rating;
    public long stock, reviewCount;
    public boolean available;

    public FirebaseProduct() {}

    public String unitLabel() { return ProductUnit.label(unit); }

    public boolean matches(String q) {
        String s = q == null ? "" : q.toLowerCase().trim();
        return s.isEmpty()
                || (name != null && name.toLowerCase().contains(s))
                || (category != null && category.toLowerCase().contains(s))
                || (shopName != null && shopName.toLowerCase().contains(s));
    }
}
