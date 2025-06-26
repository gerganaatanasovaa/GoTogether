package com.example.pmu.fragments;

import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmu.R;
import com.example.pmu.adapters.ConversationAdapter;
import com.example.pmu.interfaces.ChatClickListener;
import com.example.pmu.interfaces.ConversationsListener;
import com.example.pmu.models.ConversationModel;
import com.example.pmu.models.User;
import com.example.pmu.utils.RequestBuilder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.fragment_all_chats)
public class AllChatsFragment extends BaseFragment {

    @ViewById RecyclerView chatsRecyclerView;

    private ConversationAdapter adapter;
    private final List<ConversationModel> conversationList = new ArrayList<>();


    @AfterViews
    void init() {
        adapter = new ConversationAdapter(
                conversationList,
                new ChatClickListener() {
                    @Override
                    public void onClick(ConversationModel convo) {
                        ChatFragment chatFragment = new ChatFragment_();
                        Bundle args = new Bundle();
                        args.putString("receiverId", convo.getUser().getUserId());
                        chatFragment.setArguments(args);
                        addFragment(chatFragment);
                    }
                }
        );

        chatsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatsRecyclerView.setAdapter(adapter);

        loadChats();
    }

    private void loadChats() {
        String myId = User.getInstance().getUserId();
        RequestBuilder.getAllMessages(
                myId,
                new ConversationsListener() {
                    @Override
                    public void onSuccess(List<ConversationModel> conversations) {
                        conversationList.clear();
                        conversationList.addAll(conversations);
                        adapter.notifyDataSetChanged();

                        if (conversationList.isEmpty()) {
                            Toast.makeText(getContext(), "No conversations to show", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Loaded " + conversationList.size() + " chats", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(
                                getContext(),
                                "Failed to load chats: " + errorMessage,
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }
}
