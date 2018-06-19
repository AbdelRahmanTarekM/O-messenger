package com.example.noso.myapplication.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.noso.myapplication.PreferenceManager;
import com.example.noso.myapplication.R;
import com.example.noso.myapplication.models.Conversation;
import com.example.noso.myapplication.models.Friends;

import java.util.List;

public class ConversationsAdapter extends BaseAdapter {
    private List<Conversation> data;
    private LayoutInflater inflater;
    private Context context;
    private Drawable group,single;

    public ConversationsAdapter(List<Conversation> data, Context context){

        this.data=data;
        this.context=context;

        group=ContextCompat.getDrawable(context,R.drawable.group_background);
        single=ContextCompat.getDrawable(context,R.drawable.single_background);
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Conversation c=data.get(position);

        inflater=LayoutInflater.from(context);
        convertView=inflater.inflate(R.layout.conversation_list_item,parent,false);

        TextView conversationName=convertView.findViewById(R.id.txt_conversation);
        StringBuilder builder = new StringBuilder();

        for (Friends f : c.getUsers()) {
            if (!f.getID().equals(PreferenceManager.id))
                builder.append(f.getUserName()+" ");
        }

        conversationName.setText(builder.toString());

        ImageView conversationImage=convertView.findViewById(R.id.img_conversation);

        if(c.getUsers().size() == 2){ //individual chat-room
            conversationImage.setBackground(single);
            conversationImage.setImageResource(R.drawable.s);
        }else if(c.getUsers().size() > 2){ //group chat room
            conversationImage.setBackground(group);
            conversationImage.setImageResource(R.drawable.g);
        }

        return convertView;
    }
}
