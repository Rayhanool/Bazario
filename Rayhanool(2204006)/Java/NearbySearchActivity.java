package com.bazario.app;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bazario.app.model.FirebaseProduct;
import com.bazario.app.model.ProductUnit;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Locale;

public class NearbySearchActivity extends AppCompatActivity {

    private LinearLayout results;
    private EditText query;
    private EditText area;


    // ============================================================
    // COLORS
    // ============================================================

    private static final int BLUE =
            Color.parseColor("#1689D7");

    private static final int GREEN =
            Color.parseColor("#16A34A");

    private static final int DARK =
            Color.parseColor("#202124");

    private static final int GRAY =
            Color.parseColor("#4B5563");

    private static final int BORDER =
            Color.parseColor("#E5E7EB");


    @Override
    protected void onCreate(Bundle b) {

        super.onCreate(b);

        setContentView(
                R.layout.activity_nearby_search
        );


        query = findViewById(
                R.id.query
        );

        area = findViewById(
                R.id.area
        );

        results = findViewById(
                R.id.results
        );


        area.setText(
                Session.area(this)
        );


        findViewById(R.id.back)
                .setOnClickListener(
                        v -> finish()
                );


        findViewById(R.id.search)
                .setOnClickListener(
                        v -> search()
                );


        query.setText(
                getIntent()
                        .getStringExtra("query")
        );


        if (query.getText().length() > 0) {

            search();
        }
    }


    // ============================================================
    // SEARCH PRODUCTS
    // ============================================================

    private void search() {

        String q =
                query.getText()
                        .toString()
                        .trim();

        String a =
                area.getText()
                        .toString()
                        .trim();


        if (q.isEmpty() || a.isEmpty()) {

            toast(
                    "Enter product and area"
            );

            return;
        }


        Session.setArea(
                this,
                a
        );


        results.removeAllViews();


        if (!FirebaseUtil.isConfigured(this)) {

            add(
                    "Firebase is not configured. " +
                    "Add google-services.json and create Firestore data."
            );

            return;
        }


        FirebaseUtil.db(this)
                .collection("products")
                .whereEqualTo(
                        "area",
                        a
                )
                .get()

                .addOnSuccessListener(
                        snap -> {

                            int count = 0;


                            for (
                                    DocumentSnapshot d :
                                    snap
                            ) {

                                String n =
                                        d.getString(
                                                "name"
                                        );


                                Boolean av =
                                        d.getBoolean(
                                                "available"
                                        );


                                long st =
                                        stockValue(d);


                                /*
                                 * Detect product unit.
                                 *
                                 * Rice / Alu / Flour
                                 *        -> kg
                                 *
                                 * Oil
                                 *        -> bottle
                                 *
                                 * Biscuit / Chips
                                 *        -> packet
                                 *
                                 * Other products
                                 *        -> piece
                                 */

                                String unit =
                                        ProductUnit.detectLegacy(
                                                d.getString(
                                                        "unit"
                                                ),
                                                n,
                                                d.getString(
                                                        "category"
                                                )
                                        );


                                boolean available =
                                        Boolean.TRUE.equals(av)
                                                ||
                                        (
                                                d.getBoolean(
                                                        "available"
                                                ) == null
                                                        &&
                                                st > 0
                                        );


                                if (
                                        n != null
                                                &&
                                        n.toLowerCase(
                                                        Locale.US
                                                )
                                                .contains(
                                                        q.toLowerCase(
                                                                Locale.US
                                                        )
                                                )
                                                &&
                                        available
                                                &&
                                        st > 0
                                ) {

                                    addCard(
                                            toProduct(d)
                                    );

                                    count++;
                                }
                            }


                            if (count == 0) {

                                add(
                                        "No available seller found for \"" +
                                        q +
                                        "\" in " +
                                        a +
                                        "."
                                );
                            }

                        }
                )

                .addOnFailureListener(
                        e ->
                                add(
                                        "Search failed: " +
                                        e.getMessage()
                                )
                );
    }


    // ============================================================
    // FIREBASE DOCUMENT -> PRODUCT
    // ============================================================

    private FirebaseProduct toProduct(
            DocumentSnapshot d
    ) {

        FirebaseProduct p =
                new FirebaseProduct();


        p.id =
                d.getId();


        p.shopId =
                val(
                        d,
                        "shopId",
                        val(
                                d,
                                "ownerId",
                                ""
                        )
                );


        p.shopName =
                val(
                        d,
                        "shopName",
                        "Seller"
                );


        p.name =
                val(
                        d,
                        "name",
                        "Product"
                );


        p.category =
                val(
                        d,
                        "category",
                        ""
                );


        p.description =
                val(
                        d,
                        "description",
                        ""
                );


        p.area =
                val(
                        d,
                        "area",
                        ""
                );


        p.price =
                num(
                        d,
                        "price",
                        0
                );


        p.oldPrice =
                num(
                        d,
                        "oldPrice",
                        0
                );


        p.rating =
                num(
                        d,
                        "rating",
                        0
                );


        p.stock =
                stockValue(d);


        p.reviewCount =
                longVal(
                        d,
                        "reviewCount",
                        0
                );


        p.unit =
                ProductUnit.detectLegacy(
                        d.getString(
                                "unit"
                        ),
                        p.name,
                        p.category
                );


        p.available =
                Boolean.TRUE.equals(
                        d.getBoolean(
                                "available"
                        )
                )
                        ||
                p.stock > 0;


        return p;
    }


