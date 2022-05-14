package com.example.dormsuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.dormsuser.Model.Leave;
import com.example.dormsuser.Model.Student;
import com.example.dormsuser.databinding.ActivityLeaveApplyBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;


import java.util.Calendar;

public class NewLeaveActivity extends AppCompatActivity{

    private DatePickerDialog picker;
    ActivityLeaveApplyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLeaveApplyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.fromInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar calendar=Calendar.getInstance();
                int day=calendar.get(Calendar.DAY_OF_MONTH);
                int month=calendar.get(Calendar.MONTH);
                int year=calendar.get(Calendar.YEAR);

                picker= new DatePickerDialog(NewLeaveActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        fromCalender = Calendar.getInstance();
                        fromCalender.set(year,month,dayOfMonth);
                        month++;
                        binding.fromInput.setText(dayOfMonth+ "/" +month+"/"+year);
                    }
                },year,month,day);

                picker.getDatePicker().setMinDate(System.currentTimeMillis());
                picker.show();
            }
        });

        binding.toInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(fromCalender == null){
                    Toast.makeText(NewLeaveActivity.this, "Choose a starting date", Toast.LENGTH_SHORT).show();
                    return;
                }

                picker= new DatePickerDialog(NewLeaveActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month++;
                        binding.toInput.setText(dayOfMonth+ "/" +month+"/"+year);
                    }
                },fromCalender.get(Calendar.YEAR),fromCalender.get(Calendar.MONTH),fromCalender.get(Calendar.DAY_OF_MONTH));
                picker.getDatePicker().setMinDate(fromCalender.getTimeInMillis());
                picker.show();
            }
        });

        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gatherData();
            }
        });

    }


    Calendar fromCalender;


    void gatherData(){

        String fromDate = binding.fromInput.getText().toString();
        String toDate = binding.toInput.getText().toString();
        String reason = binding.reasonInput.getText().toString();

        if(fromDate.isEmpty() || toDate.isEmpty() || reason.isEmpty()){
            Toast.makeText(this, "Enter required data", Toast.LENGTH_SHORT).show();
            return;
        }

        String leaveId = String.valueOf(System.currentTimeMillis());
        String studentSt = getSharedPreferences("DORM_USER",MODE_PRIVATE).getString("STUDENT_DATA","");
        Student student = new Gson().fromJson(studentSt,Student.class);

        if(student == null) {
            showToast("Failed to get student data");
            return;
        }

        Leave leave = new Leave(leaveId,student.getStudId(),student.getAdminId(),student.getFull_name(),fromDate,toDate,reason,"SUBMITTED",null);

        binding.progress.setVisibility(View.VISIBLE);
        FirebaseFirestore.getInstance()
                .collection("Leave")
                .document(leave.getLeaveId())
                .set(leave)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        binding.progress.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            showToast("Leave request submitted successfully");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            },2000);
                        } else showToast(task.getException().getMessage());
                    }
                });
    }



    void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}