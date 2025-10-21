package com.example.brewmate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.brewmate.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TopProductsAdapter extends RecyclerView.Adapter<TopProductsAdapter.ViewHolder> {

    private List<String> products;

    public TopProductsAdapter(List<String> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public TopProductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_top_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopProductsAdapter.ViewHolder holder, int position) {
        String product = products.get(position);
        holder.txtProductName.setText(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.txtProductName);
        }
    }
}
