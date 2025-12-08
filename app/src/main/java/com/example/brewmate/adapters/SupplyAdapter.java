package com.example.brewmate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.brewmate.R;
import com.example.brewmate.models.Supply;
import java.util.List;

public class SupplyAdapter extends RecyclerView.Adapter<SupplyAdapter.SupplyViewHolder> {

    private List<Supply> supplyList;
    private OnSupplyActionListener listener;

    public interface OnSupplyActionListener {
        void onDelete(Supply supply);
        void onEdit(Supply supply);
    }

    public SupplyAdapter(List<Supply> supplyList, OnSupplyActionListener listener) {
        this.supplyList = supplyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SupplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_supplies, parent, false);
        return new SupplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SupplyViewHolder holder, int position) {
        Supply supply = supplyList.get(position);
        holder.tvItemName.setText(supply.getSupplyName());
        holder.tvSupplier.setText("Supplier: " + supply.getSupplierName());
        holder.tvQuantity.setText(supply.getQuantity());

        double qtyVal = parseDoubleSafe(supply.getQuantity());
        double threshold = supply.getLowStockThreshold();
        if (threshold > 0 && qtyVal <= threshold) {
            holder.tvQuantity.setText(supply.getQuantity() + "  • " + holder.itemView.getContext().getString(R.string.low_stock_label));
            holder.tvQuantity.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.red_600));
        } else {
            holder.tvQuantity.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.black));
        }

        holder.btnDelete.setOnClickListener(v -> listener.onDelete(supply));
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(supply));
    }

    @Override
    public int getItemCount() {
        return supplyList.size();
    }

    public void updateList(List<Supply> newList) {
        this.supplyList = newList;
        notifyDataSetChanged();
    }

    static class SupplyViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvSupplier, tvQuantity;
        ImageButton btnDelete, btnEdit;

        public SupplyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvSupplier = itemView.findViewById(R.id.tvSupplier);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnDelete = itemView.findViewById(R.id.btnDeleteSupply);
            btnEdit = itemView.findViewById(R.id.btnEditSupply);
        }
    }

    private double parseDoubleSafe(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}