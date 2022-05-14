package com.example.dormsuser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dormsuser.Model.Complaint;
import com.example.dormsuser.databinding.RecvViewComplaintBinding;
import com.google.firebase.database.annotations.NotNull;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ViewHolder> {

    List<Complaint> complaints =  new ArrayList();
    ComplaintAction listner;

    public ComplaintAdapter(ComplaintAction listner) {
        this.listner = listner;
    }


    @NonNull
    @NotNull
    @Override
    public ComplaintAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        RecvViewComplaintBinding binding = RecvViewComplaintBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull ComplaintAdapter.ViewHolder holder, int position) {

        Complaint add_complaint=complaints.get(position);
        String msg = add_complaint.getComplaint();

        if(msg != null && msg.length() > 25)
            msg = msg.substring(0,25);
        holder.binding.vComplaintTxt.setText(msg.trim());
        holder.binding.status.setText(add_complaint.getStatus());

        Date date = new Date();
        date.setTime(Long.parseLong(add_complaint.getTimestamp()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
        String time = dateFormat.format(date);
        holder.binding.timeTxt.setText(time);

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listner.onCompClick(add_complaint);
            }
        });
    }

    @Override
    public int getItemCount() {
        return complaints.size();
    }
    public void updateList(List<Complaint> complaints){
        this.complaints = complaints;
        notifyDataSetChanged();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder{

        RecvViewComplaintBinding binding;

        public ViewHolder(@NonNull @NotNull RecvViewComplaintBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }


    public interface ComplaintAction {
        void onCompClick(Complaint complaint);
    }
}
