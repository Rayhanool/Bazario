package com.bazario.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bazario.app.adapter.ProductAdapter;
import com.bazario.app.model.Product;
import com.bazario.app.model.ProductRepository;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView productsRecycler;
    private EditText searchBox;
    private TextView cartBadge;
    private TextView greeting, locationLabel;
    private View loginButton;
    private ProductAdapter adapter;
    private List<Product> allProducts;
    private String activeCategory = null;

    private MaterialButton btnGroceries, btnDals, btnHouse, btnBaby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        allProducts = ProductRepository.all();

        productsRecycler = findViewById(R.id.productsRecycler);
        searchBox = findViewById(R.id.searchBox);
        cartBadge = findViewById(R.id.cartBadge);
        greeting = findViewById(R.id.greeting);
        locationLabel = findViewById(R.id.locationLabel);
        loginButton = findViewById(R.id.loginButton);

        btnGroceries = findViewById(R.id.catGroceries);
        btnDals = findViewById(R.id.catDals);
        btnHouse = findViewById(R.id.catHouse);
        btnBaby = findViewById(R.id.catBaby);

        productsRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        showProducts(allProducts);
        refreshHeader();

        loginButton.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
        locationLabel.setOnClickListener(v -> showLocationDialog());
        findViewById(R.id.nearbyButton).setOnClickListener(v -> {
            Intent i = new Intent(this, NearbySearchActivity.class);
            i.putExtra("query", searchBox.getText().toString().trim());
            startActivity(i);
        });
        findViewById(R.id.cartButton).setOnClickListener(v -> requireLoginThen(CartActivity.class));
        findViewById(R.id.profileButton).setOnClickListener(v -> requireLoginThen(ProfileActivity.class));

        // Bottom Navigation
        findViewById(R.id.navHome).setOnClickListener(v -> resetHomeFeed());
        findViewById(R.id.logoCard).setOnClickListener(v -> resetHomeFeed());
        findViewById(R.id.ordersButton).setOnClickListener(v -> requireLoginThen(OrdersActivity.class));
        findViewById(R.id.profileButtonBottom).setOnClickListener(v -> requireLoginThen(ProfileActivity.class));
        findViewById(R.id.offersButton).setOnClickListener(v -> 
            Toast.makeText(this, "Special offer: Get 10% off with code BAZARIO10", Toast.LENGTH_LONG).show()
        );

        // Search Filter
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase().trim();
                List<Product> filtered = new ArrayList<>();
                for (Product p : allProducts) {
                    if (p.name.toLowerCase().contains(query) || p.category.toLowerCase().contains(query)) {
                        filtered.add(p);
                    }
                }
                showProducts(filtered);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Category Chips
        btnGroceries.setOnClickListener(v -> toggleCategory("Groceries", btnGroceries));
        btnDals.setOnClickListener(v -> toggleCategory("Dals", btnDals));
        btnHouse.setOnClickListener(v -> toggleCategory("Household", btnHouse));
        btnBaby.setOnClickListener(v -> toggleCategory("Baby", btnBaby));
    }

    private void toggleCategory(String category, MaterialButton selectedBtn) {
        if (category.equals(activeCategory)) {
            activeCategory = null;
            resetChipStyles();
            showProducts(allProducts);
        } else {
            activeCategory = category;
            resetChipStyles();
            selectedBtn.setBackgroundColor(getColor(R.color.bazario_blue));
            selectedBtn.setTextColor(getColor(R.color.white));
            selectedBtn.setIconTintResource(R.color.white);

            List<Product> filtered = new ArrayList<>();
            for (Product p : allProducts) {
                if (p.category.toLowerCase().contains(category.toLowerCase())) {
                    filtered.add(p);
                }
            }
            showProducts(filtered);
        }
    }

    private void resetChipStyles() {
        MaterialButton[] buttons = {btnGroceries, btnDals, btnHouse, btnBaby};
        for (MaterialButton btn : buttons) {
            btn.setBackgroundColor(getColor(R.color.bazario_blue_subtle));
            btn.setTextColor(getColor(R.color.bazario_navy));
            btn.setIconTintResource(R.color.bazario_blue);
        }
    }

    private void showProducts(List<Product> list) {
        adapter = new ProductAdapter(list, p -> 
            startActivity(new Intent(this, ProductDetailsActivity.class).putExtra("id", p.id))
        );
        productsRecycler.setAdapter(adapter);
    }

    private void refreshHeader() {
        int count = Session.cartCount(this);
        cartBadge.setText(String.valueOf(count));
        cartBadge.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        
        if (Session.isLoggedIn(this)) {
            greeting.setText("Hi, " + Session.name(this));
            loginButton.setVisibility(View.GONE);
        } else {
            greeting.setText("Welcome to Bazario");
            loginButton.setVisibility(View.VISIBLE);
        }
        String area = Session.area(this);
        locationLabel.setText(area.isEmpty() ? "📍 Select Area ▾" : "📍 " + area + " ▾");
    }

    private void requireLoginThen(Class<?> targetActivity) {
        if (Session.isLoggedIn(this)) {
            startActivity(new Intent(this, targetActivity));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private void showLocationDialog() {
        String[] areas = {"Gazipur", "Dhaka", "Uttara", "Mirpur", "Gulshan", "Banani", "Dhanmondi", "Other / Custom Area..."};
        String currentArea = Session.area(this);
        int selectedIndex = 0;
        for (int i = 0; i < areas.length - 1; i++) {
            if (areas[i].equalsIgnoreCase(currentArea)) {
                selectedIndex = i;
                break;
            }
        }

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Select your delivery area")
                .setSingleChoiceItems(areas, selectedIndex, (dialog, which) -> {
                    if (which == areas.length - 1) {
                        dialog.dismiss();
                        showCustomAreaPrompt();
                    } else {
                        Session.setArea(this, areas[which]);
                        refreshHeader();
                        dialog.dismiss();
                        Toast.makeText(this, "Area set to " + areas[which], Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showCustomAreaPrompt() {
        final EditText input = new EditText(this);
        input.setHint("Enter area name (e.g. Sylhet)");
        input.setText(Session.area(this));
        input.setSingleLine(true);
        int pad = (int)(24 * getResources().getDisplayMetrics().density);
        input.setPadding(pad, 12, pad, 12);
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Custom Delivery Area")
                .setView(input)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Set", (d, w) -> {
                    String a = input.getText().toString().trim();
                    if (!a.isEmpty()) {
                        Session.setArea(this, a);
                        refreshHeader();
                        Toast.makeText(this, "Area set to " + a, Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    private void resetHomeFeed() {
        activeCategory = null;
        resetChipStyles();
        searchBox.setText("");
        showProducts(allProducts);
        androidx.core.widget.NestedScrollView scrollView = findViewById(R.id.mainScrollView);
        if (scrollView != null) {
            scrollView.smoothScrollTo(0, 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshHeader();
    }
}
