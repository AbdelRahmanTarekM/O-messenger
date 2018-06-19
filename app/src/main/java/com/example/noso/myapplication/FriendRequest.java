package com.example.noso.myapplication;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.noso.myapplication.Interfaces.ApiClient;
import com.example.noso.myapplication.Interfaces.FriendsClient;
import com.example.noso.myapplication.adapters.RequestAdapter;
import com.example.noso.myapplication.models.Friends;
import com.example.noso.myapplication.beans.RecyclerTouchListener;
import com.example.noso.myapplication.models.UserId;
import com.example.noso.myapplication.models.Users;

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
public class FriendRequest extends Fragment {

    private View parentView;
    private ListView listView;
    private CardView errorLayout;
    private RecyclerView recyclerView;
    private RequestAdapter adapter;
    private List<Friends> friendsList;
    private int clickPosition;
    private Button overflow;
    private FriendsClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.friend_requests, container, false);
        overflow = parentView.findViewById(R.id.overflow);
        recyclerView = parentView.findViewById(R.id.recycler_view);
        errorLayout=parentView.findViewById(R.id.request_empty_card);
        friendsList = new ArrayList<>();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        initView();

        return parentView;
    }


    private void initView() {


        client = ApiClient.getClient().create(FriendsClient.class);
        Call<List<Friends>> call = client.requests(PreferenceManager.xAuthToken);

        Log.d("O-messenge", "onClick: " + call.toString());
        call.enqueue(new Callback<List<Friends>>() {
            @Override
            public void onResponse(Call<List<Friends>> call, Response<List<Friends>> response) {
                friendsList = response.body();
                if (friendsList == null || friendsList.size() == 0) {
                     errorLayout.setVisibility(View.VISIBLE);
                } else {
                    adapter = new RequestAdapter(getActivity(), friendsList);
                    recyclerView.setAdapter(adapter);
                }
                recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, final int position) {
                        clickPosition = position;
                        showPopupMenu(view);

                        }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));
            }
            @Override
            public void onFailure(Call<List<Friends>> call, Throwable t) {

            }
        });
    }

    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(getActivity(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.requestmenu, popup.getMenu());
        popup.setOnMenuItemClickListener(new FriendRequest.MyMenuItemClickListener());
        popup.show();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_approve_request:
                    Call<Users> call = client.approveFriend(PreferenceManager.xAuthToken, new UserId(friendsList.get(clickPosition).getID()));
                    call.enqueue(new Callback<Users>() {
                        @Override
                        public void onResponse(Call<Users> call, Response<Users> response) {
                            Users user = response.body();
                            friendsList.remove(clickPosition);
                            adapter = new RequestAdapter(getActivity(), friendsList);
                            recyclerView.setAdapter(adapter);
                        }

                        @Override
                        public void onFailure(Call<Users> call, Throwable t) {
                            Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return true;


                case R.id.action_reject_request:
                    call = client.rejectFriend(PreferenceManager.xAuthToken, new UserId(friendsList.get(clickPosition).getID()));
                    call.enqueue(new Callback<Users>() {
                        @Override
                        public void onResponse(Call<Users> call, Response<Users> response) {
                            Users user = response.body();
                            friendsList.remove(clickPosition);
                            adapter = new RequestAdapter(getActivity(), friendsList);
                            recyclerView.setAdapter(adapter);
                        }

                        @Override
                        public void onFailure(Call<Users> call, Throwable t) {
                            Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });

                    return true;
                default:
            }
            return false;
        }
    }
}

