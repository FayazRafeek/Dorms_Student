package com.example.dormsuser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dormsuser.Model.Leave;
import com.example.dormsuser.databinding.RecvViewLeaveBinding;
import com.google.firebase.database.annotations.NotNull;


import java.util.ArrayList;
import java.util.List;

public class LeaveAdapter extends RecyclerView.Adapter<LeaveAdapter.ViewHolder>{

    Context context;
    List<Leave>list =  new ArrayList();

    public LeaveAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public LeaveAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        RecvViewLeaveBinding binding = RecvViewLeaveBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull LeaveAdapter.ViewHolder holder, int position) {

        Leave leave= list.get(position);

        holder.binding.status.setText(leave.getStatus());
        switch (leave.getStatus()){
            case "SUBMITTED" :
                holder.binding.statusParent.setBackgroundColor(ContextCompat.getColor(context,R.color.grey_10));
                holder.binding.status.setTextColor(ContextCompat.getColor(context,R.color.white));
                break;
            case "VIEWED" :
                holder.binding.statusParent.setBackgroundColor(ContextCompat.getColor(context,R.color.orange));
                holder.binding.status.setTextColor(ContextCompat.getColor(context,R.color.black));
                break;
            case "APPROVED" :
                holder.binding.statusParent.setBackgroundColor(ContextCompat.getColor(context,R.color.green));
                holder.binding.status.setTextColor(ContextCompat.getColor(context,R.color.black));
                break;
            case "REJECTED" :
                holder.binding.statusParent.setBackgroundColor(ContextCompat.getColor(context,R.color.peach));
                holder.binding.status.setTextColor(ContextCompat.getColor(context,R.color.black));
                break;
        }

        holder.binding.reason.setText(leave.getReason().trim());

        holder.binding.fromDate.setText(leave.getFromDate());
        holder.binding.toDate.setText(leave.getToDate());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public void updateList(List<Leave> lists){
        this.list = lists;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        RecvViewLeaveBinding binding;

        public ViewHolder(@NonNull @NotNull RecvViewLeaveBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }
}
