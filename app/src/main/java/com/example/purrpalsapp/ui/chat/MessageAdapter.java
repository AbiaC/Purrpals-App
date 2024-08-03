package com.example.purrpalsapp.ui.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.purrpalsapp.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<MessageModal> mMessages;
    private Context mContext;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public MessageAdapter(List<MessageModal> messages, Context context) {
        mMessages = messages;
        mContext = context;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessageModal message = mMessages.get(position);

        if (message.getSenderEmail().equals(user.getEmail())) {
            holder.other.setVisibility(View.GONE);
            holder.user.setVisibility(View.VISIBLE);
            holder.textViewUserTime.setText(getTime(message.getTime()));
            holder.textViewUserMessage.setText(message.getText());
        } else {
            holder.other.setVisibility(View.VISIBLE);
            holder.user.setVisibility(View.GONE);
            holder.textViewAdminTime.setText(getTime(message.getTime()));
            holder.textViewAdmin.setText(message.getText());
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewUserMessage, textViewUserTime, textViewAdmin, textViewAdminTime;
        public CardView user, other;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserMessage = itemView.findViewById(R.id.userTextViewMessage);
            textViewUserTime = itemView.findViewById(R.id.userTextViewTime);
            textViewAdmin = itemView.findViewById(R.id.adminTextViewMsg);
            textViewAdminTime = itemView.findViewById(R.id.adminTimeMsg);
            user = itemView.findViewById(R.id.userReply);
            other = itemView.findViewById(R.id.admin_reply);
        }
    }

    private String getTime(long time) {
        // Convert timestamp to a readable time format
        Date date = new Date(time * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.US);
        return sdf.format(date);
    }
}

