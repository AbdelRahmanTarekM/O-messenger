package com.example.noso.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

public class NewConversation extends AppCompatActivity {
    FloatingActionButton fab;
    EditText search;
    String friend;
    List<String> Searched = new ArrayList<String>();
    List<Friends> users;
    ArrayAdapter<String> arrayAdapter;
    List<String> names;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_conversation);

        listView = (ListView) findViewById(R.id.retrivedFriends);
        fab = findViewById(R.id.searchFriend);
        search = findViewById(R.id.newFriend);


        FriendsClient client = ApiClient.getClient().create(FriendsClient.class);
        Call<List<Friends>> call = client.friends(PreferenceManager.xAuthToken);
        Log.d("homie", "onClick: " + call.toString());
        call.enqueue(new Callback<List<Friends>>() {
            @Override
            public void onResponse(Call<List<Friends>> call, Response<List<Friends>> response) {
                users = response.body();
                arrayAdapter = new ArrayAdapter<String>(NewConversation.this, android.R.layout.simple_list_item_1);

                names = new ArrayList<String>();
                for (int i = 0; i < users.size(); i++) {
                    names.add(users.get(i).getUserName());
                }

                arrayAdapter.addAll(names);

                listView.setAdapter(arrayAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Toast.makeText(NewConversation.this, "Clicked item!", Toast.LENGTH_LONG).show();
                        Intent Q = new Intent(NewConversation.this, ChatScreen.class);
                        startActivity(Q);
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Friends>> call, Throwable t) {

            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friend = search.getText().toString();
                if(arrayAdapter!=null){
                    arrayAdapter.clear();
                    for (int i = 0; i < users.size(); i++) {
                        if (friend.equals(users.get(i).getUserName())) {
                            Searched.add(users.get(i).getUserName());
                        }
                    }
                    arrayAdapter.addAll(Searched);
                    listView.setAdapter(arrayAdapter);
                    Searched.clear();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (search.getText().toString().isEmpty()) {
            finish();
        } else {
            search.setText("");
            arrayAdapter.clear();
            arrayAdapter.addAll(names);
            listView.setAdapter(arrayAdapter);
        }
    }
}
