package com.homerianreyes.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

public class SocialMediaActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private ArrayAdapter arrayAdapter;
    private ArrayList<String> arrayList;
    private SwipeRefreshLayout swipeRefreshLayoutContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_media);

        FancyToast.makeText(SocialMediaActivity.this, "Welcome " + ParseUser.getCurrentUser().getUsername() + "!", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS,false).show();

        swipeRefreshLayoutContainer = findViewById(R.id.swipeRefreshLayoutContainer);
        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(SocialMediaActivity.this, android.R.layout.simple_list_item_1, arrayList);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        //try catch cuz there might be no users and the app will crush
        try {
            //get users in parse server
            ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
            //condition: get all username except current user
            parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
            //find data
            parseQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> users, ParseException e) {

                    if (e == null) {

                        if (users.size() > 0) {

                            for (ParseUser user : users) {

                                arrayList.add(user.getUsername());
                            }
                            listView.setAdapter(arrayAdapter);
                        }
                    }
                    progressDialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        swipeRefreshLayoutContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                try {

                    ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
                    parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
                    parseQuery.whereNotContainedIn("username", arrayList);
                    parseQuery.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> users, ParseException e) {

                            if (users.size() > 0) {

                                if (e == null) {

                                    for (ParseUser user : users) {

                                        arrayList.add(user.getUsername());
                                    }
                                    arrayAdapter.notifyDataSetChanged();
                                    if (swipeRefreshLayoutContainer.isRefreshing()) {
                                        swipeRefreshLayoutContainer.setRefreshing(false);
                                    }
                                }
                            } else {
                                if (swipeRefreshLayoutContainer.isRefreshing()){
                                    swipeRefreshLayoutContainer.setRefreshing(false);
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }//onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }//Menu

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.logoutUserItem) {
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {

                    if (e == null) {
                        transitionToLogInActivity();
                    } else {
                        e.printStackTrace();
                    }
                }
            });
            FancyToast.makeText(SocialMediaActivity.this, ParseUser.getCurrentUser().getUsername() + " is logged out.", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS,false).show();
        }
        return super.onOptionsItemSelected(item);
    }//Menu Options

    private void transitionToLogInActivity() {
        Intent intent = new Intent(SocialMediaActivity.this, LogInActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(SocialMediaActivity.this, ChatBoxActivity.class);
        intent.putExtra("selectedUser", arrayList.get(position));
        startActivity(intent);
    }
}