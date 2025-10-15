package com.example.brewmate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.brewmate.R;
import com.example.brewmate.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final Context context;
    private final List<User> userList;
    private final OnUserDeleteListener deleteListener;

    // 👇 Add a listener interface
    public interface OnUserDeleteListener {
        void onUserDelete(User user);
    }

    public UserAdapter(Context context, List<User> userList, OnUserDeleteListener deleteListener) {
        this.context = context;
        this.userList = userList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.tvFullName.setText(user.getFullName());
        holder.tvUsername.setText("@" + user.getUsername());
        holder.tvEmail.setText(user.getEmail());
        holder.tvCreatedAt.setText("Created " + user.getCreatedAt());

        holder.editFrame.setOnClickListener(v ->
                Toast.makeText(context, "Edit " + user.getFullName(), Toast.LENGTH_SHORT).show());

        holder.deleteFrame.setOnClickListener(v ->
//                Toast.makeText(context, "Delete " + user.getFullName(), Toast.LENGTH_SHORT).show()
                {
                    if (deleteListener != null) {
                        deleteListener.onUserDelete(user);
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvFullName, tvUsername, tvEmail, tvCreatedAt;
        FrameLayout editFrame, deleteFrame;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tvFullName);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            editFrame = itemView.findViewById(R.id.edit_frame);
            deleteFrame = itemView.findViewById(R.id.delete_frame);
        }
    }
}
