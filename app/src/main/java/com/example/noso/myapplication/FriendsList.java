package com.example.noso.myapplication;


import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.noso.myapplication.Interfaces.ApiClient;
import com.example.noso.myapplication.Interfaces.FriendsClient;
import com.example.noso.myapplication.adapters.FriendAdapter;
import com.example.noso.myapplication.models.Friends;
import com.example.noso.myapplication.beans.RecyclerTouchListener;
import com.example.noso.myapplication.models.UserId;
import com.example.noso.myapplication.models.Users;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendsList extends Fragment {

    private View parentView;
    private ListView listView;
    private LinearLayout errorLayout;
    private Call<List<Friends>> call;
    private Call<List<Friends>> callNew;
    private int clickPosition;
    private RecyclerView recyclerView;
    private FriendAdapter adapter;
    private List<Friends> friendsList;
    private List<Friends> friendsNew;
    private FriendsClient client;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.friends_list, container, false);
        recyclerView = parentView.findViewById(R.id.recycler_view_friend);
        // listView = parentView.findViewById(R.id.friendsList);
        //errorLayout = parentView.findViewById(R.id.layout_error_friends);
        friendsList = new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new FriendsList.GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        initView();
        return parentView;
    } //aflt el OnCReateView

    private int dpToPx(int i) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, r.getDisplayMetrics()));
    } //aflt el dptopx

    private void initView() {
        client = ApiClient.getClient().create(FriendsClient.class);
        call = client.friends(PreferenceManager.xAuthToken);
        Log.d("homie", "onClick: " + call.toString());
        call.enqueue(new Callback<List<Friends>>() {
            @Override
            public void onResponse(Call<List<Friends>> call, Response<List<Friends>> response) {
                friendsList = response.body();
                if (friendsList == null || friendsList.size() == 0) {
                    //errorLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "No friends", Toast.LENGTH_SHORT).show();
                } else {
                    adapter = new FriendAdapter(getActivity(), friendsList);
                    adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                } // aflt el else bta3t el initial friends
                recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        clickPosition = position;
                        final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getContext());
                        alertDialog.setTitle("Remove friend?");
                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d("homie", "onClick: AddFriend " + clickPosition + " " + friendsList.get(clickPosition).getID());
                                Call<Users> call2 = client.removeFriend(PreferenceManager.xAuthToken, new UserId(friendsList.get(clickPosition).getID()));
                                call2.enqueue(new Callback<Users>() {
                                    @Override
                                    public void onResponse(Call<Users> call, Response<Users> response) {
                                        Users users = response.body();
                                        Log.d("homie", "onResponse: Add Friend response is null? " + (users == null));
                                        Log.d("homie", "onClick: AddFriend " + response.message());

                                        friendsList.remove(clickPosition);
                                        adapter = new FriendAdapter(getActivity(), friendsList);
                                        // adapter.notifyDataSetChanged();
                                        recyclerView.setAdapter(adapter);

                                    }

                                    @Override
                                    public void onFailure(Call<Users> call, Throwable t) {

                                    }
                                }); //aflt el call2.enqueue bta3t el remove Friend


                            } //OnClick el setPositiveButton
                        }); // aflt el setPositiveButton
                        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }); //aflt el setNegativeButton
                        android.app.AlertDialog dialog = alertDialog.create();
                        dialog.show();
                    } //aflt el OnClick bta3 el recyclerview

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                })); // aflt el addOnItemTouchListener


            } //aflt rl response bta3t el initial friend

            @Override
            public void onFailure(Call<List<Friends>> call, Throwable t) {

            }
        }); // aflt el call.enqueue bta3t el friendsList (initial list)


    } //aflt el initview()


    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }


    }


} // aflt el fragment