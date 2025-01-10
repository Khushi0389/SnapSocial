package com.example.bobatap;
import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.bobatap.ChatAdapter;
import com.example.bobatap.ChatModel;
import com.example.bobatap.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    FirebaseUser user;
    CircleImageView imageView;
    TextView name, status;
    EditText chatET;
    ImageView sendBtn;
    RecyclerView recyclerView;

    ChatAdapter adapter;
    List<ChatModel> list;

    String chatID;

    // Create a BroadcastReceiver for receiving new messages.
    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Retrieve the new message from the intent.
            String newMessage = intent.getStringExtra("message");

            // Update your chat UI with the new message.
            updateChatUI(newMessage);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();

        loadUserData();

        loadMessages();

        sendBtn.setOnClickListener(v -> {
            String message = chatET.getText().toString().trim();

            if (message.isEmpty()) {
                return;
            }

            CollectionReference reference = FirebaseFirestore.getInstance().collection("Messages");

            Map<String, Object> map = new HashMap<>();
            map.put("lastMessage", message);
            map.put("time", FieldValue.serverTimestamp());
            reference.document(chatID).update(map);

            String messageID = reference
                    .document(chatID)
                    .collection("Messages")
                    .document()
                    .getId();

            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("id", messageID);
            messageMap.put("message", message);
            messageMap.put("senderID", user.getUid());
            messageMap.put("time", FieldValue.serverTimestamp());

            reference.document(chatID).collection("Messages").document(messageID).set(messageMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            chatET.setText("");
                        } else {
                            Toast.makeText(ChatActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    void init() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        imageView = findViewById(R.id.profileImage);
        name = findViewById(R.id.nameTV);
        status = findViewById(R.id.statusTV);
        chatET = findViewById(R.id.chatET);
        sendBtn = findViewById(R.id.sendBtn);
        recyclerView = findViewById(R.id.recyclerView);
        list = new ArrayList<>();
        adapter = new ChatAdapter(this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    void loadUserData() {
        String oppositeUID = getIntent().getStringExtra("uid");

        FirebaseFirestore.getInstance().collection("Users").document(oppositeUID)
                .addSnapshotListener((value, error) -> {
                    if (error != null)
                        return;

                    if (value == null)
                        return;

                    if (!value.exists())
                        return;

                    boolean isOnline = Boolean.TRUE.equals(value.getBoolean("online"));
                    status.setText(isOnline ? "Online" : "Offline");

                    Glide.with(getApplicationContext()).load(value.getString("profileImage")).into(imageView);
                    name.setText(value.getString("name"));
                });
    }

    void loadMessages() {
        chatID = getIntent().getStringExtra("id");

        CollectionReference reference = FirebaseFirestore.getInstance()
                .collection("Messages")
                .document(chatID)
                .collection("Messages");

        reference
                .orderBy("time", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null)
                        return;

                    if (value == null || value.isEmpty())
                        return;

                    list.clear();
                    for (QueryDocumentSnapshot snapshot : value) {
                        ChatModel model = snapshot.toObject(ChatModel.class);
                        list.add(model);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    // Update your chat UI with the new message.
    private void updateChatUI(String newMessage) {
        // Implement the logic to display the new message in your chat interface.
        // You may need to add the new message to your list of messages and refresh the adapter.
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the BroadcastReceiver to receive new messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, new IntentFilter("new_message"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the BroadcastReceiver to avoid memory leaks.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
    }
}
