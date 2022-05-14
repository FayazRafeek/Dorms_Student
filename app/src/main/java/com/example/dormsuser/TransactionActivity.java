package com.example.dormsuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.dormsuser.Model.FeeReceipt;
import com.example.dormsuser.Model.Student;
import com.example.dormsuser.databinding.ActivityTransHistoryBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class TransactionActivity extends AppCompatActivity {

    ActivityTransHistoryBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fetchPayments();

        binding.swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchPayments();
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    void fetchPayments(){
        binding.swipe.setRefreshing(true);

        String studentSt = getSharedPreferences("DORM_USER",MODE_PRIVATE).getString("STUDENT_DATA","");
        Student student = new Gson().fromJson(studentSt,Student.class);

        FirebaseFirestore.getInstance().collection("Admin")
                .document(student.getAdminId()).collection("Fees")
                .whereEqualTo("studId",student.getStudId())
                .orderBy("ts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        binding.swipe.setRefreshing(false);

                        if(task.isSuccessful()){

                            List<FeeReceipt> receipts = new ArrayList<>();
                            for(DocumentSnapshot d : task.getResult())
                                receipts.add(d.toObject(FeeReceipt.class));

                            updateRecycler(receipts);

                        } else {
                            Toast.makeText(TransactionActivity.this, "Failed to get transactions", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    FeesAdapter feesAdapter;
    void updateRecycler(List<FeeReceipt> list){

        if(feesAdapter == null){
            feesAdapter = new FeesAdapter(this);
            binding.recycler.setAdapter(feesAdapter);
            binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        }

        feesAdapter.updateList(list);
    }
}