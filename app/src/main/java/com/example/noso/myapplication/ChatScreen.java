package com.example.noso.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.noso.myapplication.Interfaces.ApiClient;
import com.example.noso.myapplication.Interfaces.ConversationsClient;
import com.example.noso.myapplication.adapters.MessageAdapter;
import com.example.noso.myapplication.models.Message;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatScreen extends AppCompatActivity implements View.OnClickListener {

    public static final int GET_FROM_GALLERY = 3;
    public static final int GET_FROM_CAMERA = 4;
    private static final String TAG = "Homie";
    private static int SIGN_IN_REQUEST_CODE = 1;
    private RelativeLayout activity_chat_screen;
    private LinearLayout messageBoxLayout, revealingLayout;
    private RelativeLayout relGesture, relFile, relCamera;
    private String uri = "http://192.168.1.103:3001/", conversationId = "5b1d91c5f25ddb39805bb62c";

    private MessageAdapter adapter;
    private List<Message> messages;
    private ListView messagesLV;

    FloatingActionButton fab, cameraBtn, fileBtn, gestureBtn;
    EditText input;
    boolean chatMode;
    Animation moveIn, moveOut, moveInFile, moveInGesture, moveInCamera;

    private Socket mSocket;

    {
        try {
            mSocket = IO.socket(uri);
        } catch (URISyntaxException e) {
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == GET_FROM_GALLERY || requestCode == GET_FROM_CAMERA) && resultCode == Activity.RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                Log.e("homie", "onActivityResult: " + bitmap);
                TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                if (!textRecognizer.isOperational()) {
                    Log.e("ERROR", "Detector Dependecies are not ready yet");
                } else {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = textRecognizer.detect(frame);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < items.size(); i++) {
                        TextBlock item = items.valueAt(i);
                        stringBuilder.append(item.getValue());
                        stringBuilder.append("\n");
                    }
                    input.setText(stringBuilder.toString());
                    Log.d("homie", "onActivityResult: " + stringBuilder.length());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(ChatScreen.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }


    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = null; //(JSONObject) args[0];
                    try {
                        data = new JSONObject((String) args[0]);
                        Log.e("Homie", "run: success");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String message;
                    int sent;
                    try {
                        String senderId, senderName, payload, conversationId;
                        int type = data.getInt("type");
                        senderId = data.getString("senderId");
                        senderName = data.getString("senderName");
                        payload = data.getString("payload");
                        conversationId = data.getString("conversationId");

                        Message message1 = new Message(senderId, senderName, type, payload, conversationId);
                        messages.add(message1);
                        adapter.notifyDataSetChanged();
//                        Toast.makeText(ChatScreen.this, payload, Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "run: ", e);
                        return;
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);
        activity_chat_screen = findViewById(R.id.chat_screen);
        fab = findViewById(R.id.sendFab);
        cameraBtn = findViewById(R.id.btn_camera);
        gestureBtn = findViewById(R.id.btn_gesture);
        fileBtn = findViewById(R.id.btn_file);
        input = findViewById(R.id.input);
        relCamera = findViewById(R.id.rel_camera);
        relFile = findViewById(R.id.rel_file);
        relGesture = findViewById(R.id.rel_gesture);
        revealingLayout = findViewById(R.id.revealingLayout);
        messageBoxLayout = findViewById(R.id.message_box_layout);
        messagesLV = findViewById(R.id.list_of_messages);
        messages = new ArrayList<>();

        conversationId = getIntent().getStringExtra("id");

        ConversationsClient client = ApiClient.getClient().create(ConversationsClient.class);
        Call<List<Message>> listCall = client.getMessages(conversationId);
        listCall.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {

                if (response.isSuccessful()) {
                    messages = response.body();
                    adapter = new MessageAdapter(ChatScreen.this, messages);
                    messagesLV.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {

            }
        });



        mSocket.on("newMessage", onNewMessage);
        mSocket.connect();

        moveIn = AnimationUtils.loadAnimation(this, R.anim.move_from_right);
        moveOut = AnimationUtils.loadAnimation(this, R.anim.move_out_right);
        moveInCamera = AnimationUtils.loadAnimation(this, R.anim.move_in_camera);
        moveInFile = AnimationUtils.loadAnimation(this, R.anim.move_in_file);
        moveInGesture = AnimationUtils.loadAnimation(this, R.anim.move_in_gesture);


        cameraBtn.setOnClickListener(this);
        gestureBtn.setOnClickListener(this);
        fileBtn.setOnClickListener(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chatMode) {
                    String message = input.getText().toString().trim();
                    if (TextUtils.isEmpty(message)) {
                        return;
                    }
                    Message message1 = new Message(PreferenceManager.id, PreferenceManager.username, 1, message, conversationId);
                    Gson gson = new Gson();
                    String jsonMessage = gson.toJson(message1, Message.class);
                    mSocket.emit("createMessage", jsonMessage);
                    input.setText("");
                } else {
                    revealingLayout.setVisibility(View.VISIBLE);
                    messageBoxLayout.setVisibility(View.GONE);
                    revealingLayout.startAnimation(moveIn);
                    relGesture.startAnimation(moveInGesture);
                    relFile.startAnimation(moveInFile);
                    relCamera.startAnimation(moveInCamera);
                }
            }
        });


        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                final String messageText = input.getText().toString().trim();
                if (messageText.isEmpty()) {
                    chatMode = false;
                    fab.setImageResource(R.drawable.ic_add);
                } else {
                    fab.setImageResource(R.drawable.ic_send);
                    chatMode = true;
                }
            }
        });

        mSocket.emit("join", conversationId);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_camera:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChatScreen.this);
                alertDialog.setTitle("choose picture from ..");
                alertDialog.setPositiveButton("camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, GET_FROM_CAMERA);
                    }
                });
                alertDialog.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        startActivityForResult(intent, GET_FROM_GALLERY);
                    }
                });
                AlertDialog dialog = alertDialog.create();
                dialog.show();
                break;
            case R.id.btn_gesture:
                Toast.makeText(this, "Currently Unavailable", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_file:
                Toast.makeText(this, "Currently Unavailable", Toast.LENGTH_SHORT).show();
                break;

        }
        revealingLayout.startAnimation(moveOut);
        messageBoxLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("new message", onNewMessage);
    }
}
