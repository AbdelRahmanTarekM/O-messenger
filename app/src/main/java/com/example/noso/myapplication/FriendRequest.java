package com.example.noso.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.example.noso.myapplication.Interfaces.FriendsClient;
import com.example.noso.myapplication.beans.Friends;
import com.example.noso.myapplication.beans.UserId;
import com.example.noso.myapplication.beans.Users;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Users: special
 * Date: 13-12-22
 * Time: 下午3:26
 * Mail: specialcyci@gmail.com
 */
public class FriendRequest extends Fragment {

    private View parentView;
    private ListView listView;
    private LinearLayout errorLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.friend_requests, container, false);
        listView = parentView.findViewById(R.id.friendsRequests);
        errorLayout = parentView.findViewById(R.id.layout_error_requests);
        initView();
        return parentView;
    }

    private void initView() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("https://thawing-fortress-83069.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        final FriendsClient client = retrofit.create(FriendsClient.class);
        Call<List<Friends>> call = client.requests(PreferenceManager.xAuthToken);

        Log.d("O-messenge", "onClick: " + call.toString());
        call.enqueue(new Callback<List<Friends>>() {
            @Override
            public void onResponse(Call<List<Friends>> call, Response<List<Friends>> response) {
                final List<Friends> users = response.body();
                if (users == null || users.size() == 0) {
                    errorLayout.setVisibility(View.VISIBLE);
                } else {
                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);

                    final List<String> names = new ArrayList<String>();
                    Log.d("O-messenger", "List size: " + names.size());
                    for (int i = 0; i < users.size(); i++) {
                        Log.d("O-messenger", "username: " + users.get(i).getUserName());
                        names.add(users.get(i).getUserName());
                    }
                    arrayAdapter.addAll(names);

                    listView.setAdapter(arrayAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, final int pos, long l) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                            alertDialog.setTitle("Accept friend request?");
                            alertDialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //TODO: approve friend request
                                    Log.d("homie", "onClick: AddFriend " + pos + " " + users.get(pos).getID());
                                    Call<Users> call = client.approveFriend(PreferenceManager.xAuthToken, new UserId(users.get(pos).getID()));
                                    call.enqueue(new Callback<Users>() {
                                        @Override
                                        public void onResponse(Call<Users> call, Response<Users> response) {
                                            Users user = response.body();
                                            users.remove(pos);
                                            arrayAdapter.remove(names.get(pos));
                                            Log.d("homie", "onResponse: Add Friend response is null? " + (user == null));
                                            Log.d("homie", "onClick: AddFriend " + response.message());
                                        }

                                        @Override
                                        public void onFailure(Call<Users> call, Throwable t) {

                                        }
                                    });
                                }
                            });
                            alertDialog.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //TODO: reject friend request
                                    Call<Users> call = client.rejectFriend(PreferenceManager.xAuthToken, new UserId(users.get(pos).getID()));
                                    call.enqueue(new Callback<Users>() {
                                        @Override
                                        public void onResponse(Call<Users> call, Response<Users> response) {
                                            Users user = response.body();
                                            users.remove(pos);
                                            arrayAdapter.remove(names.get(pos));
                                            Log.d("homie", "onResponse: Add Friend response is null? " + (user == null));
                                            Log.d("homie", "onClick: AddFriend " + response.message());
                                        }

                                        @Override
                                        public void onFailure(Call<Users> call, Throwable t) {

                                        }
                                    });
                                }
                            });
                            AlertDialog dialog = alertDialog.create();
                            dialog.show();
                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<List<Friends>> call, Throwable t) {

            }
        });
    }

}
