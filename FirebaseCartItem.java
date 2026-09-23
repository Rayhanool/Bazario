package com.bazario.app.model;

import org.json.JSONObject;

public class FirebaseCartItem {
    public String productId, shopId, shopName, name, category, description, area, unit;
    public double price, oldPrice, rating;
    public long stock, reviewCount, quantity;

    public FirebaseCartItem() {}

    public static FirebaseCartItem fromProduct(FirebaseProduct p, long quantity){
        FirebaseCartItem i=new FirebaseCartItem();
        i.productId=p.id; i.shopId=p.shopId; i.shopName=p.shopName; i.name=p.name;
        i.category=p.category; i.description=p.description; i.area=p.area; i.unit=ProductUnit.normalize(p.unit);
        i.price=p.price; i.oldPrice=p.oldPrice; i.rating=p.rating;
        i.stock=p.stock; i.reviewCount=p.reviewCount; i.quantity=quantity;
        return i;
    }

    public JSONObject toJson() throws Exception{
        JSONObject o=new JSONObject();
        o.put("productId",productId); o.put("shopId",shopId); o.put("shopName",shopName);
        o.put("name",name); o.put("category",category); o.put("description",description); o.put("area",area); o.put("unit",unit);
        o.put("price",price); o.put("oldPrice",oldPrice); o.put("rating",rating);
        o.put("stock",stock); o.put("reviewCount",reviewCount); o.put("quantity",quantity);
        return o;
    }

    public static FirebaseCartItem fromJson(JSONObject o) throws Exception{
        FirebaseCartItem i=new FirebaseCartItem();
        i.productId=o.optString("productId",""); i.shopId=o.optString("shopId",""); i.shopName=o.optString("shopName","");
        i.name=o.optString("name","Product"); i.category=o.optString("category",""); i.description=o.optString("description","");
        i.area=o.optString("area",""); i.unit=ProductUnit.normalize(o.optString("unit",ProductUnit.PIECE));
        i.price=o.optDouble("price",0); i.oldPrice=o.optDouble("oldPrice",0);
        i.rating=o.optDouble("rating",0); i.stock=o.optLong("stock",0); i.reviewCount=o.optLong("reviewCount",0);
        i.quantity=o.optLong("quantity",1);
        return i;
    }
}
