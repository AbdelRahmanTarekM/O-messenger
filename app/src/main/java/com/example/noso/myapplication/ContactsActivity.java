package com.example.noso.myapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.noso.myapplication.Interfaces.ContactAdapter;
import com.example.noso.myapplication.beans.Users;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recyclerView;
    ContactAdapter mAdapter;
    List<Users> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        android.support.v7.widget.Toolbar myToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.contacts_toolbar);
        myToolbar.setTitle("Select Contacts ...");
        setSupportActionBar(myToolbar);
        //Floating Action Button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
        //Recycler View
        recyclerView = findViewById(R.id.recycler_view);
        mAdapter = new ContactAdapter(mList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        loadData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                break;
        }
    }

    private void loadData() {

        Users users = new Users("Homie", "homie@mail");
        mList.add(users);

        users = new Users("Test", "homie@mail");
        mList.add(users);

        users = new Users("Nigga", "homie@mail");
        mList.add(users);

        mAdapter.notifyDataSetChanged();
    }
}
