package com.example.noso.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.noso.myapplication.Interfaces.ApiClient;
import com.example.noso.myapplication.Interfaces.ConversationsClient;
import com.example.noso.myapplication.models.Conversation;
import com.example.noso.myapplication.models.Friends;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Chats extends AppCompatActivity {
    FloatingActionButton fab;
    PreferenceManager session;
    private ListView listView;
    Call<List<Friends>> userCall;
    LinearLayout errorLayout;
    String TAG = "Homie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        android.support.v7.widget.Toolbar myToolbar = findViewById(R.id.chats_toolbar);
        myToolbar.setTitle("Chats");
        setSupportActionBar(myToolbar);
        session = new PreferenceManager(getApplicationContext());
        String xauth = session.returnxAuth();

        listView = findViewById(R.id.chats);
        errorLayout = findViewById(R.id.layout_error_chats);

        PreferenceManager.xAuthToken = xauth;

        fab = findViewById(R.id.newConversation);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Chats.this, ContactsActivity.class);
                startActivity(i);
            }
        });

        retreiveChats();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.manageFriends:
                Intent i = new Intent(Chats.this, FriendsActivity.class);
                startActivity(i);
                return true;
            case R.id.settingsAcc:
                Intent q = new Intent(Chats.this, Setting.class);
                startActivity(q);
                return true;
        }

        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (userCall.isExecuted())
//            userCall.cancel();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void retreiveChats(){
        final ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        Log.e(TAG, "retreiveChats: " );
        ConversationsClient client = ApiClient.getClient().create(ConversationsClient.class);
        Call<List<Conversation>> call = client.getConversations(PreferenceManager.id);
        Log.e(TAG, "call: " +call.toString());
        call.enqueue(new Callback<List<Conversation>>() {
            @Override
            public void onResponse(Call<List<Conversation>> call, Response<List<Conversation>> response) {
                Log.e(TAG, "onResponse: code: " + response.code());
                Log.e(TAG, "onResponse: message: " + response.message());
                if (response.isSuccessful()) {
                    PreferenceManager.setConversations(response.body());
                    if (PreferenceManager.conversations != null) {
                        Log.e("Homie", String.valueOf(PreferenceManager.conversations.getConversations().size()));
                        if (PreferenceManager.conversations.getConversations().size() != 0) {
                            for (Conversation c : PreferenceManager.conversations.getConversations()) {
                                StringBuilder builder = new StringBuilder();
                                for (Friends f : c.getUsers()) {
                                    if (!f.getUserName().equals(PreferenceManager.username)) {
                                        Log.e(TAG, "Preference: " + PreferenceManager.username);
                                        Log.e(TAG, "Conversation: " + f.getUserName());
                                        builder.append(f.getUserName());
                                        builder.append(" ");
                                        Log.e(TAG, "Username: " + f.getUserName());
                                    }
                                }
                                stringArrayAdapter.add(builder.toString());
                                Log.e(TAG, "Builder Value: " + builder.toString());
                            }

                            listView.setAdapter(stringArrayAdapter);
                            listView.setVisibility(View.VISIBLE);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Intent intent = new Intent(Chats.this, ChatScreen.class);
                                    intent.putExtra("id", PreferenceManager.conversations.getConversations().get(position).get_id());
                                    startActivity(intent);
                                }
                            });
                        }
                    } else {
                        errorLayout.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Conversation>> call, Throwable t) {
                Log.e(TAG, "onFailure: ",t );
            }
        });


    }
}
