package com.example.brewmate.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.brewmate.R;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ViewHolder> {

    // Inner model class to hold transaction data
    public static class Transaction {
        String id;
        String items;
        String time;
        String cashier;
        double amount;

        public Transaction(String id, String items, String time, String cashier, double amount) {
            this.id = id;
            this.items = items;
            this.time = time;
            this.cashier = cashier;
            this.amount = amount;
        }
    }

    private List<Transaction> transactions;

    // Constructor
    public ReportsAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public ReportsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate a simple layout for each transaction card
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportsAdapter.ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);

        holder.txtId.setText(transaction.id);
        holder.txtItems.setText(transaction.items);
        holder.txtTime.setText(transaction.time + " by " + transaction.cashier);
        holder.txtAmount.setText("$" + String.format("%.2f", transaction.amount));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtId, txtItems, txtTime, txtAmount;
        CardView cardContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtId = itemView.findViewById(R.id.txtTransactionId);
            txtItems = itemView.findViewById(R.id.txtTransactionItems);
            txtTime = itemView.findViewById(R.id.txtTransactionTime);
            txtAmount = itemView.findViewById(R.id.txtTransactionAmount);
            cardContainer = itemView.findViewById(R.id.cardTransaction);
        }
    }
}
