package com.example.noso.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.noso.myapplication.Interfaces.ApiClient;
import com.example.noso.myapplication.Interfaces.FriendsClient;
import com.example.noso.myapplication.beans.Friends;
import com.example.noso.myapplication.beans.UserId;
import com.example.noso.myapplication.beans.Users;
import com.example.noso.myapplication.beans.RecyclerTouchListener;



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
        friendsList = new ArrayList<>();

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        initView();

        return parentView;
    }

    private int dpToPx(int i) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, r.getDisplayMetrics()));
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
                    // errorLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "No requests", Toast.LENGTH_SHORT).show();
                } else {

                    adapter = new RequestAdapter(getActivity(), friendsList);
                    // adapter.notifyDataSetChanged();
                    recyclerView.setAdapter(adapter);
                }
                // arrayAdapter.

                // recyclerView.setAdapter(adapter);

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
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                getActivity().findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = getActivity().findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

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






    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_approve_request:
                    Log.d("homie", "onClick: AddFriend " + clickPosition + " " + friendsList.get(clickPosition).getID());
                    Call<Users> call = client.approveFriend(PreferenceManager.xAuthToken, new UserId(friendsList.get(clickPosition).getID()));
                    call.enqueue(new Callback<Users>() {
                        @Override
                        public void onResponse(Call<Users> call, Response<Users> response) {
                            Users user = response.body();
                            friendsList.remove(clickPosition);
                            adapter = new RequestAdapter(getActivity(), friendsList);
                            // adapter.notifyDataSetChanged();
                            recyclerView.setAdapter(adapter);
                            // adapter.remove(names.get(position));
                            Log.d("homie", "onResponse: Add Friend response is null? " + (user == null));
                            Log.d("homie", "onClick: AddFriend " + response.message());
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
                            // adapter.notifyDataSetChanged();
                            recyclerView.setAdapter(adapter);
                            Log.d("homie", "onResponse: Add Friend response is null? " + (user == null));
                            Log.d("homie", "onClick: AddFriend " + response.message());
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

