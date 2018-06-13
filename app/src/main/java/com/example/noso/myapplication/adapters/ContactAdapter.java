package com.example.noso.myapplication.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.noso.myapplication.R;
import com.example.noso.myapplication.models.Friends;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    private List<Friends> usersList;
    public boolean[] check;

    public ContactAdapter(List<Friends> usersList) {
        this.usersList = usersList;
        check = new boolean[usersList.size()];
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_row, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Friends user = usersList.get(position);
        //TODO: Glide profile picture
        holder.name.setText(user.getUserName());
        holder.email.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView pp;
        public TextView name, email;
        public CheckBox checked;

        public ViewHolder(View itemView) {
            super(itemView);
            pp = itemView.findViewById(R.id.contact_pic_iv);
            name = itemView.findViewById(R.id.contact_username_tv);
            email = itemView.findViewById(R.id.contact_email_tv);
            checked = itemView.findViewById(R.id.contact_cb);
        }

        @Override
        public void onClick(View v) {
            checked.setChecked(!checked.isChecked());
        }
    }
}
