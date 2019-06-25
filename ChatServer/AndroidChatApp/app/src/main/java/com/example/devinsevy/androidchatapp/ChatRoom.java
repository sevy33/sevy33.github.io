package com.example.devinsevy.androidchatapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class ChatRoom extends AppCompatActivity {
    private String room;
    private String username;
    public Button button;
    private WebSocket ws;
    private OkHttpClient client;
    private Request request;
    private EchoWebSocketListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        room = MainActivity.getChatroom();
        username = MainActivity.getUser();
        TextView textView = findViewById(R.id.roomMsg);
        textView.setText("Joined " + room);

        client = new OkHttpClient();

        connectWebSocket();

    }

    private void connectWebSocket() {
        request = new Request.Builder().url("ws://10.0.2.2:8080").build();
        listener = new EchoWebSocketListener();
        ws = client.newWebSocket(request, listener);
        //client.dispatcher().executorService().shutdown();

        ws.send("join " + room + " " + username);
    }

    public void sendChat(View view) {
        EditText sendText = findViewById(R.id.sendText);
        String chatMsg = sendText.getText().toString();

        String sendMsg = "post " + room + " " + username + " " + chatMsg;

        listener = new EchoWebSocketListener();

        ws.send(sendMsg);
    }

    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            System.out.println(response);
        }
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            System.out.println(text);
            if(text.contains("Joined: ")) return;
            View linearLayout =  findViewById(R.id.info);
            TextView valueTV = new TextView(ChatRoom.this);
            valueTV.setText(text);
            valueTV.setTextColor(Color.parseColor("#ff0000"));
            valueTV.setTextSize(24);
            valueTV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams                          .WRAP_CONTENT));

            ((LinearLayout) linearLayout).addView(valueTV);
        }
        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
        }
        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            Log.d("Error", "Error : " + t.getMessage());
        }
    }
}