    // ============================================================
    // PRODUCT CARD
    // ============================================================

    private void addCard(
            FirebaseProduct p
    ) {

        // ========================================================
        // MAIN CARD
        // ========================================================

        LinearLayout card =
                new LinearLayout(this);


        card.setOrientation(
                LinearLayout.VERTICAL
        );


        card.setPadding(
                18,
                18,
                18,
                18
        );


        GradientDrawable cardBackground =
                new GradientDrawable();


        cardBackground.setColor(
                Color.WHITE
        );


        cardBackground.setCornerRadius(
                22
        );


        cardBackground.setStroke(
                1,
                BORDER
        );


        card.setBackground(
                cardBackground
        );


        card.setElevation(
                4
        );


        // ========================================================
        // PRODUCT TITLE
        // ========================================================

        TextView title =
                new TextView(this);


        title.setText(
                p.name
                        + "  •  "
                        + String.format(
                                Locale.US,
                                "%.2f BDT / %s",
                                p.price,
                                p.unitLabel()
                        )
        );


        title.setTextSize(
                21
        );


        title.setTextColor(
                DARK
        );


        title.setTypeface(
                null,
                Typeface.BOLD
        );


        title.setMaxLines(
                2
        );


        card.addView(
                title
        );


        // ========================================================
        // PRODUCT INFORMATION
        // ========================================================

        TextView info =
                new TextView(this);


        info.setText(
                "Shop: " +
                p.shopName +

                "\nArea: " +
                p.area +

                "\nStock: " +
                ProductUnit.quantityText(
                        p.stock,
                        p.unit
                ) +

                "\nRating: " +
                String.format(
                        Locale.US,
                        "%.1f",
                        p.rating
                ) +

                "\n" +
                p.description
        );


        info.setTextSize(
                18
        );


        info.setTextColor(
                GRAY
        );


        info.setLineSpacing(
                2,
                1.05f
        );


        LinearLayout.LayoutParams infoParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );


        infoParams.setMargins(
                0,
                8,
                0,
                0
        );


        card.addView(
                info,
                infoParams
        );


        // ========================================================
        // BUTTON ROW
        // ========================================================

        LinearLayout actions =
                new LinearLayout(this);


        actions.setOrientation(
                LinearLayout.HORIZONTAL
        );


        actions.setGravity(
                Gravity.CENTER_VERTICAL
        );


        float density = getResources().getDisplayMetrics().density;
        int buttonHeight = (int) (48 * density);

