package com.example.noso.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.noso.myapplication.Interfaces.ApiClient;
import com.example.noso.myapplication.Interfaces.UsersClient;
import com.example.noso.myapplication.beans.Users;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Setting extends AppCompatActivity {
    ImageView imgUser;
    TextView mail;
    EditText username;
    Spinner status;
    Button saveChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        myToolbar.setTitle("Settings");
        setSupportActionBar(myToolbar);

        imgUser = findViewById(R.id.imgUser);
        username = findViewById(R.id.textName);
        status = findViewById(R.id.statusSpinner);
        saveChanges = findViewById(R.id.saveButton);
        mail = findViewById(R.id.mail_tv);

        UsersClient client = ApiClient.getClient().create(UsersClient.class);

        Call<Users> call = client.me(PreferenceManager.xAuthToken);
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                Users users = response.body();
                if (users != null) {
                    username.setText(users.getUsername());
                    mail.setText(users.getEmail());
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {

            }
        });
    }
}
