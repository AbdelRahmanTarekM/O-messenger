package com.example.noso.myapplication;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.noso.myapplication.Interfaces.ApiClient;
import com.example.noso.myapplication.Interfaces.FriendsClient;
import com.example.noso.myapplication.models.Friends;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Users: special
 * Date: 13-12-22
 * Time: 下午3:26
 * Mail: specialcyci@gmail.com
 */
public class FriendsList extends Fragment {

    private View parentView;
    private ListView listView;
    private LinearLayout errorLayout;
    private Call<List<Friends>> call;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.friends_list, container, false);
        listView = parentView.findViewById(R.id.friendsList);
        Toast.makeText(getActivity(),"Click",Toast.LENGTH_LONG).show();
        errorLayout = parentView.findViewById(R.id.layout_error_friends);
        initView();
        return parentView;
    }

    private void initView() {


        FriendsClient client = ApiClient.getClient().create(FriendsClient.class);
        call = client.friends(PreferenceManager.xAuthToken);
        Log.d("homie", "onClick: " + call.toString());
        call.enqueue(new Callback<List<Friends>>() {
            @Override
            public void onResponse(Call<List<Friends>> call, Response<List<Friends>> response) {
                List<Friends> users = response.body();
                if (users == null || users.size() == 0) {
                    errorLayout.setVisibility(View.VISIBLE);
                } else {
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
                    List<String> names = new ArrayList<String>();
                    for (int i = 0; i < users.size(); i++) {
                        names.add(users.get(i).getUserName());
                    }
                    arrayAdapter.addAll(names);
                    listView.setAdapter(arrayAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Toast.makeText(getActivity(), "Clicked item!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Friends>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (call.isExecuted())
            call.cancel();
    }
}
