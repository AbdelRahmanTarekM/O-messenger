package com.example.noso.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.example.noso.myapplication.Interfaces.ApiClient;
import com.example.noso.myapplication.Interfaces.UsersClient;
import com.example.noso.myapplication.beans.Users;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Setting extends AppCompatActivity implements View.OnClickListener{
    ImageButton imgUser;
    TextView mail;
    EditText username;
    Spinner status;
    Button saveChanges;

    String path,downloadPath= "/storage/emulated/0/Download/";
    AmazonS3Client s3;
    TransferUtility transferUtility;
    private static final String TAG = "homie";

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

        saveChanges.setOnClickListener(this);
        imgUser.setOnClickListener(this);

        credentialProvider();
        setTransferUtility();

        UsersClient client = ApiClient.getClient().create(UsersClient.class);

        Call<Users> call = client.me(PreferenceManager.xAuthToken);
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                Users users = response.body();
                if (users != null) {
                    username.setText(users.getUsername());
                    mail.setText(users.getEmail());
                    if (path != null ) {
                        TransferObserver observer = transferUtility.download(users.getId(), new File(path+users.getId()+"png"));
                        transferObserverListener(observer);
                    }
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                Log.d(TAG, "onActivityResult: " + imageUri);
                path = ImageSelectorUtils.getFilePathFromUri(this, imageUri);
                Log.d(TAG, "path: " + path);
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                imgUser.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void credentialProvider() {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:72e60533-8780-47c4-a4aa-9b4c7b24e0a0", // Identity pool ID
                Regions.US_EAST_1 // Region
        );

        setAmazonS3Client(credentialsProvider);
    }

    public void setAmazonS3Client(CognitoCachingCredentialsProvider amazonS3Client) {
        s3 = new AmazonS3Client(amazonS3Client);
        s3.setRegion(Region.getRegion(Regions.US_EAST_1));
    }

    public void setTransferUtility() {
        transferUtility = TransferUtility.builder().defaultBucket("omessenger-userfiles-mobilehub-792948277/public").s3Client(s3).context(this).build();
    }

    public void transferObserverListener(TransferObserver observer) {
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    Log.d("MainActivity", "Done");
                    //TODO: set image with glide
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;
                //TODO: show loading message or something

                Log.d("MainActivity", "   ID:" + id + "   bytesCurrent: " + bytesCurrent + "   bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("MainActivity", "onError: ", ex);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgUser:
                //TODO: choose picture and stuff
                break;
            case R.id.saveButton:
                //TODO: upload picture
                break;
        }
    }
}
