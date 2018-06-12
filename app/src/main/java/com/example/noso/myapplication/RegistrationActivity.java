package com.example.noso.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "homie";
    String path;
    AmazonS3Client s3;
    TransferUtility transferUtility;
    EditText uName, mail, pass, rPass;
    ImageButton profilePicture;
    TextInputLayout mailTIL, pwTIL, rePwTIL, uNameTIL;
    Button Regbtn;
    boolean mailGood, passGood, rPassGood, uNameGood;
    PreferenceManager session;
    LinearLayout parent;
    Animation shakingAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        credentialProvider();
        setTransferUtility();

        shakingAnimation = AnimationUtils.loadAnimation(this, R.anim.shake_animation);
        mailGood = passGood = rPassGood = false;

        uName = findViewById(R.id.UserName);
        mail = findViewById(R.id.mail);
        pass = findViewById(R.id.Password);
        rPass = findViewById(R.id.Re_password);
        pwTIL = findViewById(R.id.password_til);
        Regbtn = findViewById(R.id.register);
        mailTIL = findViewById(R.id.mail_til);
        rePwTIL = findViewById(R.id.rePW_til);
        uNameTIL = findViewById(R.id.username_TIL);
        parent = findViewById(R.id.reg_parent);
        profilePicture = findViewById(R.id.profile_IV);
        profilePicture.setOnClickListener(this);
        session = new PreferenceManager(getApplicationContext());

        uName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validateUsername();
            }
        });
        mail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEmail();
            }
        });

        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validatePassword();
            }
        });

        rPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                validateConfirmPassword();
            }
        });

        Regbtn.setOnClickListener(this);

    }

    private void validateConfirmPassword() {
        if (rPass.getText().toString().isEmpty()) {
            rPassGood = false;
            rePwTIL.setErrorEnabled(true);
            rePwTIL.setError("A password confirmation is required");
        } else if (!rPass.getText().toString().equals(pass.getText().toString())) {
            rePwTIL.setErrorEnabled(true);
            rePwTIL.setError("password does not match");
            rPassGood = false;
        } else {
            rePwTIL.setErrorEnabled(false);
            rPassGood = true;
        }
    }

    private void validatePassword() {
        String password = pass.getText().toString();
        if (password.isEmpty()) {
            passGood = false;
            pwTIL.setErrorEnabled(true);
            pwTIL.setError("A Password is required");
        } else if (password.length() < 8) {
            pwTIL.setErrorEnabled(true);
            pwTIL.setError("Password too short");
            passGood = false;
        } else {
            pwTIL.setErrorEnabled(false);
            passGood = true;
        }
    }

    private void validateEmail() {
        String emailIn = mail.getText().toString();
        if (emailIn.isEmpty()) {
            mailGood = false;
            mailTIL.setErrorEnabled(true);
            mailTIL.setError("The Email is required");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailIn).matches()) {
            mailTIL.setErrorEnabled(true);
            mailTIL.setError("Wrong email format");
            mailGood = false;
        } else {
            mailTIL.setErrorEnabled(false);
            mailGood = true;
        }
    }

    private void validateUsername() {
        String username = uName.getText().toString();
        if (username.isEmpty()) {
            uNameGood = false;
            uNameTIL.setErrorEnabled(true);
            uNameTIL.setError("The Username is required");
        } else {
            uNameTIL.setErrorEnabled(false);
            uNameGood = true;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.register) {
            validateUsername();
            validateEmail();
            validatePassword();
            validateConfirmPassword();
            if (mailGood && passGood && rPassGood && uNameGood) {
                final String First, Email, Password, Username;
                Email = mail.getText().toString();
                Password = pass.getText().toString();
                Username = uName.getText().toString();
                Users users = new Users(Username, Email, Password);

                UsersClient client = ApiClient.getClient().create(UsersClient.class);
                Call<Users> call = client.signup(users);
                Log.d("homie", "onClick: " + call.toString());
                disableControls();
                call.enqueue(new Callback<Users>() {
                    @Override
                    public void onResponse(Call<Users> call, Response<Users> response) {
                        enableControls();
                        Users users = response.body();
                        if (path != null && users != null) {
                            TransferObserver observer = transferUtility.upload(users.getId(), new File(path));
                            transferObserverListener(observer);
                        }
                        String xAuth = response.headers().get("x-auth");
                        session.LoginSession(users.getEmail(), xAuth,users.getUsername(),users.getId());
                        Intent i = new Intent(RegistrationActivity.this, WelcomeActivity.class);
                        startActivity(i);
                        finish();

                    }

                    @Override
                    public void onFailure(Call<Users> call, Throwable t) {
                        enableControls();
                        Toast.makeText(RegistrationActivity.this, "Sorry, something went wrong!", Toast.LENGTH_LONG).show();
                        Log.e("homie", "onFailure: ", t);
                    }
                });
            } else {
                parent.startAnimation(shakingAnimation);
            }
        } else if (v.getId() == R.id.profile_IV) {
            final Intent intent = ImageSelectorUtils.getImageSelectionIntent();
            startActivityForResult(intent, 0);
        }
    }

    private void disableControls() {
        uName.setEnabled(false);
        mail.setEnabled(false);
        pass.setEnabled(false);
        rPass.setEnabled(false);
        Regbtn.setEnabled(false);
    }

    private void enableControls() {
        uName.setEnabled(true);
        mail.setEnabled(true);
        pass.setEnabled(true);
        rPass.setEnabled(true);
        Regbtn.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
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
                profilePicture.setImageBitmap(bitmap);
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
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;

                Log.d("MainActivity", "   ID:" + id + "   bytesCurrent: " + bytesCurrent + "   bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("MainActivity", "onError: ", ex);
            }
        });
    }
}
