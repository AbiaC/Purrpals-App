package com.example.purrpalsapp.ui.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.purrpalsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {
    private DatabaseReference mDatabase;
    private List<MessageModal> mMessages;
    private MessageAdapter mAdapter;
    private FirebaseUser user;
    private ProgressBar progressBar;
    private String chatId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mMessages = new ArrayList<>();
        mAdapter = new MessageAdapter(mMessages, getContext());


        Bundle bundle = getArguments();
        if (bundle!= null) {
            chatId = bundle.getString("payId");
            loadMessages();
        } else {
            Log.e("Error", "bundle is null");
        }
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewBalance);
        progressBar = view.findViewById(R.id.progressMain);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ImageButton buttonSend = view.findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextMessage = view.findViewById(R.id.editTextMessage);
                String messageText = editTextMessage.getText().toString();
                if (messageText.isEmpty()) {
                    Toast.makeText(requireActivity(), "Enter Message", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendMessage(messageText);
                editTextMessage.setText("");
            }
        });
        return view;
    }

    private void loadMessages() {
        mDatabase.child("messages").child(chatId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                mMessages.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MessageModal message = snapshot.getValue(MessageModal.class);
                    mMessages.add(message);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Log.w("Hello", "loadMessages:onCancelled", databaseError.toException());
            }
        });
    }

    private void sendMessage(String messageText) {
        String senderName = user.getEmail(); // Replace with the current user's name
        boolean senderType = true; // Replace with the current user's type (true for user, false for admin)
        String userId = user.getUid(); // Replace with the current user's ID

        MessageModal message = new MessageModal(senderName, senderType, messageText, System.currentTimeMillis() / 1000, userId);

        mDatabase.child("messages").child(chatId).push().setValue(message);
    }
}