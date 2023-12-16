package com.example.enos.chat;
import android.content.Context;

import static java.util.Locale.filter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.example.enos.R;
import com.example.enos.adapter.ChatUserAdapter;
import com.example.enos.model.ChatUserModel;

import java.util.ArrayList;
import java.util.List;

public class ChatUsersActivity extends AppCompatActivity {

    ChatUserAdapter adapter;
    List<ChatUserModel> list;
    FirebaseUser user;

    EditText searchET;
    // Khai báo biến handler ở cấp độ lớp (nên đặt ở đầu lớp)
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_users);

        init();

        fetchUserData();

        clickListener();

        // Lắng nghe sự thay đổi trên EditText để thực hiện chức năng tìm kiếm
        searchET = findViewById(R.id.searchET);
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("Search", "onTextChanged: " + charSequence.toString());
                filter(charSequence.toString());
            }


            @Override
            public void afterTextChanged(Editable editable) {}
        });

    }

    void init() {

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        searchET = findViewById(R.id.searchET);

        list = new ArrayList<>();
        adapter = new ChatUserAdapter(this, list);


        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);

        user = FirebaseAuth.getInstance().getCurrentUser();


    }


    void fetchUserData() {

        CollectionReference reference = FirebaseFirestore.getInstance().collection("Messages");
        reference.whereArrayContains("uid", user.getUid())
                .addSnapshotListener((value, error) -> {

                    if (error != null)
                        return;

                    if (value == null)
                        return;



                    if (value.isEmpty())
                        return;


                    list.clear();
                    for (QueryDocumentSnapshot snapshot : value) {

                        if (snapshot.exists()) {
                            ChatUserModel model = snapshot.toObject(ChatUserModel.class);
                            list.add(model);
                        }

                    }

                    adapter.notifyDataSetChanged();

                });


    }


    void clickListener() {

        adapter.OnStartChat((position, uids, chatID) -> {

            String oppositeUID;
            if (!uids.get(0).equalsIgnoreCase(user.getUid())) {
                oppositeUID = uids.get(0);
            } else {
                oppositeUID = uids.get(1);
            }

            Intent intent = new Intent(ChatUsersActivity.this, ChatActivity.class);
            intent.putExtra("uid", oppositeUID);
            intent.putExtra("id", chatID);



            startActivity(intent);


        });

    }


    private void filter(String text) {
        List<ChatUserModel> filteredUsers = new ArrayList<>();

        if (list != null && text != null) {
            String searchTextLowerCase = text.toLowerCase();

            for (ChatUserModel user : list) {
                if (user != null) {
                    getNameFromFirestore(user, this, name -> {
                        if (name != null && name.toLowerCase().contains(searchTextLowerCase)) {
                            filteredUsers.add(user);
                        }

                        // Cập nhật danh sách khi đã kiểm tra xong tất cả các user
                        if (user.equals(list.get(list.size() - 1))) {
                            runOnUiThread(() -> updateAdapter(filteredUsers));
                        }
                    });
                }
            }
        } else {
            // Nếu chuỗi tìm kiếm là rỗng, hiển thị lại toàn bộ danh sách
            runOnUiThread(() -> updateAdapter(new ArrayList<>(list)));
        }
    }



    private void updateAdapter(List<ChatUserModel> updatedList) {
        adapter.updateList(updatedList);
        runOnUiThread(() -> adapter.notifyDataSetChanged());
    }







    private void getNameFromFirestore(ChatUserModel user, Context context, NameCallback callback) {
        String oppositeUID = getOppositeUID(user);
        if (oppositeUID != null) {
            FirebaseFirestore.getInstance().collection("Users").document(oppositeUID)
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            String name = snapshot.getString("name");
                            if (name != null) {
                                callback.onNameReceived(name);
                            }
                        } else {
                            assert task.getException() != null;
                            Toast.makeText(context, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Định nghĩa một interface callback
    interface NameCallback {
        void onNameReceived(String name);
    }

    private String getOppositeUID(ChatUserModel user) {
        List<String> uids = user.getUid();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (uids != null && firebaseUser != null) {
            if (!uids.get(0).equalsIgnoreCase(firebaseUser.getUid())) {
                return uids.get(0);
            } else {
                return uids.get(1);
            }
        }
        return null;
    }
}