        LinearLayout.LayoutParams actionsParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );


        actionsParams.setMargins(
                0,
                18,
                0,
                0
        );


        // ========================================================
        // ADD TO CART BUTTON
        // ========================================================

        MaterialButton addCart =
                createActionButton(
                        "ADD TO CART",
                        BLUE,
                        Color.WHITE
                );


        // ========================================================
        // BUY NOW BUTTON
        // ========================================================

        MaterialButton buyNow =
                createActionButton(
                        "BUY NOW",
                        GREEN,
                        Color.WHITE
                );


        // ========================================================
        // REVIEWS BUTTON
        // ========================================================

        MaterialButton reviews =
                createActionButton(
                        "REVIEWS",
                        Color.WHITE,
                        Color.parseColor(
                                "#374151"
                        )
                );


        reviews.setStrokeWidth(
                2
        );


        reviews.setStrokeColor(
                ColorStateList.valueOf(
                        Color.parseColor(
                                "#D1D5DB"
                        )
                )
        );


        // ========================================================
        // BUTTON WIDTH
        // ========================================================

        /*
         * ADD TO CART gets a little more width because
         * its text is longer.
         */

        LinearLayout.LayoutParams addCartParams =
                new LinearLayout.LayoutParams(
                        0,
                        buttonHeight,
                        1.20f
                );


        addCartParams.setMargins(
                2,
                0,
                3,
                0
        );


        LinearLayout.LayoutParams buyNowParams =
                new LinearLayout.LayoutParams(
                        0,
                        buttonHeight,
                        0.90f
                );


        buyNowParams.setMargins(
                3,
                0,
                3,
                0
        );


        LinearLayout.LayoutParams reviewParams =
                new LinearLayout.LayoutParams(
                        0,
                        buttonHeight,
                        0.90f
                );


        reviewParams.setMargins(
                3,
                0,
                2,
                0
        );


        actions.addView(
                addCart,
                addCartParams
        );


        actions.addView(
                buyNow,
                buyNowParams
        );


        actions.addView(
                reviews,
                reviewParams
        );


        card.addView(
                actions,
                actionsParams
        );


        // ========================================================
        // CARD MARGIN
        // ========================================================

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );


        cardParams.setMargins(
                0,
                0,
                0,
                16
        );


        results.addView(
                card,
                cardParams
        );


        // ========================================================
        // BUTTON CLICK ACTIONS
        // ========================================================

        addCart.setOnClickListener(
                v ->
                        addToCart(
                                p,
                                false
                        )
        );


        buyNow.setOnClickListener(
                v ->
                        addToCart(
                                p,
                                true
                        )
        );


        reviews.setOnClickListener(
                v ->
                        ReviewDialog.show(
                                this,
                                p.id,
                                p.shopId,
                                p.name
                        )
        );
    }


    // ============================================================
    // CREATE BUTTON
    // ============================================================

    private MaterialButton createActionButton(
            String text,
            int backgroundColor,
            int textColor
    ) {

        MaterialButton button =
                new MaterialButton(this);


        button.setText(
                text
        );


        /*
         * VERY IMPORTANT
         *
         * Keep button text in one line.
         * This prevents:
         *
         * ADD TO
         * CART
         *
         * from happening.
         */

        button.setSingleLine(
                true
        );


        button.setMaxLines(
                1
        );


        button.setTextSize(
                12
        );


        button.setTextColor(
                textColor
        );


        button.setTypeface(
                null,
                Typeface.BOLD
        );


        button.setGravity(
                Gravity.CENTER
        );


        /*
         * Remove default MaterialButton
         * padding/insets.
         */

        float density = getResources().getDisplayMetrics().density;
        int padH = (int) (8 * density);
        int padV = (int) (10 * density);
        button.setPadding(
                padH,
                padV,
                padH,
                padV
        );


        button.setInsetTop(
                0
        );


        button.setInsetBottom(
                0
        );


        button.setMinHeight(
                0
        );


        button.setMinimumHeight(
                0
        );


        button.setMinWidth(
                0
        );


        button.setMinimumWidth(
                0
        );


        button.setCornerRadius(
                (int) (12 * density)
        );


        button.setBackgroundTintList(
                ColorStateList.valueOf(
                        backgroundColor
                )
        );


        button.setRippleColor(
                ColorStateList.valueOf(
                        Color.parseColor(
                                "#33000000"
                        )
                )
        );


        return button;
    }


    // ============================================================
    // ADD TO CART / BUY NOW
    // ============================================================

    private void addToCart(
            FirebaseProduct p,
            boolean buyNow
    ) {

        Session.addFirebaseCartItem(
                this,
                p
        );


        Toast.makeText(
                this,
                p.name +
                        " added to cart",
                Toast.LENGTH_SHORT
        ).show();


        if (buyNow) {

            startActivity(
                    new Intent(
                            this,
                            CartActivity.class
                    )
            );
        }
    }


    // ============================================================
    // FIREBASE STRING
    // ============================================================

    private String val(
            DocumentSnapshot d,
            String key,
            String def
    ) {

        String x =
                d.getString(
                        key
                );


        return x == null
                ? def
                : x;
    }


    // ============================================================
    // FIREBASE NUMBER
    // ============================================================

    private double num(
            DocumentSnapshot d,
            String key,
            double def
    ) {

        Double x =
                d.getDouble(
                        key
                );


        return x == null
                ? def
                : x;
    }


    // ============================================================
    // FIREBASE LONG
    // ============================================================

    private long longVal(
            DocumentSnapshot d,
            String key,
            long def
    ) {

        Long x =
                d.getLong(
                        key
                );


        return x == null
                ? def
                : x;
    }


    // ============================================================
    // STOCK VALUE
    // ============================================================

    private long stockValue(
            DocumentSnapshot d
    ) {

        /*
         * New Firebase format:
         *
         * stock: 500
         */

        Long n =
                d.getLong(
                        "stock"
                );


        if (n != null) {

            return Math.max(
                    0,
                    n
            );
        }


        /*
         * Old Firebase format:
         *
         * stock: "500 kg"
         */

        String raw =
                d.getString(
                        "stock"
                );


        if (raw == null) {

            return 0;
        }


        StringBuilder digits =
                new StringBuilder();


        for (
                int i = 0;
                i < raw.length();
                i++
        ) {

            char ch =
                    raw.charAt(i);


            if (
                    Character.isDigit(ch)
            ) {

                digits.append(
                        ch
                );
            }
        }


        try {

            return digits.length() == 0
                    ? 0
                    : Long.parseLong(
                            digits.toString()
                    );

        } catch (Exception e) {

            return 0;
        }
    }


    // ============================================================
    // EMPTY / ERROR MESSAGE
    // ============================================================

    private void add(
            String message
    ) {

        TextView text =
                new TextView(this);


        text.setText(
                message
        );


        text.setTextSize(
                16
        );


        text.setTextColor(
                GRAY
        );


        text.setPadding(
                20,
                30,
                20,
                30
        );


        results.addView(
                text
        );
    }


    // ============================================================
    // TOAST
    // ============================================================

    private void toast(
            String message
    ) {

        Toast.makeText(
                this,
                message,
                Toast.LENGTH_LONG
        ).show();
    }
}