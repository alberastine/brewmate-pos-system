package com.example.brewmate.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.brewmate.R;
import com.example.brewmate.models.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MenuProductAdapter extends RecyclerView.Adapter<MenuProductAdapter.MenuProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private SharedPreferences prefs;
    private Gson gson = new Gson();
    private static final String PREF_NAME = "ProductPrefs";
    private static final String KEY_CART = "cart";

    public MenuProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public MenuProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu_product, parent, false);
        return new MenuProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MenuProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvProductName.setText(product.getName());
        holder.tvPrice.setText(String.format("₱%.2f", product.getPrice()));

        holder.tvQuantity.setText("0");
        holder.addToCart.setEnabled(false);
        holder.addToCart.setAlpha(0.5f);

        holder.btnIncrease.setOnClickListener(v -> {
            int qty = Integer.parseInt(holder.tvQuantity.getText().toString()) + 1;
            holder.tvQuantity.setText(String.valueOf(qty));
            updateButtonState(holder, qty);
        });

        holder.btnDecrease.setOnClickListener(v -> {
            int qty = Integer.parseInt(holder.tvQuantity.getText().toString());
            if (qty > 0) qty--;
            holder.tvQuantity.setText(String.valueOf(qty));
            updateButtonState(holder, qty);
        });

        holder.addToCart.setOnClickListener(v -> {
            int qty = Integer.parseInt(holder.tvQuantity.getText().toString());
            if (qty > 0) {
                addToCart(product, qty);
                holder.tvQuantity.setText("0");
                updateButtonState(holder, 0);
            }
        });
    }

    private void updateButtonState(MenuProductViewHolder holder, int qty) {
        if (qty > 0) {
            holder.addToCart.setEnabled(true);
            holder.addToCart.setAlpha(1f);
        } else {
            holder.addToCart.setEnabled(false);
            holder.addToCart.setAlpha(0.5f);
        }
    }

    private void addToCart(Product product, int quantity) {
        List<Product> cart = loadCart();
        boolean exists = false;

        for (Product p : cart) {
            if (p.getName().equals(product.getName())) {
                p.setQuantity(p.getQuantity() + quantity);
                exists = true;
                break;
            }
        }

        if (!exists) {
            Product newItem = new Product(product.getId(), product.getName(), product.getPrice(), product.getCategory());
            newItem.setQuantity(quantity);
            cart.add(newItem);
        }

        saveCart(cart);
    }

    private List<Product> loadCart() {
        String json = prefs.getString(KEY_CART, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<Product>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private void saveCart(List<Product> cart) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_CART, gson.toJson(cart));
        editor.apply();
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class MenuProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvPrice, btnIncrease, btnDecrease, tvQuantity;
        CardView addToCart;

        public MenuProductViewHolder(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            addToCart = itemView.findViewById(R.id.add_to_cart);
        }
    }
}
