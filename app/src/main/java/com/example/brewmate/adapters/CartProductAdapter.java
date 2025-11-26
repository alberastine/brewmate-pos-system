package com.example.brewmate.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.brewmate.R;
import com.example.brewmate.models.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartProductAdapter extends RecyclerView.Adapter<CartProductAdapter.CartViewHolder> {

    private Context context;
    private List<Product> cartList;
    private SharedPreferences prefs;
    private Gson gson = new Gson();
    private static final String PREF_NAME = "ProductPrefs";
    private static final String KEY_CART = "cart";

    public CartProductAdapter(Context context, List<Product> cartList) {
        this.context = context;
        this.cartList = cartList;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart_product, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        Product product = cartList.get(position);

        holder.tvCartProductName.setText(product.getName());
        holder.tvEachPrice.setText(String.format("₱%.2f each", product.getPrice()));
        holder.tvCartQuantity.setText(String.valueOf(product.getQuantity()));
        updateTotal(holder, product);

        holder.btnPlus.setOnClickListener(v -> {
            product.setQuantity(product.getQuantity() + 1);
            updateTotal(holder, product);
            saveCart();
        });

        holder.btnMinus.setOnClickListener(v -> {
            if (product.getQuantity() > 1) {
                product.setQuantity(product.getQuantity() - 1);
            } else {
                cartList.remove(product);
                notifyDataSetChanged();
            }
            updateTotal(holder, product);
            saveCart();
        });
    }

    private void updateTotal(CartViewHolder holder, Product product) {
        holder.tvTotalSummary.setText(String.format("%d × ₱%.2f", product.getQuantity(), product.getPrice()));
        holder.tvTotalPrice.setText(String.format("₱%.2f", product.getQuantity() * product.getPrice()));
        holder.tvCartQuantity.setText(String.valueOf(product.getQuantity()));
    }

    private void saveCart() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_CART, gson.toJson(cartList));
        editor.apply();
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvCartProductName, tvEachPrice, tvCartQuantity, btnPlus, btnMinus, tvTotalSummary, tvTotalPrice;

        public CartViewHolder(View itemView) {
            super(itemView);
            tvCartProductName = itemView.findViewById(R.id.tvCartProductName);
            tvEachPrice = itemView.findViewById(R.id.tvEachPrice);
            tvCartQuantity = itemView.findViewById(R.id.tvCartQuantity);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            tvTotalSummary = itemView.findViewById(R.id.tvTotalSummary);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
        }
    }
}
