package com.example.pmu.fragments;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pmu.R;
import com.example.pmu.adapters.ChatAdapter;
import com.example.pmu.interfaces.MessageListener;
import com.example.pmu.interfaces.MessageSentListener;
import com.example.pmu.interfaces.UserFetchListener;
import com.example.pmu.models.Message;
import com.example.pmu.models.User;
import com.example.pmu.utils.RequestBuilder;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@EFragment(R.layout.fragment_chat)
public class ChatFragment extends BaseFragment{

    @ViewById
    EditText messageEditText;

    @ViewById
    ImageButton sendImageButton;

    @ViewById
    RecyclerView chatRecyclerView;

    @FragmentArg
    String receiverId;

    @ViewById
    TextView userNameTextView;

    @ViewById
    de.hdodenhof.circleimageview.CircleImageView userImageView;

    private ChatAdapter chatAdapter;
    private List<Message> messageList = new ArrayList<>();
   private ListenerRegistration messageListener;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (messageListener != null) {
            messageListener.remove();
            messageListener = null;
        }
    }
    @AfterViews
    void init() {
        Log.d("ChatFragment", "receiverId: " + receiverId);

        chatAdapter = new ChatAdapter(messageList, User.getInstance().getUserId());
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRecyclerView.setAdapter(chatAdapter);

        setupUserHeader();
        setupMessageSending();
        startListeningForMessages();
    }

    private void setupUserHeader() {
        RequestBuilder.getUserById(receiverId, new UserFetchListener() {
            @Override
            public void onSuccess(User user) {
                userNameTextView.setText(user.getFirstName()); // или getFullName()
                Glide.with(getContext())
                        .load(user.getProfileImageUrl())
                        .placeholder(R.drawable.ic_placeholder)
                        .into(userImageView);

                userImageView.setOnClickListener(v -> openUserProfile());
                userNameTextView.setOnClickListener(v -> openUserProfile());

            }

            @Override
            public void onFailure(String message) {
                userNameTextView.setText("Unknown");
            }
        });
    }

    private void setupMessageSending() {
        sendImageButton.setOnClickListener(v -> {
            String text = messageEditText.getText().toString().trim();
            Timestamp timestamp = Timestamp.now();
            if (!text.isEmpty()) {
                RequestBuilder.sendMessage(receiverId, text, timestamp, new MessageSentListener() {
                    @Override
                    public void onSuccess() {
                        messageEditText.setText("");
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(getContext(), "Failed to send", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void startListeningForMessages() {
        messageListener = RequestBuilder.listenForMessages(
                User.getInstance().getUserId(),
                receiverId,
                new MessageListener() {
                    @Override
                    public void onSuccess(List<Message> messages) {
                        messageList.clear();
                        messageList.addAll(messages);
                        chatAdapter.notifyDataSetChanged();
                        chatRecyclerView.scrollToPosition(messageList.size() - 1);
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openUserProfile() {
        ProfileFragment_ fragment = new ProfileFragment_();
        Bundle bundle = new Bundle();
        bundle.putString("userId", receiverId);
        fragment.setArguments(bundle);
        addFragment(fragment);
    }
}
