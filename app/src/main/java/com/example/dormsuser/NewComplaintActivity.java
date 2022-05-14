package com.example.dormsuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dormsuser.Model.Complaint;
import com.example.dormsuser.Model.Student;
import com.example.dormsuser.databinding.ActivityComplaintBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

public class NewComplaintActivity extends AppCompatActivity {

    ActivityComplaintBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityComplaintBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               submitComp();
            }
        });

    }

    void submitComp(){

        String comp = binding.comp2.getText().toString();

        if(comp == null || comp.isEmpty()){
            Toast.makeText(NewComplaintActivity.this, "Please enter...", Toast.LENGTH_SHORT).show();
            return;
        }

        String compId = String.valueOf(System.currentTimeMillis());
        String studentSt = getSharedPreferences("DORM_USER",MODE_PRIVATE).getString("STUDENT_DATA","");
        Student student = new Gson().fromJson(studentSt,Student.class);

        if(student == null) {
            showToast("Failed to get student data");
            return;
        }

        Complaint add_complaint=new Complaint(compId,student.getStudId(),student.getAdminId(),comp,"SUBMITTED",compId,student.getFull_name(),student.getProfileUrl(),student.getCollege());

        FirebaseFirestore.getInstance().collection("Complaints")
                .document(add_complaint.getComplaintId())
                .set(add_complaint)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(NewComplaintActivity.this, " Complaint Posted Successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else
                            Toast.makeText(NewComplaintActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}