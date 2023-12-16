package com.example.enos.adapter;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.enos.R;
import com.example.enos.ReplacerActivity;
import com.example.enos.model.HomeModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeHolder> {

    private final List<HomeModel> list;
    Activity context;
    OnPressed onPressed;
    private MutableLiveData<Integer> commentCount;

    public HomeAdapter(List<HomeModel> list, Activity context) {
        this.list = list;
        this.context = context;
        this.commentCount = new MutableLiveData<>();;
    }

    public void setCommentCountLiveData(MutableLiveData<Integer> commentCount) {
        this.commentCount = commentCount;
    }

    @NonNull
    @Override
    public HomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_items, parent, false);
        return new HomeHolder(view, commentCount);
    }

    public void updateList(List<HomeModel> newList) {
        for (HomeModel newItem : newList) {
            int existingIndex = findItemIndexById(newItem.getId());
            if (existingIndex != -1) {
                // Update existing item
                list.set(existingIndex, newItem);
            } else {
                // Add new item
                list.add(newItem);
            }
        }
        notifyDataSetChanged();
    }

    private int findItemIndexById(String itemId) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(itemId)) {
                return i;
            }
        }
        return -1;
    }



    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull HomeHolder holder, int position) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        holder.userNameTv.setText(list.get(position).getName());
        holder.timeTv.setText("" + list.get(position).getTimestamp());

        List<String> likeList = list.get(position).getLikes();

        int count = likeList.size();

        if (count == 0) {
            holder.likeCountTv.setText("0 Like");
        } else if (count == 1) {
            holder.likeCountTv.setText(count + " Like");
        } else {
            holder.likeCountTv.setText(count + " Likes");
        }

        if (user != null) {
            // Thực hiện các hành động với user
            //check if already like
            //assert user != null;

            holder.likeCheckBox.setChecked(likeList.contains(user.getUid()));

            holder.descriptionTv.setText(list.get(position).getDescription());

            Random random = new Random();

            int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));

            /*
            Glide.with(context.getApplicationContext())
                    .load(list.get(position).getProfileImage())
                    .placeholder(R.drawable.ic_person)
                    .timeout(6500)
                    .into(holder.profileImage);*/
            fetchImageUrl(list.get(position).getUid(), holder);

            Glide.with(context.getApplicationContext())
                    .load(list.get(position).getImageUrl())
                    .placeholder(new ColorDrawable(color))
                    .timeout(7000)
                    .into(holder.imageView);

            holder.clickListener(position,
                    list.get(position).getId(),
                    list.get(position).getName(),
                    list.get(position).getUid(),
                    list.get(position).getLikes(),
                    list.get(position).getImageUrl()
            );

        } else {
            // Xử lý khi user là null

        }



    }

    void fetchImageUrl(String uid, HomeHolder holder) {
        FirebaseFirestore.getInstance().collection("Users").document(uid)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot snapshot = task.getResult();
                        if (snapshot != null) {
                            String profileImageUrl = snapshot.getString("profileImage");
                            Glide.with(context.getApplicationContext())
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.ic_person)
                                    .into(holder.profileImage);
                        } else {
                            Log.e("fetchImageUrl", "DocumentSnapshot is null!");
                        }
                    } else {
                        assert task.getException() != null;
                        Log.e("fetchImageUrl", "Error: " + task.getException().getMessage());
                        Toast.makeText(context, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public void OnPressed(OnPressed onPressed) {
        this.onPressed = onPressed;
    }

    public interface OnPressed {
        void onLiked(int position, String id, String uid, List<String> likeList, boolean isChecked);

       // void setCommentCount(TextView textView);
    }




    class HomeHolder extends RecyclerView.ViewHolder {


        private final CircleImageView profileImage;
        private final TextView userNameTv;
        private final TextView timeTv;
        private final TextView likeCountTv;
        private final TextView descriptionTv;
        private final ImageView imageView;
        private final CheckBox likeCheckBox;
        private final ImageButton commentBtn;
        private final ImageButton shareBtn;


        public HomeHolder(@NonNull View itemView,MutableLiveData<Integer> commentCount) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImage);
            imageView = itemView.findViewById(R.id.imageView);
            userNameTv = itemView.findViewById(R.id.nameTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            likeCountTv = itemView.findViewById(R.id.likeCountTv);
            likeCheckBox = itemView.findViewById(R.id.likeBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);
            descriptionTv = itemView.findViewById(R.id.descTv);

            TextView commentTV = itemView.findViewById(R.id.commentTV);

           //onPressed.setCommentCount(commentTV);

            // You can directly observe the commentCount here

            commentCount.observe((LifecycleOwner) context, count -> {
                assert count != null;
                if (count == 0) {
                    commentTV.setVisibility(View.GONE);
                } else {
                    commentTV.setVisibility(View.VISIBLE);
                   // String commentText = "See all " + count + " comments";
                   // commentTV.setText(commentText);
                }
            });

        }

        public void clickListener(final int position, final String id, String name, final String uid, final List<String> likes, final String imageUrl) {

            commentBtn.setOnClickListener(v -> {

                Intent intent = new Intent(context, ReplacerActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("uid", uid);
                intent.putExtra("isComment", true);

                context.startActivity(intent);

            });

            likeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> onPressed.onLiked(position, id, uid, likes, isChecked));

            shareBtn.setOnClickListener(v -> {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, imageUrl);
                intent.setType("text/*");
                context.startActivity(Intent.createChooser(intent, "Share link using..."));

            });


        }
    }

}