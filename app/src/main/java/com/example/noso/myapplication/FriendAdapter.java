package com.example.noso.myapplication;
import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.noso.myapplication.beans.Friends;

import java.util.List;
public class FriendAdapter extends  RecyclerView.Adapter<FriendAdapter.MyViewHolder>{
    private Context mContext;
    private List<Friends> friendsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail, overflow;
        public ImageButton removeFriend;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title_friend);
            count =  view.findViewById(R.id.count_friend);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail_friend);
            removeFriend = view.findViewById(R.id.overflow_friend);

        }
    }
    public FriendAdapter(Context mContext, List<Friends> friendsList) {
        this.mContext = mContext;
        this.friendsList = friendsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friendcard, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Friends friend = friendsList.get(position);
        holder.title.setText(friend.getUserName());
        // holder.count.setText(friend.getNumOfSongs() + " songs");

        // loading album cover using Glide library
        Glide.with(mContext).load(friend.getPicURL()).into(holder.thumbnail);


        }

    @Override
    public int getItemCount() {
        return friendsList.size();
    }
    }


