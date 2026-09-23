package com.bazario.app.model;

import com.bazario.app.R;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    public static List<Product> all() {
        List<Product> p = new ArrayList<>();
        p.add(new Product(1,"Black Gram Lentils","Dals & Pulses",20.00,35.00,R.drawable.black_gram,"Quality black gram lentils for everyday cooking."));
        p.add(new Product(2,"Brown Lentils Sabut Masoor","Dals & Pulses",19.99,30.00,R.drawable.masoor_dal,"Nutritious brown lentils with rich natural flavor."));
        p.add(new Product(3,"Dry Fruits Mixed","Dry Fruit",20.00,35.00,R.drawable.dry_fruits,"A convenient mix of premium dry fruits."));
        p.add(new Product(4,"Steel Cooking Pot","Cookware",20.00,35.00,R.drawable.cooking_pot,"Durable cookware for daily kitchen use."));
        p.add(new Product(5,"Small Plastic Dustpan","Household",20.00,35.00,R.drawable.dustpan,"Lightweight dustpan for quick cleaning."));
        p.add(new Product(6,"Baby Care Accessories","Baby Care",20.00,35.00,R.drawable.baby_care,"Useful baby-care accessories for everyday needs."));
        p.add(new Product(7,"Green Tea","Beverages",19.99,29.99,R.drawable.green_tea,"Refreshing green tea for a simple daily routine."));
        p.add(new Product(8,"Grocery Essentials","Groceries",15.00,22.00,R.drawable.grocery,"Popular grocery essentials from Bazario."));
        return p;
    }
    public static Product byId(int id) { for(Product p: all()) if(p.id==id) return p; return all().get(0); }
}
