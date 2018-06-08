package com.example.noso.myapplication;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.noso.myapplication.beans.Friends;

import java.util.List;
public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.MyViewHolder>{

    private Context mContext;
    private List<Friends> requestsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count =  view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);

        }
    }
    public RequestAdapter(Context mContext, List<Friends> requestsList) {
        this.mContext = mContext;
        this.requestsList = requestsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.requestcard, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Friends friend = requestsList.get(position);
        holder.title.setText(friend.getUserName());
        // holder.count.setText(friend.getNumOfSongs() + " songs");

        // loading album cover using Glide library
        Glide.with(mContext).load(friend.getPicURL()).into(holder.thumbnail);


    }


    @Override
    public int getItemCount() {
        return requestsList.size();
    }


}
