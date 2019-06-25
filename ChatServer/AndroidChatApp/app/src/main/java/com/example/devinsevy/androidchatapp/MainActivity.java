package com.example.devinsevy.androidchatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import okhttp3.WebSocket;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MainActivity extends AppCompatActivity {
    public static String chatroom = "";
    public static String user = "";
    public Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



    public void sendMessage(View view) {
        EditText chatRoomText = findViewById(R.id.chatText);
        chatroom = chatRoomText.getText().toString();

        EditText userText = findViewById(R.id.userText);
        user = userText.getText().toString();


        startActivity(new Intent(MainActivity.this, ChatRoom.class));
    }

    public static String getChatroom() {
        return chatroom;
    }

    public static String getUser() {
        return user;
    }
}
