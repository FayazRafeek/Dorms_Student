package com.example.dormsuser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.dormsuser.Model.Complaint;
import com.example.dormsuser.Model.Student;
import com.example.dormsuser.databinding.ActivityViewComplaintBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ComplaintListActivity extends AppCompatActivity implements ComplaintAdapter.ComplaintAction {

    ActivityViewComplaintBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityViewComplaintBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        EventChangeListner();

        binding.swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                EventChangeListner();
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

        binding.swipe.setRefreshing(true);
        String studentSt = getSharedPreferences("DORM_USER",MODE_PRIVATE).getString("STUDENT_DATA","");
        Student student = new Gson().fromJson(studentSt,Student.class);

        FirebaseFirestore.getInstance().collection("Complaints")
                .whereEqualTo("userId",student.getStudId())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if(error != null){
                            Toast.makeText(ComplaintListActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        binding.swipe.setRefreshing(false);
                        List<Complaint> complaints = new ArrayList<>();
                        for (DocumentSnapshot dc : value)
                            complaints.add(dc.toObject(Complaint.class));

                        updateRecycler_c(complaints);

                    }
                });
    }
    ComplaintAdapter complaintAdapter;
    void updateRecycler_c(List<Complaint> complaints){
        if(complaintAdapter == null){
            complaintAdapter = new ComplaintAdapter(this);
            binding.recycler.setAdapter(complaintAdapter);
            binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        }
       complaintAdapter.updateList(complaints);
    }

    @Override
    public void onCompClick(Complaint complaint) {
        AppSingleton.getINSTANCE().setSelectedComplaint(complaint);
        startActivity(new Intent(this,ComplaintDetailActivity.class));
    }
}