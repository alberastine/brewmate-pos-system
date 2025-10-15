package com.example.brewmate.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.brewmate.R;
import com.example.brewmate.models.History;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<History> historyList;

    public HistoryAdapter(List<History> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        History history = historyList.get(position);
        holder.tvAction.setText(history.getAction());
        holder.tvDescription.setText(history.getDescription());
        holder.tvTimeAgo.setText(formatTimeAgo(history.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void updateData(List<History> newHistoryList) {
        this.historyList = newHistoryList;
        notifyDataSetChanged();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvAction, tvDescription, tvTimeAgo;
        HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAction = itemView.findViewById(R.id.tvAction);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
        }
    }

    private String formatTimeAgo(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;

        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        long hours = TimeUnit.MILLISECONDS.toHours(diff);
        long days = TimeUnit.MILLISECONDS.toDays(diff);

        if (minutes < 1) return "just now";
        if (minutes < 60) return minutes + " min ago";
        if (hours < 24) return hours + " h ago";
        return days + " d ago";
    }
}
