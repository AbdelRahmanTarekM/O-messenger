package com.example.noso.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.noso.myapplication.Interfaces.ApiClient;
import com.example.noso.myapplication.Interfaces.FriendsClient;
import com.example.noso.myapplication.models.UserId;
import com.example.noso.myapplication.models.UserName;
import com.example.noso.myapplication.models.Users;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFriendActivity extends AppCompatActivity implements View.OnClickListener {

    ListView resultsList;
    List<Users> users;
    Button searchBtn;
    EditText searchBar;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        resultsList = findViewById(R.id.results_listView);
        searchBar = findViewById(R.id.search_bar_et);
        searchBtn = findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(this);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        resultsList.setAdapter(arrayAdapter);
        resultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//                Toast.makeText(AddFriendActivity.this,"item clicked",Toast.LENGTH_LONG).show();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddFriendActivity.this);
                alertDialog.setTitle("Send friend request?");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        FriendsClient client = ApiClient.getClient().create(FriendsClient.class);
                        Call<Users> call = client.addFriend(PreferenceManager.xAuthToken, new UserId(users.get(position).getId()));

                        call.enqueue(new Callback<Users>() {
                            @Override
                            public void onResponse(Call<Users> call, Response<Users> response) {
                                Users users = response.body();
                                Log.d("homie", "onResponse: Add Friend response is null? " + (users == null));
                                Log.d("homie", "onClick: AddFriend " + response.message());
                                if (response.message().equals("Bad Request"))
                                    Toast.makeText(AddFriendActivity.this, "Request already sent", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onFailure(Call<Users> call, Throwable t) {

                            }
                        });
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = alertDialog.create();
                dialog.show();
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.search_btn) {

            FriendsClient client = ApiClient.getClient().create(FriendsClient.class);
            Call<List<Users>> call = client.search(PreferenceManager.xAuthToken, new UserName(searchBar.getText().toString()));
            call.enqueue(new Callback<List<Users>>() {
                @Override
                public void onResponse(Call<List<Users>> call, Response<List<Users>> response) {
                    users = response.body();
                    arrayAdapter.clear();
                    List<String> names = new ArrayList<>();
                    for (int i = 0; i < users.size(); i++) {
                        names.add(users.get(i).getUsername() + "\n" + users.get(i).getEmail());
                    }

                    arrayAdapter.addAll(names);

                }

                @Override
                public void onFailure(Call<List<Users>> call, Throwable t) {

                }
            });
        }
    }
}
