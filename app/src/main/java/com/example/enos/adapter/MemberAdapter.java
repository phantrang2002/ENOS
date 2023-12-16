package com.example.enos.adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.enos.R;

import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.MemberViewHolder> {
    private List<Member> memberList;

    // Constructor
    public MemberAdapter(List<Member> memberList) {
        this.memberList = memberList;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_ttct, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        Member member = memberList.get(position);

        // Bind data to ViewHolder
        holder.maTSTextView.setText("MaTS: " + member.getMaTS());
        holder.tenTSTextView.setText("TenTS: " + member.getTenTS());
        holder.ketQuaTextView.setText("KetQua: " + member.getKetQua());
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    // ViewHolder class
    static class MemberViewHolder extends RecyclerView.ViewHolder {
        EditText maTSTextView, tenTSTextView, ketQuaTextView;

        MemberViewHolder(View itemView) {
            super(itemView);
            maTSTextView = itemView.findViewById(R.id.maET); // Replace with your actual TextView ID
            tenTSTextView = itemView.findViewById(R.id.tenET); // Replace with your actual TextView ID
            ketQuaTextView = itemView.findViewById(R.id.ketQuaET); // Replace with your actual TextView ID
        }
    }
}
