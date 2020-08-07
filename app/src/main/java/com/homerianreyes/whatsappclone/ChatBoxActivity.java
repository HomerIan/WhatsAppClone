package com.homerianreyes.whatsappclone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ChatBoxActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView listViewChatBox;
    private Button btnSendChat;
    private ArrayList<String> chatList;
    private ArrayAdapter arrayAdapter;
    private String selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);

        selectedUser = getIntent().getStringExtra("selectedUser");

        getSupportActionBar().setTitle(selectedUser + "\'s chat");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnSendChat = findViewById(R.id.btnSendChat);
        btnSendChat.setOnClickListener(this);

        listViewChatBox = findViewById(R.id.listViewChatBox);
        chatList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(ChatBoxActivity.this, android.R.layout.simple_list_item_1, chatList);
        listViewChatBox.setAdapter(arrayAdapter);

        try {

            ParseQuery<ParseObject> firstUserChatQuery = ParseQuery.getQuery("Chat");
            ParseQuery<ParseObject> secondUserChatQuery = ParseQuery.getQuery("Chat");

            firstUserChatQuery.whereEqualTo("Sender", ParseUser.getCurrentUser().getUsername());
            firstUserChatQuery.whereEqualTo("Reciever", selectedUser);

            secondUserChatQuery.whereEqualTo("Sender", selectedUser);
            secondUserChatQuery.whereEqualTo("Reciever", ParseUser.getCurrentUser().getUsername());

            ArrayList<ParseQuery<ParseObject>> allQueries = new ArrayList<>();
            allQueries.add(firstUserChatQuery);
            allQueries.add(secondUserChatQuery);

            ParseQuery<ParseObject> myQuery = ParseQuery.or(allQueries);
            myQuery.orderByAscending("createdAt");

            myQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (objects.size() > 0 && e == null) {

                        for (ParseObject chatObject : objects) {

                            String message = chatObject.get("Message") + "";
                            if (chatObject.get("Sender").equals(ParseUser.getCurrentUser().getUsername())) {
                                message = ParseUser.getCurrentUser().getUsername() + ": " + message;
                            }

                            if (chatObject.get("Sender").equals(selectedUser)) {
                                message = selectedUser + ": " + message;
                            }
                            chatList.add(message);
                            arrayAdapter.notifyDataSetChanged();
                        }
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }//onCreate

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btnSendChat:

                final EditText edtMessage = findViewById(R.id.edtChat);

                ParseObject chat = new ParseObject("Chat");
                chat.put("Sender", ParseUser.getCurrentUser().getUsername());
                chat.put("Reciever", selectedUser);
                chat.put("Message", edtMessage.getText().toString());
                chat.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                        if (e == null) {
                            chatList.add(ParseUser.getCurrentUser().getUsername() + ": " + edtMessage.getText().toString());
                            arrayAdapter.notifyDataSetChanged();
                            edtMessage.getText().clear();
                        }
                    }
                });

                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}