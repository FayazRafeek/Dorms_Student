package com.example.dormsuser;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.example.dormsuser.Model.Leave;
import com.example.dormsuser.databinding.ActivityViewLeaveBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class LeaveListActivity extends AppCompatActivity {

    ActivityViewLeaveBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityViewLeaveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EventChangeListner();

        binding.swipe.setRefreshing(true);
        binding.swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.swipe.setRefreshing(false);
                    }
                },1800);
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void EventChangeListner() {

        FirebaseFirestore.getInstance().collection("Leave").orderBy("leaveId", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable  QuerySnapshot value, @Nullable  FirebaseFirestoreException error) {
                        binding.swipe.setRefreshing(false);
                        if(error != null){
                            Log.e("Firestore Error",error.getMessage());
                            return;
                        } else {
                            List<Leave> list = new ArrayList<>();
                            for (DocumentSnapshot dc:value.getDocuments()){
                                Leave item = dc.toObject(Leave.class);
                                list.add(item);
                            }
                            updateRecycler(list);
                        }
                    }
                });
    }

    LeaveAdapter leaveAdapter;
    void updateRecycler(List<Leave> list){

        if(leaveAdapter == null){
            leaveAdapter = new LeaveAdapter(this);
            binding.leaveRecycler.setAdapter(leaveAdapter);
            binding.leaveRecycler.setLayoutManager(new LinearLayoutManager(this));
        }

        leaveAdapter.updateList(list);
    }
}