package com.bazario.app.model;

import java.io.Serializable;

public class Review implements Serializable {
    public String id, productId, shopId, customerId, customerName, comment;
    public double rating;
    public long createdAt;

    public Review() {}
}
