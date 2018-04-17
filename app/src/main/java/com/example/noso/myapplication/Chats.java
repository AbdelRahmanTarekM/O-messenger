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
import android.widget.Toast;

import com.example.noso.myapplication.Interfaces.ApiClient;
import com.example.noso.myapplication.Interfaces.FriendsClient;
import com.example.noso.myapplication.beans.Friends;

import java.util.ArrayList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        android.support.v7.widget.Toolbar myToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.chats_toolbar);
        myToolbar.setTitle("Chats");
/*        myToolbar.setBackgroundColor(R.color.navigationBarColor);
        myToolbar.setTitleTextColor(R.color.windowBackground);*/
        setSupportActionBar(myToolbar);
        session = new PreferenceManager(getApplicationContext());
        String xauth = session.returnxAuth();

        listView = findViewById(R.id.chats);
        errorLayout = findViewById(R.id.layout_error_chats);

        PreferenceManager.xAuthToken = xauth;

        FriendsClient client = ApiClient.getClient().create(FriendsClient.class);
        userCall = client.friends(PreferenceManager.xAuthToken);
        Log.d("homie", "onClick: " + userCall.toString());
        userCall.enqueue(new Callback<List<Friends>>() {
            @Override
            public void onResponse(Call<List<Friends>> call, Response<List<Friends>> response) {
                List<Friends> users = response.body();
                if (users == null || users.size() == 0) {
                    errorLayout.setVisibility(View.VISIBLE);
                } else {
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Chats.this, android.R.layout.simple_list_item_1);
                    List<String> names = new ArrayList<String>();
                    for (int i = 0; i < users.size(); i++) {
                        names.add(users.get(i).getUserName());
                    }
                    arrayAdapter.addAll(names);
                    listView.setAdapter(arrayAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Toast.makeText(Chats.this, "Clicked item!", Toast.LENGTH_LONG).show();
                            Intent Q = new Intent(Chats.this, ChatScreen.class);
                            startActivity(Q);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Friends>> call, Throwable t) {
                Toast.makeText(Chats.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

        fab = findViewById(R.id.newConversation);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Chats.this, ChatScreen.class);
                startActivity(i);
            }
        });
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
        if (userCall.isExecuted())
            userCall.cancel();

    }


}
