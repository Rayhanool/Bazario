package com.bazario.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bazario.app.model.Product;
import com.bazario.app.model.ProductRepository;

public class ProductDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        int productId = getIntent().getIntExtra("id", 1);
        Product product = ProductRepository.byId(productId);

        ImageView imgView = findViewById(R.id.detailImage);
        TextView name = findViewById(R.id.detailName);
        TextView shopNameView = findViewById(R.id.detailShopName);
        TextView category = findViewById(R.id.detailCategory);
        TextView price = findViewById(R.id.detailPrice);
        TextView description = findViewById(R.id.detailDescription);
        Button btnAddToCart = findViewById(R.id.addToCart);
        Button btnReviews = findViewById(R.id.btnReviews);

        imgView.setImageResource(product.imageRes);
        name.setText(product.name);
        String shopName = getIntent().getStringExtra("shopName");
        if (shopName == null || shopName.isEmpty()) {
            shopName = "Bazario Official Store";
        }
        shopNameView.setText("Sold by: " + shopName);
        category.setText(product.category);
        price.setText(String.format("$%.2f", product.price));
        description.setText(product.description);

        btnReviews.setOnClickListener(v -> {
            ReviewDialog.show(this, String.valueOf(product.id), "default_shop", product.name);
        });

        btnAddToCart.setOnClickListener(v -> {
            if (!Session.isLoggedIn(this)) {
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }
            int currentQty = Session.qty(this, product.id);
            Session.setQty(this, product.id, currentQty + 1);
            Toast.makeText(this, "Added to cart (" + (currentQty + 1) + ")", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.back).setOnClickListener(v -> finish());
    }
}

