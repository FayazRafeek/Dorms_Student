package com.example.dormsuser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.dormsuser.Model.FeeReceipt;
import com.example.dormsuser.Model.MenuSelection;
import com.example.dormsuser.Model.Room;
import com.example.dormsuser.Model.Student;
import com.example.dormsuser.databinding.ActivityFeesBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class FeesActivity extends AppCompatActivity {

    ActivityFeesBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFeesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setUpSpinners();
        binding.amtParent.setVisibility(View.GONE);

        binding.payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPayment();
            }
        });


    }

    String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};
    String[] year = {"2020","2021","2022","2023","2024","2025","2026"};
    ArrayList<String> monthAr = new ArrayList<>(Arrays.asList(months));
    ArrayList<String> yearAr = new ArrayList<>(Arrays.asList(year));

    String selectedYear = "";
    String selectedMonth = "";

    void setUpSpinners(){
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this,R.layout.drop_layout_item,months);
        binding.month.setAdapter(monthAdapter);
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this,R.layout.drop_layout_item,year);
        binding.year.setAdapter(yearAdapter);

        binding.year.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedYear = year[i];
                refetchData();
            }
        });

        binding.month.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedMonth = months[i];
                refetchData();
            }
        });
    }


    Room room; List<MenuSelection> menuSelections;
    void refetchData(){

        if(selectedYear == null || selectedYear.isEmpty() || selectedMonth == null || selectedMonth.isEmpty()){
            resetUi();
            return;
        }

        binding.progress.setVisibility(View.VISIBLE);
        binding.amtParent.setVisibility(View.GONE);

        String studentSt = getSharedPreferences("DORM_USER",MODE_PRIVATE).getString("STUDENT_DATA","");
        Student student = new Gson().fromJson(studentSt,Student.class);
        String roomId = student.getRoom();

        if(roomId == null){
            Toast.makeText(this, "You are not added to any room!", Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.YEAR, Integer.valueOf(selectedYear));
        startCal.set(Calendar.MONTH,monthAr.indexOf(selectedMonth));
        startCal.set(Calendar.DAY_OF_MONTH,1);
        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.YEAR, Integer.valueOf(selectedYear));
        endCal.set(Calendar.MONTH,monthAr.indexOf(selectedMonth));
        endCal.set(Calendar.DAY_OF_MONTH,startCal.getActualMaximum(Calendar.DAY_OF_MONTH));

        FirebaseFirestore.getInstance().collection("Admin").document(student.getAdminId())
                .collection("Rooms")
                .document(roomId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful()){

                            room = task.getResult().toObject(Room.class);
                            FirebaseFirestore.getInstance().collection("Student").document(student.getStudId())
                                    .collection("Selection")
                                    .whereGreaterThanOrEqualTo("menuDate",startCal.getTimeInMillis())
                                    .whereLessThanOrEqualTo("menuDate",endCal.getTimeInMillis())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                menuSelections = new ArrayList<>();
                                                for (DocumentSnapshot d : task.getResult())
                                                    menuSelections.add(d.toObject(MenuSelection.class));
                                            } else {
                                                Toast.makeText(FeesActivity.this, "No Mess data found", Toast.LENGTH_SHORT).show();
                                            }

                                            updateData();
                                        }
                                    });

                        } else {
                            Toast.makeText(FeesActivity.this, "Failed to get room Data", Toast.LENGTH_SHORT).show();
                            binding.progress.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private static final String TAG = "333";

    int rAmt=0,mAmt=0,tAmt=0;
    void updateData(){

        binding.progress.setVisibility(View.GONE);
        if(room == null){
            Toast.makeText(FeesActivity.this, "Failed to get room Data", Toast.LENGTH_SHORT).show();
            binding.amtParent.setVisibility(View.GONE);
            return;
        }

        binding.ramount.setText("₹" + room.getRent());

        int total = 0;
        if(menuSelections != null && !menuSelections.isEmpty())
            for (MenuSelection s : menuSelections){
                int mTotal = 0;
                for (FoodItem f : s.getFoodItems())
                    mTotal += Integer.valueOf(f.getPrice());
                total += mTotal;
            }

        binding.mamount.setText("₹" + total);
        int allTotal = Integer.valueOf(room.getRent()) + total;
        binding.tamount.setText("₹" + allTotal);
        binding.amtParent.setVisibility(View.VISIBLE);

        rAmt = Integer.valueOf(room.getRent());
        mAmt = Integer.valueOf(total);
        tAmt = allTotal;


    }

    void startPayment(){

        binding.payParent.setVisibility(View.VISIBLE);
        String studentSt = getSharedPreferences("DORM_USER",MODE_PRIVATE).getString("STUDENT_DATA","");
        Student student = new Gson().fromJson(studentSt,Student.class);
        String id = System.currentTimeMillis() + student.getStudId().substring(0,3) + room.getRoomId().substring(0,3);
        FeeReceipt receipt = new FeeReceipt(id,student.getStudId(),student.getFull_name(),student.getAdminId(),selectedMonth,selectedYear, room.getRoomId(),rAmt,mAmt,tAmt,String.valueOf(System.currentTimeMillis()));
        FirebaseFirestore.getInstance().collection("Admin")
                .document(student.getAdminId())
                .collection("Fees")
                .document(receipt.getPayId())
                .set(receipt)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            binding.statusTxt.setText("Completing payment...");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    binding.payProgress.setVisibility(View.GONE);
                                    binding.statusTxt.setText("Payment successfull");
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(new Intent(FeesActivity.this,TransactionActivity.class));
                                            finish();
                                        }
                                    },800);
                                }
                            },1500);
                        } else {
                            binding.payProgress.setVisibility(View.GONE);
                            binding.statusTxt.setText("Payment Failed\n" + task.getException().getMessage());
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                   binding.payParent.setVisibility(View.GONE);
                                }
                            },1500);
                        }
                    }
                });

    }

    void resetUi(){
        binding.amtParent.setVisibility(View.GONE);
        binding.progress.setVisibility(View.GONE);
    }
}