package com.example.noso.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.noso.myapplication.Interfaces.ApiClient;
import com.example.noso.myapplication.Interfaces.ConversationsClient;
import com.example.noso.myapplication.Interfaces.FriendsClient;
import com.example.noso.myapplication.Interfaces.UsersClient;
import com.example.noso.myapplication.adapters.ContactAdapter;
import com.example.noso.myapplication.beans.Conversation;
import com.example.noso.myapplication.beans.Friends;
import com.example.noso.myapplication.beans.Users;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactsActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recyclerView;
    ContactAdapter mAdapter;
    List<Friends> mList = new ArrayList<>(), selected = new ArrayList<>();
    private Call<List<Friends>> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        android.support.v7.widget.Toolbar myToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.contacts_toolbar);
        myToolbar.setTitle("Select Contacts ...");
        setSupportActionBar(myToolbar);
        //Floating Action Button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
        //Recycler View
        recyclerView = findViewById(R.id.recycler_view);
        mAdapter = new ContactAdapter(mList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        Log.d("homie", "onCreate: ");
        loadData();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                selected.clear();
                for (int i = 0; i < mAdapter.getItemCount(); i++) {
                    ContactAdapter.ViewHolder vh = (ContactAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                    if (vh.checked.isChecked()) {
                        selected.add(mList.get(i));
                    }
                }
                if (selected.isEmpty()) {
                    Toast.makeText(this, "Please select at least One Contact", Toast.LENGTH_LONG).show();
                } else {
                    //TODO: load personal data from shared preferences
                    UsersClient client = ApiClient.getClient().create(UsersClient.class);

                    Call<Users> call = client.me(PreferenceManager.xAuthToken);
                    call.enqueue(new Callback<Users>() {
                        @Override
                        public void onResponse(Call<Users> call, Response<Users> response) {
                            Users users = response.body();
                            if (users != null) {
                                selected.add(new Friends(users.getUsername(), users.getEmail(), users.getId()));
                                ConversationsClient convoClient = ApiClient.getClient().create(ConversationsClient.class);
                                Call<Conversation> convoCall = convoClient.newConversation(new Conversation(selected));
                                convoCall.enqueue(new Callback<Conversation>() {
                                    @Override
                                    public void onResponse(Call<Conversation> call, Response<Conversation> response) {
                                        if (response.isSuccessful()) {
                                            //TODO: add conversation to shared preferences
                                            Conversation conversation = response.body();
                                            PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
                                            preferenceManager.addConversation(conversation);
                                            Intent i = new Intent(ContactsActivity.this, ChatScreen.class);
                                            i.putExtra("id", conversation.get_id());
                                            startActivity(i);
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Conversation> call, Throwable t) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<Users> call, Throwable t) {

                        }
                    });

                }

                break;
        }
    }

    private void loadData() {


        FriendsClient client = ApiClient.getClient().create(FriendsClient.class);
        call = client.friends(PreferenceManager.xAuthToken);
        Log.d("homie", "onClick: " + call.toString());
        call.enqueue(new Callback<List<Friends>>() {
            @Override
            public void onResponse(Call<List<Friends>> call, Response<List<Friends>> response) {
                List<Friends> users = response.body();
                if (users == null || users.size() == 0) {
                    //TODO: display no friends layout
                    Toast.makeText(ContactsActivity.this, "No friends", Toast.LENGTH_LONG).show();
                } else {
                    mList.addAll(users);
                    //Toast.makeText(ContactsActivity.this, " "+mList.size(), Toast.LENGTH_LONG).show();
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Friends>> call, Throwable t) {

            }
        });

    }
}
