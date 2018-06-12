package com.example.noso.myapplication.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.noso.myapplication.PreferenceManager;
import com.example.noso.myapplication.R;
import com.example.noso.myapplication.beans.Message;

import java.util.List;

public class MessageAdapter extends BaseAdapter {

    private Context context;
    private List<Message> messageList;

    public MessageAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
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

        txtMsg.setText(m.getPayload());
        lblFrom.setText(m.getSenderName());

        return convertView;
    }
}
