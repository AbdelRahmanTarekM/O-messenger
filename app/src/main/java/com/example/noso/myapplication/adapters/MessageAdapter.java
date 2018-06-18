package com.example.noso.myapplication.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.bumptech.glide.Glide;
import com.example.noso.myapplication.PreferenceManager;
import com.example.noso.myapplication.R;
import com.example.noso.myapplication.models.Message;

import java.util.Calendar;
import java.util.List;

public class MessageAdapter extends BaseAdapter {

    private Context context;
    private List<Message> messageList;
    private AmazonS3Client s3;

    private void credentialProvider() {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                "us-east-1:72e60533-8780-47c4-a4aa-9b4c7b24e0a0", // Identity pool ID
                Regions.US_EAST_1 // Region
        );

        setAmazonS3Client(credentialsProvider);
    }

    private void setAmazonS3Client(CognitoCachingCredentialsProvider amazonS3Client) {
        s3 = new AmazonS3Client(amazonS3Client);
        s3.setRegion(Region.getRegion(Regions.US_EAST_1));
    }

    public MessageAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
        credentialProvider();
    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Message m = messageList.get(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (m.getSenderId().equals(PreferenceManager.id)) {
            convertView = inflater.inflate(R.layout.list_item_msg_right,
                    null);
        } else {
            convertView = inflater.inflate(R.layout.list_item_msg,
                    null);
        }


        TextView lblFrom = convertView.findViewById(R.id.lblMsgFrom);
        TextView txtMsg = convertView.findViewById(R.id.txtMsg);
        ImageView imageView = convertView.findViewById(R.id.imageView);
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_WEEK, 7);



        if (m.getType() == 2) {
            txtMsg.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            String displayPicture = s3.generatePresignedUrl("omessenger-userfiles-mobilehub-792948277/public", m.getPayload(), calendar.getTime()).toString();

            Glide.with(context)
                    .load(displayPicture)
                    .centerCrop()
                    .into(imageView);
        }

        txtMsg.setText(m.getPayload());
        lblFrom.setText(m.getSenderName());

        return convertView;
    }
}
